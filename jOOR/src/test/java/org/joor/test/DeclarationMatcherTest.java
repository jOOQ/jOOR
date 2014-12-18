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

import org.joor.invoke.DeclarationMatcher;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Grzegorz Gajos
 */
public class DeclarationMatcherTest {

    @Test
    public void assignable() throws Exception {
        assertTrue(new DeclarationMatcher(new Class<?>[]{Object.class}).pass(new Class<?>[]{null}).assignable());
        assertTrue(new DeclarationMatcher(new Class<?>[]{Integer.class}).pass(new Class<?>[]{Integer.class}).assignable());
        assertTrue(new DeclarationMatcher(new Class<?>[]{Number.class}).pass(new Class<?>[]{Integer.class}).assignable());
        assertTrue(new DeclarationMatcher(new Class<?>[]{Object.class}).pass(new Class<?>[]{Integer.class}).assignable());

        assertFalse(new DeclarationMatcher(new Class<?>[]{Integer.class}).pass(new Class<?>[]{String.class}).assignable());
        assertFalse(new DeclarationMatcher(new Class<?>[]{String.class}).pass(new Class<?>[]{Object.class}).assignable());
    }

    @Test
    public void distance() throws Exception {
        assertEquals(0, new DeclarationMatcher(new Class<?>[]{Integer.class}).pass(new Class<?>[]{Integer.class}).distance());
        assertEquals(1, new DeclarationMatcher(new Class<?>[]{Number.class}).pass(new Class<?>[]{Integer.class}).distance());
        assertEquals(2, new DeclarationMatcher(new Class<?>[]{Object.class}).pass(new Class<?>[]{Integer.class}).distance());
        assertEquals(3, new DeclarationMatcher(new Class<?>[]{Integer.class}).pass(new Class<?>[]{null}).distance());
        assertEquals(1, new DeclarationMatcher(new Class<?>[]{Object.class}).pass(new Class<?>[]{null}).distance());
        assertEquals(2, new DeclarationMatcher(new Class<?>[]{Object.class, Object.class}).pass(new Class<?>[]{null, null}).distance());

        assertEquals(Integer.MAX_VALUE, new DeclarationMatcher(new Class<?>[]{Integer.class}).pass(new Class<?>[]{String.class}).distance());
        assertEquals(Integer.MAX_VALUE, new DeclarationMatcher(new Class<?>[]{String.class}).pass(new Class<?>[]{Object.class}).distance());
    }
}
