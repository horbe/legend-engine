// Copyright 2020 Goldman Sachs
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

package org.finos.legend.engine.shared.javaCompiler;

class MemoryClassLoader extends ClassLoader
{
    private final MemoryFileManager manager;

    MemoryClassLoader(MemoryFileManager manager, ClassLoader parent)
    {
        super(parent);
        this.manager = manager;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException
    {
        ClassJavaSource mc = this.manager.getClassJavaSourceByName(name);
        if (mc != null)
        {
            byte[] array = mc.getBytes();
            return defineClass(name, array, 0, array.length);
        }
        return super.findClass(name);
    }
}