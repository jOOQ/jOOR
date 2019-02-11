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

import static org.hamcrest.CoreMatchers.is;
import static org.joor.Reflect.accessible;
import static org.joor.Reflect.on;
import static org.joor.Reflect.onClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.util.HashMap;
import java.util.Map;

import org.joor.Reflect;
import org.joor.ReflectException;
import org.joor.test.Test2.ConstructorType;
import org.joor.test.Test3.MethodType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Lukas Eder
 * @author Thomas Darimont
 */
public class ReflectTest {

    static final boolean JDK9 = false                                       ;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        Test1.S_INT1 = 0;
        Test1.S_INT2 = null;
        Test1.S_DATA = null;
    }

    @Test
    public void testOnClass() {
        assertEquals(onClass(Object.class), onClass("java.lang.Object", ClassLoader.getSystemClassLoader()));
        assertEquals(onClass(Object.class), onClass("java.lang.Object"));
        assertEquals(onClass(Object.class).<Object>get(), onClass("java.lang.Object").get());

        try {
            onClass("asdf");
            fail();
        }
        catch (ReflectException expected) {}

        try {
            onClass("asdf", ClassLoader.getSystemClassLoader());
            fail();
        }
        catch (ReflectException expected) {}
    }

    @Test
    public void testOnInstance() {
        assertEquals(Object.class, onClass(Object.class).get());
        assertEquals("abc", on((Object) "abc").get());
        assertEquals(1, (int) (Integer) on(1).get());
    }

    @Test
    public void testConstructors() {
        assertEquals("", onClass(String.class).create().get());
        assertEquals("abc", onClass(String.class).create("abc").get());
        assertEquals("abc", onClass(String.class).create("abc".getBytes()).get());
        assertEquals("abc", onClass(String.class).create("abc".toCharArray()).get());
        assertEquals("b", onClass(String.class).create("abc".toCharArray(), 1, 1).get());

        try {
            onClass(String.class).create(new Object());
            fail();
        }
        catch (ReflectException expected) {}
    }

    @Test
    public void testPrivateConstructor() {
        assertNull(onClass(PrivateConstructors.class).create().get("string"));
        assertEquals("abc", onClass(PrivateConstructors.class).create("abc").get("string"));
    }

    @Test
    public void testConstructorsWithAmbiguity() {
        // [#5] Re-enact when this is implemented
        assumeTrue(false);

        Test2 test;

        test = onClass(Test2.class).create().get();
        assertEquals(null, test.n);
        assertEquals(ConstructorType.NO_ARGS, test.constructorType);

        test = onClass(Test2.class).create("abc").get();
        assertEquals("abc", test.n);
        assertEquals(ConstructorType.OBJECT, test.constructorType);

        test = onClass(Test2.class).create(1L).get();
        assertEquals(1L, test.n);
        assertEquals(ConstructorType.NUMBER, test.constructorType);

        test = onClass(Test2.class).create(1).get();
        assertEquals(1, test.n);
        assertEquals(ConstructorType.INTEGER, test.constructorType);

        test = onClass(Test2.class).create('a').get();
        assertEquals('a', test.n);
        assertEquals(ConstructorType.OBJECT, test.constructorType);
    }

    @Test
    public void testMethods() {
        // Instance methods
        // ----------------
        assertEquals("", on((Object) " ").call("trim").get());
        assertEquals("12", on((Object) " 12 ").call("trim").get());
        assertEquals("34", on((Object) "1234").call("substring", 2).get());
        assertEquals("12", on((Object) "1234").call("substring", 0, 2).get());
        assertEquals("1234", on((Object) "12").call("concat", "34").get());
        assertEquals("123456", on((Object) "12").call("concat", "34").call("concat", "56").get());
        assertEquals(2, (int) (Integer) on((Object) "1234").call("indexOf", "3").get());
        assertEquals(2.0f, (Float) on((Object) "1234").call("indexOf", "3").call("floatValue").get(), 0.0f);
        assertEquals("2", on((Object) "1234").call("indexOf", "3").call("toString").get());

        // Static methods
        // --------------
        assertEquals("true", onClass(String.class).call("valueOf", true).get());
        assertEquals("1", onClass(String.class).call("valueOf", 1).get());
        assertEquals("abc", onClass(String.class).call("valueOf", "abc".toCharArray()).get());
        assertEquals("abc", onClass(String.class).call("copyValueOf", "abc".toCharArray()).get());
        assertEquals("b", onClass(String.class).call("copyValueOf", "abc".toCharArray(), 1, 1).get());
    }

    @Test
    public void testVoidMethods() {
        // Instance methods
        // ----------------
        Test4 test4 = new Test4();
        assertEquals(test4, on(test4).call("i_method").get());

        // Static methods
        // --------------
        assertEquals(Test4.class, onClass(Test4.class).call("s_method").get());
    }

    @Test
    public void testPrivateMethods() throws Exception {
        // Instance methods
        // ----------------
        Test8 test8 = new Test8();
        assertEquals(test8, on(test8).call("i_method").get());

        // Static methods
        // --------------
        assertEquals(Test8.class, onClass(Test8.class).call("s_method").get());

    }

    @Test
    public void testNullArguments() throws Exception {
        Test9 test9 = new Test9();
        on(test9).call("put", "key", "value");
        assertTrue(test9.map.containsKey("key"));
        assertEquals("value", test9.map.get("key"));

        on(test9).call("put", "key", null);
        assertTrue(test9.map.containsKey("key"));
        assertNull(test9.map.get("key"));
    }

    @Test
    public void testPublicMethodsAreFoundInHierarchy() throws Exception {
        TestHierarchicalMethodsSubclass subclass = new TestHierarchicalMethodsSubclass();
        assertEquals(TestHierarchicalMethodsBase.PUBLIC_RESULT, on(subclass).call("pub_base_method", 1).get());
    }

    @Test
    public void testPrivateMethodsAreFoundInHierarchy() throws Exception {
        TestHierarchicalMethodsSubclass subclass = new TestHierarchicalMethodsSubclass();
        on(subclass).call("very_priv_method").get();
    }

    @Test
    public void testPrivateMethodsAreFoundOnDeclaringClass() throws Exception {
        TestHierarchicalMethodsSubclass subclass = new TestHierarchicalMethodsSubclass();
        assertEquals(TestHierarchicalMethodsSubclass.PRIVATE_RESULT, on(subclass).call("priv_method", 1).get());

        TestHierarchicalMethodsBase baseClass = new TestHierarchicalMethodsBase();
        assertEquals(TestHierarchicalMethodsBase.PRIVATE_RESULT, on(baseClass).call("priv_method", 1).get());
    }

    @Test
    public void testMethodsWithAmbiguity() {
        // [#5] Re-enact when this is implemented
        assumeTrue(false);

        Test3 test;

        test = onClass(Test3.class).create().call("method").get();
        assertEquals(null, test.n);
        assertEquals(MethodType.NO_ARGS, test.methodType);

        test = onClass(Test3.class).create().call("method", "abc").get();
        assertEquals("abc", test.n);
        assertEquals(MethodType.OBJECT, test.methodType);

        test = onClass(Test3.class).create().call("method", 1L).get();
        assertEquals(1L, test.n);
        assertEquals(MethodType.NUMBER, test.methodType);

        test = onClass(Test3.class).create().call("method", 1).get();
        assertEquals(1, test.n);
        assertEquals(MethodType.INTEGER, test.methodType);

        test = onClass(Test3.class).create().call("method", 'a').get();
        assertEquals('a', test.n);
        assertEquals(MethodType.OBJECT, test.methodType);
    }

    @Test
    public void testFields() throws Exception {
        // Instance methods
        // ----------------
        Test1 test1 = new Test1();
        assertEquals(1, (int) (Integer) on(test1).set("I_INT1", 1).get("I_INT1"));
        assertEquals(1, (int) (Integer) on(test1).field("I_INT1").get());
        assertEquals(1, (int) (Integer) on(test1).set("I_INT2", 1).get("I_INT2"));
        assertEquals(1, (int) (Integer) on(test1).field("I_INT2").get());
        assertNull(on(test1).set("I_INT2", null).get("I_INT2"));
        assertNull(on(test1).field("I_INT2").get());

        // Static methods
        // --------------
        assertEquals(1, (int) (Integer) onClass(Test1.class).set("S_INT1", 1).get("S_INT1"));
        assertEquals(1, (int) (Integer) onClass(Test1.class).field("S_INT1").get());
        assertEquals(1, (int) (Integer) onClass(Test1.class).set("S_INT2", 1).get("S_INT2"));
        assertEquals(1, (int) (Integer) onClass(Test1.class).field("S_INT2").get());
        assertNull(onClass(Test1.class).set("S_INT2", null).get("S_INT2"));
        assertNull(onClass(Test1.class).field("S_INT2").get());

        // Hierarchies
        // -----------
        TestHierarchicalMethodsSubclass test2 = new TestHierarchicalMethodsSubclass();
        assertEquals(1, (int) (Integer) on(test2).set("invisibleField1", 1).get("invisibleField1"));
        assertEquals(1, accessible(TestHierarchicalMethodsBase.class.getDeclaredField("invisibleField1")).get(test2));

        assertEquals(1, (int) (Integer) on(test2).set("invisibleField2", 1).get("invisibleField2"));
        assertEquals(0, accessible(TestHierarchicalMethodsBase.class.getDeclaredField("invisibleField2")).get(test2));
        assertEquals(1, accessible(TestHierarchicalMethodsSubclass.class.getDeclaredField("invisibleField2")).get(test2));

        assertEquals(1, (int) (Integer) on(test2).set("invisibleField3", 1).get("invisibleField3"));
        assertEquals(1, accessible(TestHierarchicalMethodsSubclass.class.getDeclaredField("invisibleField3")).get(test2));

        assertEquals(1, (int) (Integer) on(test2).set("visibleField1", 1).get("visibleField1"));
        assertEquals(1, accessible(TestHierarchicalMethodsBase.class.getDeclaredField("visibleField1")).get(test2));

        assertEquals(1, (int) (Integer) on(test2).set("visibleField2", 1).get("visibleField2"));
        assertEquals(0, accessible(TestHierarchicalMethodsBase.class.getDeclaredField("visibleField2")).get(test2));
        assertEquals(1, accessible(TestHierarchicalMethodsSubclass.class.getDeclaredField("visibleField2")).get(test2));

        assertEquals(1, (int) (Integer) on(test2).set("visibleField3", 1).get("visibleField3"));
        assertEquals(1, accessible(TestHierarchicalMethodsSubclass.class.getDeclaredField("visibleField3")).get(test2));

        assertNull(accessible(null));
    }

    @Test
    public void testFinalFields() {

        try {
            // Instance methods
            // ----------------
            Test11 test11 = new Test11();
            assertEquals(1, (int) (Integer) on(test11).set("F_INT1", 1).get("F_INT1"));
            assertEquals(1, (int) (Integer) on(test11).field("F_INT1").get());
            assertEquals(1, (int) (Integer) on(test11).set("F_INT2", 1).get("F_INT1"));
            assertEquals(1, (int) (Integer) on(test11).field("F_INT2").get());
            assertNull(on(test11).set("F_INT2", null).get("F_INT2"));
            assertNull(on(test11).field("F_INT2").get());

            // Static methods
            // ----------------
            assertEquals(1, (int) (Integer) onClass(Test11.class).set("SF_INT1", 1).get("SF_INT1"));
            assertEquals(1, (int) (Integer) onClass(Test11.class).field("SF_INT1").get());
            assertEquals(1, (int) (Integer) onClass(Test11.class).set("SF_INT2", 1).get("SF_INT2"));
            assertEquals(1, (int) (Integer) onClass(Test11.class).field("SF_INT2").get());
            onClass(Test11.class).set("SF_INT2", 1).field("SF_INT2").get();
            assertNull(onClass(Test11.class).set("SF_INT2", null).get("SF_INT2"));
            assertNull(onClass(Test11.class).field("SF_INT2").get());
        }
        catch (ReflectException e) {

            // [#50] This may no longer work on JDK 9
            if (!JDK9)
                throw e;
        }
    }

    @Test
    public void testFinalFieldAdvanced() {
        try {
            onClass(Test11.class).set("S_DATA", onClass(Test11.class).create())
                    .field("S_DATA")
                    .set("I_DATA", onClass(Test11.class).create())
                    .field("I_DATA")
                    .set("F_INT1", 1)
                    .set("F_INT2", 1)
                    .set("SF_INT1", 2)
                    .set("SF_INT2", 2);
            assertEquals(2, Test11.SF_INT1);
            assertEquals(2, (int) Test11.SF_INT2);
            assertEquals(0, Test11.S_DATA.F_INT1);
            assertEquals(0, (int) Test11.S_DATA.F_INT2);
            assertEquals(1, Test11.S_DATA.I_DATA.F_INT1);
            assertEquals(1, (int) Test11.S_DATA.I_DATA.F_INT2);
        }
        catch (ReflectException e) {

            // [#50] This may no longer work on JDK 9
            if (!JDK9)
                throw e;
        }
    }

    @Test
    @Ignore
    public void testPrivateStaticFinal() {
        Reflect reflect = onClass(TestPrivateStaticFinal.class);

        assertEquals(Integer.valueOf(1), reflect.get("I1"));
        assertEquals(Integer.valueOf(1), reflect.get("I2"));

        reflect.set("I1", 2);
        reflect.set("I2", 2);

        assertEquals(Integer.valueOf(2), reflect.get("I1"));
        assertEquals(Integer.valueOf(2), reflect.get("I2"));
    }

    @Test
    public void testFieldMap() {
        // Instance methods
        // ----------------
        Test1 test1 = new Test1();
        assertEquals(3, on(test1).fields().size());
        assertTrue(on(test1).fields().containsKey("I_INT1"));
        assertTrue(on(test1).fields().containsKey("I_INT2"));
        assertTrue(on(test1).fields().containsKey("I_DATA"));

        assertEquals(1, (int) (Integer) on(test1).set("I_INT1", 1).fields().get("I_INT1").get());
        assertEquals(1, (int) (Integer) on(test1).fields().get("I_INT1").get());
        assertEquals(1, (int) (Integer) on(test1).set("I_INT2", 1).fields().get("I_INT2").get());
        assertEquals(1, (int) (Integer) on(test1).fields().get("I_INT2").get());
        assertNull(on(test1).set("I_INT2", null).fields().get("I_INT2").get());
        assertNull(on(test1).fields().get("I_INT2").get());

        // Static methods
        // --------------
        assertEquals(3, onClass(Test1.class).fields().size());
        assertTrue(onClass(Test1.class).fields().containsKey("S_INT1"));
        assertTrue(onClass(Test1.class).fields().containsKey("S_INT2"));
        assertTrue(onClass(Test1.class).fields().containsKey("S_DATA"));

        assertEquals(1, (int) (Integer) onClass(Test1.class).set("S_INT1", 1).fields().get("S_INT1").get());
        assertEquals(1, (int) (Integer) onClass(Test1.class).fields().get("S_INT1").get());
        assertEquals(1, (int) (Integer) onClass(Test1.class).set("S_INT2", 1).fields().get("S_INT2").get());
        assertEquals(1, (int) (Integer) onClass(Test1.class).fields().get("S_INT2").get());
        assertNull(onClass(Test1.class).set("S_INT2", null).fields().get("S_INT2").get());
        assertNull(onClass(Test1.class).fields().get("S_INT2").get());

        // Hierarchies
        // -----------
        TestHierarchicalMethodsSubclass test2 = new TestHierarchicalMethodsSubclass();
        assertEquals(6, on(test2).fields().size());
        assertTrue(on(test2).fields().containsKey("invisibleField1"));
        assertTrue(on(test2).fields().containsKey("invisibleField2"));
        assertTrue(on(test2).fields().containsKey("invisibleField3"));
        assertTrue(on(test2).fields().containsKey("visibleField1"));
        assertTrue(on(test2).fields().containsKey("visibleField2"));
        assertTrue(on(test2).fields().containsKey("visibleField3"));
    }

    @Test
    public void testFieldAdvanced() {
        onClass(Test1.class).set("S_DATA", onClass(Test1.class).create())
                      .field("S_DATA")
                      .set("I_DATA", onClass(Test1.class).create())
                      .field("I_DATA")
                      .set("I_INT1", 1)
                      .set("S_INT1", 2);
        assertEquals(2, Test1.S_INT1);
        assertEquals(null, Test1.S_INT2);
        assertEquals(0, Test1.S_DATA.I_INT1);
        assertEquals(null, Test1.S_DATA.I_INT2);
        assertEquals(1, Test1.S_DATA.I_DATA.I_INT1);
        assertEquals(null, Test1.S_DATA.I_DATA.I_INT2);
    }

    @Test
    public void testProxy() {
        assertEquals("abc", on((Object) "abc").as(Test5.class).substring(0));
        assertEquals("bc", on((Object) "abc").as(Test5.class).substring(1));
        assertEquals("c", on((Object) "abc").as(Test5.class).substring(2));

        assertEquals("a", on((Object) "abc").as(Test5.class).substring(0, 1));
        assertEquals("b", on((Object) "abc").as(Test5.class).substring(1, 2));
        assertEquals("c", on((Object) "abc").as(Test5.class).substring(2, 3));

        assertEquals("abc", on((Object) "abc").as(Test5.class).substring(0));
        assertEquals("bc", on((Object) "abc").as(Test5.class).substring(1));
        assertEquals("c", on((Object) "abc").as(Test5.class).substring(2));

        assertEquals("a", on((Object) "abc").as(Test5.class).substring(0, 1));
        assertEquals("b", on((Object) "abc").as(Test5.class).substring(1, 2));
        assertEquals("c", on((Object) "abc").as(Test5.class).substring(2, 3));
    }

    @Test
    public void testMapProxy() {

        @SuppressWarnings({ "unused", "serial" })
        class MyMap extends HashMap<String, Object> {
            String baz;
            public void setBaz(String baz) {
                this.baz = "MyMap: " + baz;
            }

            public String getBaz() {
                return baz;
            }
        }
        Map<String, Object> map = new MyMap();

        on(map).as(Test6.class).setFoo("abc");
        assertEquals(1, map.size());
        assertEquals("abc", map.get("foo"));
        assertEquals("abc", on(map).as(Test6.class).getFoo());

        on(map).as(Test6.class).setBar(true);
        assertEquals(2, map.size());
        assertEquals(true, map.get("bar"));
        assertEquals(true, on(map).as(Test6.class).isBar());

        on(map).as(Test6.class).setBaz("baz");
        assertEquals(2, map.size());
        assertEquals(null, map.get("baz"));
        assertEquals("MyMap: baz", on(map).as(Test6.class).getBaz());

        try {
            on(map).as(Test6.class).testIgnore();
            fail();
        }
        catch (ReflectException expected) {}
    }

    @Test
    public void testPrivateField() throws Exception {
        class Foo {
            private String bar;
        }

        Foo foo = new Foo();
        on(foo).set("bar", "FooBar");
        assertThat(foo.bar, is("FooBar"));
        assertEquals("FooBar", on(foo).get("bar"));

        on(foo).set("bar", null);
        assertNull(foo.bar);
        assertNull(on(foo).get("bar"));
    }

    @Test
    public void testType() throws Exception {
        assertEquals(Object.class, on(new Object()).type());
        assertEquals(Object.class, onClass(Object.class).type());
        assertEquals(Integer.class, on(1).type());
        assertEquals(Integer.class, onClass(Integer.class).type());
    }

    @Test
    public void testCreateWithNulls() throws Exception {
        Test2 test2 = onClass(Test2.class).create((Object) null).<Test2>get();
        assertNull(test2.n);
        // Can we make any assertions about the actual construct being called?
        // assertEquals(Test2.ConstructorType.OBJECT, test2.constructorType);
    }

    @Test
    public void testCreateWithPrivateConstructor() throws Exception {
        Test10 t1 = onClass(Test10.class).create(1).get();
        assertEquals(1, (int) t1.i);
        assertNull(t1.s);

        Test10 t2 = onClass(Test10.class).create("a").get();
        assertNull(t2.i);
        assertEquals("a", t2.s);

        Test10 t3 = onClass(Test10.class).create("a", 1).get();
        assertEquals(1, (int) t3.i);
        assertEquals("a", t3.s);
    }

    @Test
    public void testHashCode() {
        Object object = new Object();
        assertEquals(Reflect.on(object).hashCode(), object.hashCode());
    }

    @Test
    public void testToString() {
        Object object = new Object() {
            @Override
            public String toString() {
                return "test";
            }
        };
        assertEquals(Reflect.on(object).toString(), object.toString());
    }

    @Test
    public void testEquals() {
        Object object = new Object();
        Reflect a = Reflect.on(object);
        Reflect b = Reflect.on(object);
        Reflect c = Reflect.on(object);

        assertTrue(b.equals(a));
        assertTrue(a.equals(b));
        assertTrue(b.equals(c));
        assertTrue(a.equals(c));
        //noinspection ObjectEqualsNull
        assertFalse(a.equals(null));
    }
















    @Test
    public void testNullStaticFieldType() {
        Map<String, Reflect> fields = Reflect.onClass(Test1.class).fields();

        assertEquals(3, fields.size());
        assertEquals(int.class, fields.get("S_INT1").type());
        assertEquals(Integer.valueOf(0), fields.get("S_INT1").get());
        assertEquals(Integer.class, fields.get("S_INT2").type());
        assertNull(fields.get("S_INT2").get());
        assertEquals(Test1.class, fields.get("S_DATA").type());
        assertNull(fields.get("S_DATA").get());
    }

    @Test
    public void testNullInstanceFieldType() {
        Map<String, Reflect> fields = Reflect.on(new Test1()).fields();

        assertEquals(3, fields.size());
        assertEquals(int.class, fields.get("I_INT1").type());
        assertEquals(Integer.valueOf(0), fields.get("I_INT1").get());
        assertEquals(Integer.class, fields.get("I_INT2").type());
        assertNull(fields.get("I_INT2").get());
        assertEquals(Test1.class, fields.get("I_DATA").type());
        assertNull(fields.get("I_DATA").get());
    }

    @Test
    public void testNullInstanceToString() {
        assertEquals("null", Reflect.on((Object) null).toString());
    }

    @Test
    public void testInitValue() {
        assertEquals((byte) 0, (byte) Reflect.initValue(byte.class));
        assertEquals((short) 0, (short) Reflect.initValue(short.class));
        assertEquals(0, (int) Reflect.initValue(int.class));
        assertEquals(0L, (long) Reflect.initValue(long.class));
        assertEquals(0.0, (double) Reflect.initValue(double.class), 0.0);
        assertEquals(0.0f, (float) Reflect.initValue(float.class), 0.0f);
        assertEquals((char) 0, (char) Reflect.initValue(char.class));
        assertEquals(false, (boolean) Reflect.initValue(boolean.class));
        assertNull(Reflect.initValue(Object.class));
        assertNull(Reflect.initValue(Byte.class));
        assertNull(Reflect.initValue(Short.class));
        assertNull(Reflect.initValue(Integer.class));
        assertNull(Reflect.initValue(Long.class));
        assertNull(Reflect.initValue(Double.class));
        assertNull(Reflect.initValue(Float.class));
        assertNull(Reflect.initValue(Character.class));
        assertNull(Reflect.initValue(Boolean.class));
    }
}
