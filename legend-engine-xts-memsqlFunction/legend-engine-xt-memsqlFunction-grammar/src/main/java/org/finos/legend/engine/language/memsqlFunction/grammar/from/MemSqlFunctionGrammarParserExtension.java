//  Copyright 2023 Goldman Sachs
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package org.finos.legend.engine.language.memsqlFunction.grammar.from;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.eclipse.collections.impl.factory.Lists;
import org.finos.legend.engine.language.pure.grammar.from.ParserErrorListener;
import org.finos.legend.engine.language.pure.grammar.from.PureGrammarParserContext;
import org.finos.legend.engine.language.pure.grammar.from.SectionSourceCode;
import org.finos.legend.engine.language.pure.grammar.from.SourceCodeParserInfo;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.MemSqlFunctionLexerGrammar;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.MemSqlFunctionParserGrammar;
import org.finos.legend.engine.language.pure.grammar.from.extension.PureGrammarParserExtension;
import org.finos.legend.engine.language.pure.grammar.from.extension.SectionParser;
import org.finos.legend.engine.protocol.pure.m3.PackageableElement;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.section.DefaultCodeSection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.section.Section;

import java.util.function.Consumer;

public class MemSqlFunctionGrammarParserExtension implements PureGrammarParserExtension
{
    public static final String NAME = "MemSql";

    @Override
    public Iterable<? extends SectionParser> getExtraSectionParsers()
    {
        return Lists.fixedSize.of(SectionParser.newParser(NAME, MemSqlFunctionGrammarParserExtension::parseSection));
    }

    private static Section parseSection(SectionSourceCode sectionSourceCode, Consumer<PackageableElement> elementConsumer, PureGrammarParserContext context)
    {
        SourceCodeParserInfo parserInfo = getBigQueryFunctionInfo(sectionSourceCode);
        DefaultCodeSection section = new DefaultCodeSection();
        section.parserName = sectionSourceCode.sectionType;
        section.sourceInformation = parserInfo.sourceInformation;

        MemSqlFunctionTreeWalker walker = new MemSqlFunctionTreeWalker(parserInfo.input, parserInfo.walkerSourceInformation, elementConsumer, section);
        walker.visit((MemSqlFunctionParserGrammar.DefinitionContext) parserInfo.rootContext);

        return section;
    }

    private static SourceCodeParserInfo getBigQueryFunctionInfo(SectionSourceCode sectionSourceCode)
    {
        CharStream input = CharStreams.fromString(sectionSourceCode.code);
        ParserErrorListener errorListener = new ParserErrorListener(sectionSourceCode.walkerSourceInformation, MemSqlFunctionParserGrammar.VOCABULARY);
        MemSqlFunctionLexerGrammar lexer = new MemSqlFunctionLexerGrammar(input);
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        MemSqlFunctionParserGrammar parser = new MemSqlFunctionParserGrammar(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        return new SourceCodeParserInfo(sectionSourceCode.code, input, sectionSourceCode.sourceInformation, sectionSourceCode.walkerSourceInformation, lexer, parser, parser.definition());
    }
}
