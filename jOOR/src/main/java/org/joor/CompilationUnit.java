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
package org.joor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Unit for holding multiple source files to be compiled in one go.
 */
public class CompilationUnit {

    private final Map<String, String> files = new LinkedHashMap<>();

    /**
     * The result of the compilation that holds mapping for each className -> class.
     */
    public static class Result {
        private final Map<String, Class<?>> classes = new LinkedHashMap<>();

        void addResult(String className, Class<?> clazz) {
            classes.put(className, clazz);
        }

        /**
         * Gets the compiled class by its class name
         *
         * @param className the class name
         * @return the compiled class
         */
        public Class<?> getClass(String className) {
            return classes.get(className);
        }

        /**
         * Number of classes in the result
         */
        public int size() {
            return classes.size();
        }

        /**
         * Set of the classes by their names
         */
        public Set<String> getClassNames() {
            return classes.keySet();
        }

    }

    static CompilationUnit.Result result() {
        return new Result();
    }

    /**
     * Creates a new compilation unit for holding input files.
     */
    public static CompilationUnit input() {
        return new CompilationUnit();
    }

    /**
     * Adds input to the compilation unit.
     *
     * @param className  the class name
     * @param content    the source code for the class
     */
    public CompilationUnit addClass(String className, String content) {
        files.put(className, content);
        return this;
    }

    Map<String, String> getInput() {
        return files;
    }
}
