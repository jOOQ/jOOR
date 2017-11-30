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
public class Test10 {

    public final String  s;
    public final Integer i;

    Test10(int i) {
        this(null, i);
    }

    Test10(String s) {
        this(s, null);
    }

    Test10(String s, int i) {
        this(s, (Integer) i);
    }

    Test10(String s, Integer i) {
        this.s = s;
        this.i = i;
    }
}
