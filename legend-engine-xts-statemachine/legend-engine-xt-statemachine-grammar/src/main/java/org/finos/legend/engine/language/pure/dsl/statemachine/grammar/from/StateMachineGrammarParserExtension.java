// Copyright 2023 Goldman Sachs
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.finos.legend.engine.language.pure.dsl.statemachine.grammar.from;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.eclipse.collections.api.list.MutableList;
import org.finos.legend.engine.language.pure.grammar.from.ParserErrorListener;
import org.finos.legend.engine.language.pure.grammar.from.PureGrammarParserContext;
import org.finos.legend.engine.language.pure.grammar.from.PureGrammarParserUtility;
import org.finos.legend.engine.language.pure.grammar.from.SectionSourceCode;
import org.finos.legend.engine.language.pure.grammar.from.SourceCodeParserInfo;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.statemachine.StateMachineLexerGrammar;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.statemachine.StateMachineParserGrammar;
import org.finos.legend.engine.language.pure.grammar.from.extension.PureGrammarParserExtension;
import org.finos.legend.engine.language.pure.grammar.from.extension.SectionParser;
import org.finos.legend.engine.protocol.pure.m3.PackageableElement;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.section.ImportAwareCodeSection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.section.Section;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.statemachine.StateMachine;

import java.util.Collections;
import java.util.function.Consumer;

public class StateMachineGrammarParserExtension implements PureGrammarParserExtension
{
    public static final String NAME = "StateMachine";

    @Override
    public MutableList<String> group()
    {
        return org.eclipse.collections.impl.factory.Lists.mutable.with("PackageableElement", "StateMachine");
    }

    @Override
    public Iterable<? extends SectionParser> getExtraSectionParsers()
    {
        return Collections.singletonList(SectionParser.newParser(NAME, StateMachineGrammarParserExtension::parseSection));
    }

    private static Section parseSection(SectionSourceCode sectionSourceCode, Consumer<PackageableElement> elementConsumer, PureGrammarParserContext context)
    {
        SourceCodeParserInfo parserInfo = getStateMachineParserInfo(sectionSourceCode);
        ImportAwareCodeSection section = new ImportAwareCodeSection();
        section.parserName = sectionSourceCode.sectionType;
        section.sourceInformation = parserInfo.sourceInformation;

        StateMachineParseTreeWalker walker = new StateMachineParseTreeWalker(parserInfo.walkerSourceInformation, context);
        StateMachineParserGrammar.DefinitionContext definitionContext = (StateMachineParserGrammar.DefinitionContext) parserInfo.rootContext;
        
        // Process imports
        for (StateMachineParserGrammar.ImportStatementContext importCtx : definitionContext.imports().importStatement())
        {
            section.imports.add(PureGrammarParserUtility.fromPath(importCtx.packagePath().identifier()));
        }
        
        // Process state machines
        for (StateMachineParserGrammar.ElementDefinitionContext elementCtx : definitionContext.elementDefinition())
        {
            StateMachine stateMachine = walker.visitStateMachine(elementCtx.stateMachine());
            section.elements.add(stateMachine.getPath());
            elementConsumer.accept(stateMachine);
        }

        return section;
    }

    private static SourceCodeParserInfo getStateMachineParserInfo(SectionSourceCode sectionSourceCode)
    {
        CharStream input = CharStreams.fromString(sectionSourceCode.code);
        ParserErrorListener errorListener = new ParserErrorListener(sectionSourceCode.walkerSourceInformation, StateMachineLexerGrammar.VOCABULARY);
        StateMachineLexerGrammar lexer = new StateMachineLexerGrammar(input);
        lexer.addErrorListener(errorListener);
        StateMachineParserGrammar parser = new StateMachineParserGrammar(new CommonTokenStream(lexer));
        parser.addErrorListener(errorListener);
        return new SourceCodeParserInfo(sectionSourceCode.code, input, sectionSourceCode.sourceInformation, sectionSourceCode.walkerSourceInformation, lexer, parser, parser.definition());
    }
}
