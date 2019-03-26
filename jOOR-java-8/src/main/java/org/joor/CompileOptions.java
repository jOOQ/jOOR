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

/* [java-8] */

import java.util.Arrays;

import java.util.Collections;
import java.util.List;

import javax.annotation.processing.Processor;

/**
 * @author Lukas Eder
 */
public final class CompileOptions {

    final List<? extends Processor> processors;
    final List<String> extraOptions;

    public CompileOptions() {
        this(
            Collections.emptyList(),
            Collections.emptyList()
        );
    }

    private CompileOptions(
        List<? extends Processor> processors,
        List<String> extraOptions
    ) {
        this.processors = processors;
        this.extraOptions = extraOptions;
    }

    public final CompileOptions processors(Processor... newProcessors) {
        return processors(Arrays.asList(newProcessors));
    }

    public final CompileOptions processors(List<? extends Processor> newProcessors) {
        return new CompileOptions(newProcessors, Collections.emptyList());
    }

    public final CompileOptions extraOptions(String... extraOptions) {
        return extraOptions(Arrays.asList(extraOptions));
    }

    public final CompileOptions extraOptions(List<String> extraOptions) {
        return new CompileOptions(Collections.emptyList(), extraOptions);
    }

    public final CompileOptions compileOptions(List<? extends Processor> newProcessors, List<String> extraOptions) {
        return new CompileOptions(newProcessors, extraOptions);
    }
}
/* [/java-8] */
