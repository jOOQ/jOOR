### Overview

jOOR stands for jOOR Object Oriented Reflection. It is a simple wrapper for the java.lang.reflect package.

jOOR's name is inspired by jOOQ, a fluent API for SQL building and execution.


### Dependencies

None!


### Simple example

````java
// All examples assume the following static import:
import static org.joor.Reflect.*;

String world = on("java.lang.String")  // Like Class.forName()
                .create("Hello World") // Call most specific matching constructor
                .call("substring", 6)  // Call most specific matching substring() method
                .call("toString")      // Call toString()
                .get();                // Get the wrapped object, in this case a String
````


### Proxy abstraction

jOOR also gives access to the java.lang.reflect.Proxy API in a simple way:

````java
public interface StringProxy {
  String substring(int beginIndex);
}

String substring = on("java.lang.String")
                    .create("Hello World")
                    .as(StringProxy.class) // Create a proxy for the wrapped object
                    .substring(6);         // Call a proxy method
````


### Comparison with standard java.lang.reflect

jOOR code:

````java
Employee[] employees = on(department).call("getEmployees").get();

for (Employee employee : employees) {
  Street street = on(employee).call("getAddress").call("getStreet").get();
  System.out.println(street);
}
````

The same example with normal reflection in Java:

````java
try {
  Method m1 = department.getClass().getMethod("getEmployees");
  Employee employees = (Employee[]) m1.invoke(department);

  for (Employee employee : employees) {
    Method m2 = employee.getClass().getMethod("getAddress");
    Address address = (Address) m2.invoke(employee);

    Method m3 = address.getClass().getMethod("getStreet");
    Street street = (Street) m3.invoke(address);

    System.out.println(street);
  }
}

// There are many checked exceptions that you are likely to ignore anyway 
catch (Exception ignore) {

  // ... or maybe just wrap in your preferred runtime exception:
  throw new RuntimeException(e);
}
````


### Similar projects

Everyday Java reflection with a fluent interface:

 * http://docs.codehaus.org/display/FEST/Reflection+Module
 * http://projetos.vidageek.net/mirror/mirror/

Reflection modelled as XPath (quite interesting!)

 * http://commons.apache.org/jxpath/users-guide.html  


### 中文说明  
测试类：  
```JAVA   
package kale.androidframework;

/**
 * @author Jack Tony
 * @date 2015/7/8
 */
public class Kale {

    private String name;

    private String className;

    Kale() {

    }

    Kale(String clsName) {
        this.className = clsName;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public static void method() {
        
    }
}
```  
测试方案：  
```JAVA
String name = null;
        Kale kale;
        // 【创建类】
        kale = Reflect.on(Kale.class).create().get(); // 无参数 
        kale = Reflect.on(Kale.class).create("kale class name").get();// 有参数
        System.err.println("------------------> class name = " + kale.getClassName());

        // 【调用方法】
        Reflect.on(kale).call("setName","调用setName");// 多参数
        System.err.println("调用方法：name = " + Reflect.on(kale).call("getName"));// 无参数
        
        // 【得到变量】
        name = Reflect.on(kale).field("name").get();// 复杂
        name = Reflect.on(kale).get("name");// 简单
        System.err.println("得到变量值： name = " + name);
        
        // 【设置变量的值】
        Reflect.on(kale).set("className", "hello");
        System.err.println("设置变量的值： name = " + kale.getClassName());
        System.err.println("设置变量的值： name = " + Reflect.on(kale).set("className", "hello2").get("className"));	  
```


