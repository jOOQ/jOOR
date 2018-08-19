package org.joor.test;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.joor.Reflect;
import org.joor.ReflectFactory;
import org.junit.Before;
import org.junit.Test;

public class pathTest {
	
	class o {
		b b = new b();
		public o() {
			b.same = new b();
			
		}

		int g = 0;
		
		int getG() {
			return 5;
		}
		
		b getB(String v) {
			return b;
		}
	}
	class b {
		String[] arr = {"A1","A2","A3"};
		int b = 5;
		b same ;
		int getG() {
			return 8;
		}
	}
	
	private o clazz;
	@Before
	public void setUp() {
		clazz = new o();
	}
	@Test
	public void testGetObject2() {
		
		 LinkedList<Reflect> observer = ReflectFactory.getObserver("getB(S)/same/getG()", clazz);
		 assertEquals((int)ReflectFactory.getObject(observer), 8);
		
		 
		 int obj = ReflectFactory.getObject("getB(S)/same/getG()", clazz, observer);
		 assertEquals(obj, 8);
		 
		 
		 //**** Test How Fast it's
		 long A = System.currentTimeMillis();
		 for (int i = 0; i < 100000; i++) {
			 obj = ReflectFactory.getObject("getB(S)/same/getG()", clazz, observer);
			 assertEquals(obj, 8);
		}
		 
		 System.out.println(System.currentTimeMillis()-A);
		  A = System.currentTimeMillis();
		  for (int i = 0; i < 100000; i++) {
				  observer = ReflectFactory.getObserver("getB(S)/same/getG()", clazz);
				 assertEquals((int)ReflectFactory.getObject(observer), 8);
		  }
		System.out.println(System.currentTimeMillis()-A);

	}
	@Test
	public void testGetObject() {
		 Object res = ReflectFactory.getObject("b/arr/[2]", clazz);
		 assertEquals(res, "A3");
		 
		 res = ReflectFactory.getObject("b/b", clazz);
		 assertEquals(res, 5);
		 
		 
		 res = ReflectFactory.getObject("b/same/arr/[0]", clazz);
		 assertEquals(res, "A1");
		 
		 
		 res = ReflectFactory.getObject("getB(S)/same/getG()", clazz);
		 assertEquals(res, 8);
	
		 
	}
	@Test
	public void testSetObject() {
		 
		 LinkedList<Reflect> res = ReflectFactory.setObject("b/arr/[2]",clazz, "Change Array AA44",null);
		 
		 Object ob = ReflectFactory.getObject("b/arr/[2]", clazz,res);
		 assertEquals(ob, "Change Array AA44");
		 
		 
		 res = ReflectFactory.getObserver("g", clazz);
		 int v = ReflectFactory.getObject(res);
		 assertEquals(v, 0);
		 o newClazz = new o(); 
		 ReflectFactory.setObject("g",newClazz, (int)9988,res);
		 
		 v = ReflectFactory.getObject("g", newClazz);
		 assertEquals(v, 9988);
	}
	
}
