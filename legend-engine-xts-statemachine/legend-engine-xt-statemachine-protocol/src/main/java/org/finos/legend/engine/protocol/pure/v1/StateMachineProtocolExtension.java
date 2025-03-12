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

package org.finos.legend.engine.protocol.pure.v1;

import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.finos.legend.engine.protocol.pure.v1.extension.ProtocolSubTypeInfo;
import org.finos.legend.engine.protocol.pure.v1.extension.PureProtocolExtension;
import org.finos.legend.engine.protocol.pure.m3.PackageableElement;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.statemachine.StateMachine;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.statemachine.StateMachineSpecification;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.statemachine.StateSpecification;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.statemachine.TransitionSpecification;

import java.util.List;
import java.util.Map;

public class StateMachineProtocolExtension implements PureProtocolExtension
{
    public static final String STATE_MACHINE_CLASSIFIER_PATH = "meta::pure::statemachine::StateMachine";

    @Override
    public MutableList<String> group()
    {
        return org.eclipse.collections.impl.factory.Lists.mutable.with("PackageableElement", "StateMachine");
    }

    @Override
    public List<Function0<List<ProtocolSubTypeInfo<?>>>> getExtraProtocolSubTypeInfoCollectors()
    {
        return Lists.fixedSize.of(() -> Lists.fixedSize.of(
                // Packageable element
                ProtocolSubTypeInfo.newBuilder(PackageableElement.class)
                        .withSubtype(StateMachine.class, "stateMachine")
                        .build(),
                // State machine specifications
                ProtocolSubTypeInfo.newBuilder(StateMachineSpecification.class)
                        .withSubtype(StateSpecification.class, "state")
                        .withSubtype(TransitionSpecification.class, "transition")
                        .build()
        ));
    }

    @Override
    public Map<Class<? extends PackageableElement>, String> getExtraProtocolToClassifierPathMap()
    {
        return Maps.mutable.with(
                StateMachine.class, STATE_MACHINE_CLASSIFIER_PATH
        );
    }
}
