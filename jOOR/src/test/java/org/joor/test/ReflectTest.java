/**
 * Copyright (c) 2009-2011, Lukas Eder, lukas.eder@gmail.com
 * All rights reserved.
 *
 * This software is licensed to you under the Apache License, Version 2.0
 * (the "License"); You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * . Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * . Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * . Neither the name "jOOR" nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.joor.test;

import static org.joor.Reflect.on;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.joor.ReflectException;
import org.joor.test.Test2.ConstructorType;
import org.joor.test.Test3.MethodType;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Lukas Eder
 */
public class ReflectTest {

    @Test
    public void testOn() {
        assertEquals(on(Object.class), on("java.lang.Object"));
        assertEquals(on(Object.class).get(), on("java.lang.Object").get());
        assertEquals(Object.class, on(Object.class).get());
        assertEquals("abc", on((Object) "abc").get());
        assertEquals(1, on(1).get());

        try {
            on("asdf");
            fail();
        }
        catch (ReflectException expected) {}
    }

    @Test
    public void testConstructors() {
        assertEquals("", on(String.class).create().get());
        assertEquals("abc", on(String.class).create("abc").get());
        assertEquals("abc", on(String.class).create("abc".getBytes()).get());
        assertEquals("abc", on(String.class).create("abc".toCharArray()).get());
        assertEquals("b", on(String.class).create("abc".toCharArray(), 1, 1).get());

        try {
            on(String.class).create(new Object());
            fail();
        }
        catch (ReflectException expected) {}
    }

    @Test
    public void testConstructorsWithAmbiguity() {
        Test2 test;

        test = on(Test2.class).create().get();
        assertEquals(null, test.n);
        assertEquals(ConstructorType.NO_ARGS, test.constructorType);

        test = on(Test2.class).create("abc").get();
        assertEquals("abc", test.n);
        assertEquals(ConstructorType.OBJECT, test.constructorType);

        test = on(Test2.class).create(new Long("1")).get();
        assertEquals(1L, test.n);
        assertEquals(ConstructorType.NUMBER, test.constructorType);

        test = on(Test2.class).create(1).get();
        assertEquals(1, test.n);
        assertEquals(ConstructorType.INTEGER, test.constructorType);

        test = on(Test2.class).create('a').get();
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
        assertEquals(2, on((Object) "1234").call("indexOf", "3").get());
        assertEquals(2.0f, on((Object) "1234").call("indexOf", "3").call("floatValue").get());
        assertEquals("2", on((Object) "1234").call("indexOf", "3").call("toString").get());

        // Static methods
        // --------------
        assertEquals("true", on(String.class).call("valueOf", true).get());
        assertEquals("1", on(String.class).call("valueOf", 1).get());
        assertEquals("abc", on(String.class).call("valueOf", "abc".toCharArray()).get());
        assertEquals("abc", on(String.class).call("copyValueOf", "abc".toCharArray()).get());
        assertEquals("b", on(String.class).call("copyValueOf", "abc".toCharArray(), 1, 1).get());
    }

    @Test
    public void testVoidMethods() {
        // Instance methods
        // ----------------
        Test4 test4 = new Test4();
        assertEquals(test4, on(test4).call("i_method").get());

        // Static methods
        // --------------
        assertEquals(Test4.class, on(Test4.class).call("s_method").get());
    }

    @Test
    public void testMethodsWithAmbiguity() {
        Test3 test;

        test = on(Test3.class).create().call("method").get();
        assertEquals(null, test.n);
        assertEquals(MethodType.NO_ARGS, test.methodType);

        test = on(Test3.class).create().call("method", "abc").get();
        assertEquals("abc", test.n);
        assertEquals(MethodType.OBJECT, test.methodType);

        test = on(Test3.class).create().call("method", new Long("1")).get();
        assertEquals(1L, test.n);
        assertEquals(MethodType.NUMBER, test.methodType);

        test = on(Test3.class).create().call("method", 1).get();
        assertEquals(1, test.n);
        assertEquals(MethodType.INTEGER, test.methodType);

        test = on(Test3.class).create().call("method", 'a').get();
        assertEquals('a', test.n);
        assertEquals(MethodType.OBJECT, test.methodType);
    }

    @Test
    public void testFields() {
        // Instance methods
        // ----------------
        Test1 test1 = new Test1();
        assertEquals(1, on(test1).set("I_INT1", 1).get("I_INT1"));
        assertEquals(1, on(test1).field("I_INT1").get());
        assertEquals(1, on(test1).set("I_INT2", 1).get("I_INT2"));
        assertEquals(1, on(test1).field("I_INT2").get());
        assertNull(on(test1).set("I_INT2", null).get("I_INT2"));
        assertNull(on(test1).field("I_INT2").get());

        // Static methods
        // --------------
        assertEquals(1, on(Test1.class).set("S_INT1", 1).get("S_INT1"));
        assertEquals(1, on(Test1.class).field("S_INT1").get());
        assertEquals(1, on(Test1.class).set("S_INT2", 1).get("S_INT2"));
        assertEquals(1, on(Test1.class).field("S_INT2").get());
        assertNull(on(Test1.class).set("S_INT2", null).get("S_INT2"));
        assertNull(on(Test1.class).field("S_INT2").get());
    }

    @Test
    public void testFieldAdvanced() {
        on(Test1.class).set("S_DATA", on(Test1.class).create())
                      .field("S_DATA")
                      .set("I_DATA", on(Test1.class).create())
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

    @Before
    public void setUp() {
        Test1.S_INT1 = 0;
        Test1.S_INT2 = null;
        Test1.S_DATA = null;
    }
}
