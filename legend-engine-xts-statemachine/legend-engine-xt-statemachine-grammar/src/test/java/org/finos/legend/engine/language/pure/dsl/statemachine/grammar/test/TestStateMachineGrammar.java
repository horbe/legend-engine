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

package org.finos.legend.engine.language.pure.dsl.statemachine.grammar.test;

import org.finos.legend.engine.language.pure.grammar.from.PureGrammarParser;
import org.finos.legend.engine.language.pure.grammar.to.PureGrammarComposer;
import org.finos.legend.engine.protocol.pure.v1.model.context.PureModelContextData;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.statemachine.StateMachine;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.statemachine.StateSpecification;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.statemachine.TransitionSpecification;
import org.junit.Assert;
import org.junit.Test;

public class TestStateMachineGrammar
{
    @Test
    public void testBasicStateMachine()
    {
        String grammar = "###StateMachine\n" +
                "StateMachine test::OrderStateMachine\n" +
                "{\n" +
                "    targetClass: test::Order;\n" +
                "    state initial new;\n" +
                "    state processing;\n" +
                "    state final completed;\n" +
                "    state final cancelled;\n" +
                "    transition startProcessing from new to processing;\n" +
                "    transition complete from processing to completed;\n" +
                "    transition cancel from processing to cancelled guard: {o: test::Order[1]|$o.amount == 0};\n" +
                "}\n";

        PureModelContextData modelContextData = PureGrammarParser.newInstance().parseModel(grammar);
        Assert.assertEquals(1, modelContextData.getElements().size());
        Assert.assertTrue(modelContextData.getElements().get(0) instanceof StateMachine);

        StateMachine stateMachine = (StateMachine) modelContextData.getElements().get(0);
        Assert.assertEquals("test::OrderStateMachine", stateMachine.getPath());
        Assert.assertEquals("test::Order", stateMachine.targetClass);
        
        // Verify states
        Assert.assertEquals(4, stateMachine.states.size());
        
        StateSpecification newState = stateMachine.states.get(0);
        Assert.assertEquals("new", newState.name);
        Assert.assertTrue(newState.isInitial);
        Assert.assertFalse(newState.isFinal);
        
        StateSpecification processingState = stateMachine.states.get(1);
        Assert.assertEquals("processing", processingState.name);
        Assert.assertFalse(processingState.isInitial);
        Assert.assertFalse(processingState.isFinal);
        
        StateSpecification completedState = stateMachine.states.get(2);
        Assert.assertEquals("completed", completedState.name);
        Assert.assertFalse(completedState.isInitial);
        Assert.assertTrue(completedState.isFinal);
        
        StateSpecification cancelledState = stateMachine.states.get(3);
        Assert.assertEquals("cancelled", cancelledState.name);
        Assert.assertFalse(cancelledState.isInitial);
        Assert.assertTrue(cancelledState.isFinal);
        
        // Verify transitions
        Assert.assertEquals(3, stateMachine.transitions.size());
        
        TransitionSpecification startProcessingTransition = stateMachine.transitions.get(0);
        Assert.assertEquals("startProcessing", startProcessingTransition.name);
        Assert.assertEquals("new", startProcessingTransition.sourceState);
        Assert.assertEquals("processing", startProcessingTransition.targetState);
        Assert.assertNull(startProcessingTransition.guardCondition);
        
        TransitionSpecification completeTransition = stateMachine.transitions.get(1);
        Assert.assertEquals("complete", completeTransition.name);
        Assert.assertEquals("processing", completeTransition.sourceState);
        Assert.assertEquals("completed", completeTransition.targetState);
        Assert.assertNull(completeTransition.guardCondition);
        
        TransitionSpecification cancelTransition = stateMachine.transitions.get(2);
        Assert.assertEquals("cancel", cancelTransition.name);
        Assert.assertEquals("processing", cancelTransition.sourceState);
        Assert.assertEquals("cancelled", cancelTransition.targetState);
        Assert.assertNotNull(cancelTransition.guardCondition);
        Assert.assertEquals("{o: test::Order[1]|$o.amount == 0}", cancelTransition.guardCondition.body);
        
        // Test round-trip
        String serialized = PureGrammarComposer.newInstance(PureGrammarComposer.Scope.PURE).renderPureModelContextData(modelContextData);
        Assert.assertTrue(serialized.contains("StateMachine test::OrderStateMachine"));
        Assert.assertTrue(serialized.contains("targetClass: test::Order"));
        Assert.assertTrue(serialized.contains("state initial new"));
        Assert.assertTrue(serialized.contains("state processing"));
        Assert.assertTrue(serialized.contains("state final completed"));
        Assert.assertTrue(serialized.contains("state final cancelled"));
        Assert.assertTrue(serialized.contains("transition startProcessing from new to processing"));
        Assert.assertTrue(serialized.contains("transition complete from processing to completed"));
        Assert.assertTrue(serialized.contains("transition cancel from processing to cancelled guard: {o: test::Order[1]|$o.amount == 0}"));
    }
}
