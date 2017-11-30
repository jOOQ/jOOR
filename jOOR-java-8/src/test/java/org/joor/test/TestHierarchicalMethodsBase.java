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

public class TestHierarchicalMethodsBase {

    public static String PUBLIC_RESULT = "PUBLIC_BASE";
    public static String PRIVATE_RESULT = "PRIVATE_BASE";

    private int invisibleField1;
    private int invisibleField2;
    public int visibleField1;
    public int visibleField2;

    public String pub_base_method(int number) {
        return PUBLIC_RESULT;
    }
    public String pub_method(int number) {
        return PUBLIC_RESULT;
    }

    private String priv_method(int number) {
        return PRIVATE_RESULT;
    }

    private String very_priv_method() {
        return PRIVATE_RESULT;
    }
}
