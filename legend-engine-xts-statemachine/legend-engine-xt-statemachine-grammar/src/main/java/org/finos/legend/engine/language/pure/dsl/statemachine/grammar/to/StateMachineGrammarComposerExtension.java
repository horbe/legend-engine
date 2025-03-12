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

package org.finos.legend.engine.language.pure.dsl.statemachine.grammar.to;

import org.eclipse.collections.api.list.MutableList;
import org.finos.legend.engine.language.pure.grammar.to.PureGrammarComposerContext;
import org.finos.legend.engine.language.pure.grammar.to.PureGrammarComposerUtility;
import org.finos.legend.engine.language.pure.grammar.to.extension.PureGrammarComposerExtension;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.PackageableElement;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.statemachine.StateMachine;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.statemachine.StateSpecification;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.statemachine.TransitionSpecification;

public class StateMachineGrammarComposerExtension implements PureGrammarComposerExtension
{
    @Override
    public MutableList<String> group()
    {
        return org.eclipse.collections.impl.factory.Lists.mutable.with("PackageableElement", "StateMachine");
    }

    @Override
    public String getAdditionalContent(PackageableElement element, PureGrammarComposerContext context)
    {
        if (element instanceof StateMachine)
        {
            return this.renderStateMachine((StateMachine) element, context);
        }
        return null;
    }

    private String renderStateMachine(StateMachine stateMachine, PureGrammarComposerContext context)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("StateMachine ").append(PureGrammarComposerUtility.convertPath(stateMachine.getPath())).append("\n{\n");
        
        // Target class
        builder.append("    targetClass: ").append(stateMachine.targetClass).append(";\n");
        
        // States
        for (StateSpecification state : stateMachine.states)
        {
            builder.append("    state ");
            if (state.isInitial)
            {
                builder.append("initial ");
            }
            if (state.isFinal)
            {
                builder.append("final ");
            }
            builder.append(state.name).append(";\n");
        }
        
        // Transitions
        for (TransitionSpecification transition : stateMachine.transitions)
        {
            builder.append("    transition ").append(transition.name)
                   .append(" from ").append(transition.sourceState)
                   .append(" to ").append(transition.targetState);
            
            if (transition.guardCondition != null)
            {
                builder.append(" guard: ").append(transition.guardCondition.body);
            }
            
            builder.append(";\n");
        }
        
        builder.append("}\n");
        return builder.toString();
    }
}
