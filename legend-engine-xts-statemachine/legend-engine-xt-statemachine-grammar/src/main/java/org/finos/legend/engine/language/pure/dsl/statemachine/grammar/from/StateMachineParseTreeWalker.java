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

import org.finos.legend.engine.language.pure.grammar.from.ParseTreeWalkerSourceInformation;
import org.finos.legend.engine.language.pure.grammar.from.PureGrammarParserContext;
import org.finos.legend.engine.language.pure.grammar.from.PureGrammarParserUtility;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.statemachine.StateMachineParserGrammar;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.statemachine.StateSpecification;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.statemachine.StateMachine;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.statemachine.TransitionSpecification;
import org.finos.legend.engine.protocol.pure.v1.model.valueSpecification.raw.Lambda;

import java.util.ArrayList;
import java.util.List;

public class StateMachineParseTreeWalker
{
    private final ParseTreeWalkerSourceInformation walkerSourceInformation;
    private final PureGrammarParserContext context;

    public StateMachineParseTreeWalker(ParseTreeWalkerSourceInformation walkerSourceInformation)
    {
        this.walkerSourceInformation = walkerSourceInformation;
        this.context = null;
    }

    public StateMachineParseTreeWalker(ParseTreeWalkerSourceInformation walkerSourceInformation, PureGrammarParserContext context)
    {
        this.walkerSourceInformation = walkerSourceInformation;
        this.context = context;
    }

    public StateMachine visitStateMachine(StateMachineParserGrammar.StateMachineContext ctx)
    {
        StateMachine stateMachine = new StateMachine();
        stateMachine.sourceInformation = walkerSourceInformation.getSourceInformation(ctx);
        stateMachine.name = PureGrammarParserUtility.fromIdentifier(ctx.qualifiedName().identifier());
        stateMachine._package = ctx.qualifiedName().packagePath() == null ? "" : PureGrammarParserUtility.fromPath(ctx.qualifiedName().packagePath().identifier());
        
        stateMachine.states = new ArrayList<>();
        stateMachine.transitions = new ArrayList<>();

        // Target class
        StateMachineParserGrammar.StateMachine_targetClassContext targetClassContext = PureGrammarParserUtility.validateAndExtractRequiredField(ctx.stateMachine_targetClass(), "targetClass", stateMachine.sourceInformation);
        stateMachine.targetClass = PureGrammarParserUtility.fromQualifiedName(targetClassContext.qualifiedName());

        // States
        for (StateMachineParserGrammar.StateMachine_stateContext stateContext : ctx.stateMachine_state())
        {
            StateSpecification state = new StateSpecification();
            state.sourceInformation = walkerSourceInformation.getSourceInformation(stateContext);
            state.name = PureGrammarParserUtility.fromIdentifier(stateContext.identifier());
            state.isInitial = stateContext.INITIAL() != null;
            state.isFinal = stateContext.FINAL() != null;
            stateMachine.states.add(state);
        }

        // Transitions
        for (StateMachineParserGrammar.StateMachine_transitionContext transitionContext : ctx.stateMachine_transition())
        {
            TransitionSpecification transition = new TransitionSpecification();
            transition.sourceInformation = walkerSourceInformation.getSourceInformation(transitionContext);
            transition.name = PureGrammarParserUtility.fromIdentifier(transitionContext.identifier(0));
            transition.sourceState = PureGrammarParserUtility.fromIdentifier(transitionContext.identifier(1));
            transition.targetState = PureGrammarParserUtility.fromIdentifier(transitionContext.identifier(2));
            
            if (transitionContext.islandDefinition() != null)
            {
                Lambda lambda = new Lambda();
                lambda.body = PureGrammarParserUtility.fromGrammarString(transitionContext.islandDefinition().getText(), true);
                transition.guardCondition = lambda;
            }
            
            stateMachine.transitions.add(transition);
        }

        return stateMachine;
    }
}
