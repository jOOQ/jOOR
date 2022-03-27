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

public class CompilationUnit {

    private Map<String, String> files = new LinkedHashMap<>();

    public static class Result {
        private final Map<String, Class<?>> classes = new LinkedHashMap<>();

        public void addResult(String className, Class<?> clazz) {
            classes.put(className, clazz);
        }

        public Class<?> getClass(String className) {
            return classes.get(className);
        }

        public int size() {
            return classes.size();
        }

    }

    public static CompilationUnit create() {
        return new CompilationUnit();
    }

    public static CompilationUnit.Result result() {
        return new Result();
    }

    public CompilationUnit unit(String className, String content) {
        files.put(className, content);
        return this;
    }

    public Map<String, String> getFiles() {
        return files;
    }
}
