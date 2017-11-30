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
public class Test3 {

    public Object     n;
    public MethodType methodType;

    public void method() {
        this.n = null;
        this.methodType = MethodType.NO_ARGS;
    }

    public void method(Integer n1) {
        this.n = n1;
        this.methodType = MethodType.INTEGER;
    }

    public void method(Number n1) {
        this.n = n1;
        this.methodType = MethodType.NUMBER;
    }

    public void method(Object n1) {
        this.n = n1;
        this.methodType = MethodType.OBJECT;
    }

    public static enum MethodType {
        NO_ARGS,
        INTEGER,
        NUMBER,
        OBJECT
    }
}
