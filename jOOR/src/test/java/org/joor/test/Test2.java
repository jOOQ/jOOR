/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.joor.test;

/**
 * @author Lukas Eder
 */
public class Test2 {

    public final Object          n;
    public final ConstructorType constructorType;

    public Test2() {
        this.n = null;
        this.constructorType = ConstructorType.NO_ARGS;
    }

    public Test2(Integer n) {
        this.n = n;
        this.constructorType = ConstructorType.INTEGER;
    }

    public Test2(Number n) {
        this.n = n;
        this.constructorType = ConstructorType.NUMBER;
    }

    public Test2(Object n) {
        this.n = n;
        this.constructorType = ConstructorType.OBJECT;
    }

    public static enum ConstructorType {
        NO_ARGS,
        INTEGER,
        NUMBER,
        OBJECT
    }
}
