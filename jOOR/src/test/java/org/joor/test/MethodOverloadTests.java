package org.joor.test;

import org.joor.Reflect;
import org.junit.Assert;
import org.junit.Test;

public class MethodOverloadTests {
    // count each overload invocation
    private static int sObject = 0;
    private static int sNumber = 0;
    private static int sInteger = 0;
    private int mObject = 0;
    private int mNumber = 0;
    private int mInteger = 0;

    private static void sOverload(Object o) {
        MethodOverloadTests.sObject++;
    }

    private static void sOverload(Number number) {
        MethodOverloadTests.sNumber++;
    }

    private static void sOverload(Integer integer) {
        MethodOverloadTests.sInteger++;
    }

    private void mOverload(Object o) {
        this.mObject++;
    }

    private void mOverload(Number number) {
        this.mNumber++;
    }

    private void mOverload(Integer integer) {
        this.mInteger++;
    }

    @Test
    public void testStaticMethodsOverload() {
        Reflect reflect = Reflect.onClass(MethodOverloadTests.class);
        reflect.call("sOverload", Integer.parseInt("1"));
        Assert.assertEquals(1, MethodOverloadTests.sInteger);

        reflect.call("sOverload", Long.parseLong("1"));
        Assert.assertEquals(1, MethodOverloadTests.sNumber);

        reflect.call("sOverload", "Hello world!");
        Assert.assertEquals(1, MethodOverloadTests.sObject);
    }

    @Test
    public void testMemberMethodsOverload() {
        MethodOverloadTests instance = new MethodOverloadTests();
        Reflect reflect = Reflect.on(instance);
        reflect.call("mOverload", Integer.parseInt("1"));
        Assert.assertEquals(1, instance.mInteger);

        reflect.call("mOverload", Long.parseLong("1"));
        Assert.assertEquals(1, instance.mNumber);

        reflect.call("mOverload", "Hello world!");
        Assert.assertEquals(1, instance.mObject);
    }
}
