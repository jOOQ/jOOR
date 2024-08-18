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

import org.joor.CompilationUnit;
import org.joor.Reflect;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MultiCompileTest {

    @Test
    public void testSingleUnit() throws Exception {
        CompilationUnit unit = CompilationUnit.input()
                .addClass("org.joor.test.CompileMultiTest1",
                        "package org.joor.test;\n" +
                                "class CompileMultiTest1 implements java.util.function.Supplier<String> {\n" +
                                "  public String get() {\n" +
                                "    return \"Bye World!\";\n" +
                                "  }\n" +
                                "}\n"
                );

        CompilationUnit.Result result = Reflect.compileUnit(unit);
        assertEquals(1, result.size());

        Reflect ref = Reflect.onClass(result.getClass("org.joor.test.CompileMultiTest1"));
        Object out = ref.create().call("get").get();
        assertEquals("Bye World!", out);
    }

    @Test
    public void testDualUnit() throws Exception {
        CompilationUnit unit = CompilationUnit.input()
                .addClass("org.joor.test.CompileMultiTest2",
                        "package org.joor.test;\n" +
                                "class CompileMultiTest2 implements java.util.function.Supplier<String> {\n" +
                                "  public String get() {\n" +
                                "    return \"Bye World!\";\n" +
                                "  }\n" +
                                "}\n"
                )
                .addClass("org.joor.test.CompileMultiTest3",
                        "package org.joor.test;\n" +
                                "class CompileMultiTest3 implements java.util.function.Supplier<String> {\n" +
                                "  public String get() {\n" +
                                "    return \"Hi World!\";\n" +
                                "  }\n" +
                                "}\n"
                );

        CompilationUnit.Result result = Reflect.compileUnit(unit);
        assertEquals(2, result.size());

        Reflect ref2 = Reflect.onClass(result.getClass("org.joor.test.CompileMultiTest2"));
        Reflect ref3 = Reflect.onClass(result.getClass("org.joor.test.CompileMultiTest3"));
        Object out2 = ref2.create().call("get").get();
        Object out3 = ref3.create().call("get").get();
        assertEquals("Bye World!", out2);
        assertEquals("Hi World!", out3);
    }

    @Test
    public void testClassLoadingOrder() throws Exception {
        CompilationUnit unit = CompilationUnit.input()
                .addClass("pm.A", "package pm; public class A extends B {}")
                .addClass("pm.B", "package pm; public class B {}"
                );

        CompilationUnit.Result result = Reflect.compileUnit(unit);
        assertEquals(2, result.size());

        Class<?> a = Reflect.onClass(result.getClass("pm.A")).type();
        Class<?> b = Reflect.onClass(result.getClass("pm.B")).type();
        assertEquals("pm.A", a.getName());
        assertEquals("pm.B", a.getSuperclass().getName());
        assertEquals("pm.B", b.getName());
    }

    @Test
    public void testClassLoadingOrderReverse() throws Exception {
        CompilationUnit unit = CompilationUnit.input()
                .addClass("pm.C", "package pm; public class C {}")
                .addClass("pm.D", "package pm; public class D extends C {}"
                );

        CompilationUnit.Result result = Reflect.compileUnit(unit);
        assertEquals(2, result.size());

        Class<?> c = Reflect.onClass(result.getClass("pm.C")).type();
        Class<?> d = Reflect.onClass(result.getClass("pm.D")).type();
        assertEquals("pm.C", c.getName());
        assertEquals("pm.D", d.getName());
        assertEquals("pm.C", d.getSuperclass().getName());
    }

}
