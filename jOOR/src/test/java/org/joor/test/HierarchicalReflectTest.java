/**
 * Copyright (c) 2011-2013, Lukas Eder, lukas.eder@gmail.com
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

import org.junit.Ignore;
import org.junit.Test;

import static org.joor.Reflect.on;
import static org.junit.Assert.assertEquals;

public class HierarchicalReflectTest {
    private Object obj;

    @Test
    public void testPublicMembersAreFoundInHierarchy() throws Exception {
        obj = new HierarchicalSubclass();
        checkMembers(HierarchicalBase.PUBLIC_RESULT, "pub_base");
    }

    private void checkMembers(String expectedResult, String name) {
        String methodName = name + "_method";
        String fieldName = name + "_field";

        // each of those four ways of accessing members uses different code, so have to test them all:
        assertEquals(expectedResult, on(obj).call(methodName, 1).get());
        assertEquals(expectedResult, on(obj).get(fieldName));
        assertEquals(expectedResult, on(obj).field(fieldName).get());
        assertEquals(expectedResult, on(obj).fields().get(fieldName).get());
    }

    @Test
    public void testPrivateMembersAreFoundInHierarchy() throws Exception {
        obj = new HierarchicalSubclass();
        checkMembers(HierarchicalBase.PRIVATE_RESULT, "very_priv");
    }

    @Test
    public void testPrivateMembersAreFoundOnDeclaringClass() throws Exception {
        obj = new HierarchicalSubclass();
        checkMembers(HierarchicalSubclass.PRIVATE_RESULT, "priv");

        obj = new HierarchicalBase();
        checkMembers(HierarchicalBase.PRIVATE_RESULT, "priv");
    }

    @Test
    public void testSetsVeryPrivateFields() throws Exception {
        obj = new HierarchicalSubclass();
        String privateResult2 = HierarchicalBase.PRIVATE_RESULT + 2;
        on(obj).set("very_priv_field", privateResult2);
        assertEquals(privateResult2, on(obj).get("very_priv_field"));
    }
}
