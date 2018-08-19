/**
 * @author Alhassan Reda * 
 */
package org.joor;

import java.util.LinkedList;

/**
 * A wrapped for an {@link Object} or {@link Class} upon which reflective calls
 * can be made.
 * this class Use When You need to wrapper object from path
 * 
 * path like this : "A/B/method()/Array/[0]/field"
 * <p>
 * field :
 * You can Call in the path any field or method follow up by "/"
 *  An example : a/b   field a in the object class have field b 
 *  
 * Method :
 * You can Call any methods with out parameter to return value or not
 * You can Call any methods have parameter Primitive type and add value throw the path
 * An examples :
 *  method1(long 5 , double 5.01) long , double parameters method with value 5 and 5.01
 *  method2(String bb) String parameter method with value bb
 *  method3(bb) String parameter method with value bb 
 *  method4(null) parameter method with value null 
 *  method5(true) boolean parameter method with value true 
 *  
 *  Array : 
 *  You can Call index of any array by calling [index]
 *  An example : b/[0] mean get index 0 from array b 
 *  
 * <p>
 * An example of using <code>ReflectFactory</code> is <code><pre>
 * // Static import all reflection methods to decrease verbosity
 * import static org.joor.Reflect.*;
 *
 * getObject("java.lang.String","java.lang.Object")
 *@version 1.0
 *@author alhassan reda
 */

public final class ReflectFactory {
	
	private static final String Pharchar_DASH = "/"; 
	private static final String METHOD_END_DASH = ")";
	private static final String METHOD_START_DASH = "(";
	private static final String ARRAY_START_DASH = "["; 
	private static final String ARRAY_END_DASH = "]"; 
	private static final String DASH = ",";
	private static final String SPACE = " ";
	private static final String DOT = ".";
	/**
	 * use this method when you need to call same path multiple times 
	 * this make it faster 10 times normal call 
	 * getFrom Reflect list 
	 * @param path path of wrapped object you need
	 * @param obj the root object you want to start with
	 * @param list observer list of last call of the same path
	 * @return the wrapped value of the last element in the path
	 */
	public static <T>T getObject(String path, Object obj , LinkedList<Reflect> list) {
		Reflect ref = Reflect.on(obj);
		list.set(0, ref);
		String[] ph = splitePhraser(path);
		int index = 1; // ignore Root 
		for (String part : ph) {
			if (isMethod(part)) {
				int indexOfParameter = part.indexOf(METHOD_START_DASH);
				ref = ref.call(list.get(index).getAccessibleObject(), 	methodParameter(
						part.substring(indexOfParameter+1, part.length()-1), 
						obj));
				list.set(index,ref);
				index++;
				continue;
			}
			if (isArray(part)) {
				ref = ref.array(arrayIndex(part));
				list.set(index,ref);
				index++;
				continue;
			}
			ref = ref.field(list.get(index).getAccessibleObject());
			list.set(index,ref);

			index++;
		}
		return ref.get();
	}
	/**
	 * this method to make observer list for the path of Reflect class
	 * to use this list later to make the next call faster
	 * @param path path of wrapped object you need
	 * @param obj the root object you want to start with
	 * @return observer list  of Reflect class
	 */
	public static LinkedList<Reflect>  getObserver(String path,Object obj) {	
		LinkedList<Reflect> observer = new LinkedList<>();
		
		Reflect ref = Reflect.on(obj);
		String[] ph = splitePhraser(path);
		observer.add(ref);
		for (String part : ph) {
			
			if (isMethod(part)) {
				int indexOfParameter = part.indexOf(METHOD_START_DASH);
				ref = ref.call(part.substring(0, indexOfParameter), 
						methodParameter(
								part.substring(indexOfParameter+1, part.length()-1), 
								obj));
				observer.add(ref);	
				continue;
			}	
			if (isArray(part)) {
				ref = ref.array(arrayIndex(part));
				observer.add(ref);	
				continue;
			}
			
			ref = ref.field(part);
			observer.add(ref);	
			
		}
		return observer;
	}
	/**
	 * get the object of wrapped values 
	 * @param path path of wrapped object you need
	 * @param obj the root object you want to start with
	 * @return wrapped values
	 */	
	public static <T>T getObject(String path,Object obj) {
		return getObserver(path, obj).getLast().get();
	}
	/**
	 * get the last value from the observer list
	 * it return list.getLast().get(); 
	 * @param list observer list of last call of the same path
	 * @return wrapped values
	 */
	public static <T>T getObject(LinkedList<Reflect> list) {
		return list.getLast().get();
	}
	/**
	 * this method invoke method or change variables or array value
	 * @param path path of wrapped object you need
	 * @param obj the root object you want to start with
	 * @param val value of needed Object
	 * @param observerList if you need fast way
	 * @return observer list to use in other time 
	 */
	public static LinkedList<Reflect> setObject(String path, Object obj , Object val,LinkedList<Reflect> observerList) {
		if (observerList == null) {
			observerList = getObserver(path, obj);
		}else {
			getObject(path, obj, observerList);
		}
		
		Reflect ref = observerList.getLast();
		
		if (ref.isAccessibleObjectTypeMethod()) {
			return observerList;
		}
		ref.set(ref.getAccessibleObject(),observerList.get(observerList.size()-2).get(), val);
		return observerList;
	}
	
	/**
	 * 
	 * @param part
	 * @return array index
	 */
	private static int arrayIndex(String part) {
		//TODO can check forEach values in Array and return right one; 
		return Integer.parseInt(part.substring(1, part.length()-1));
	}
	/**
	 * 
	 * @param part
	 * @param obj
	 * @return object Array from the method parameter
	 */
	private static Object[] methodParameter(String part,Object obj){
		if (part.isEmpty()) {
			return new Object[] {};
		}
		String[] arr = spliteMethodParameter(part);
		if (arr == null||arr.length == 0) {
			return new Object[] {};
		}
		Object ob[] = new Object[arr.length];
		for (int i = 0; i < ob.length; i++) {
			// TODO if contain Dot will return Reflect
			ob[i] = getPrimitiveValue(arr[i].split(SPACE));
		}
		
		return ob;
	}
	/**
	 * 
	 * @param w 
	 * @return value object of parameter
	 */
	private static Object getPrimitiveValue(String[] w) {
		if (w.length==1) {
			
			if("null".equals(w[0])) {
				return null;
			}else if ("true".equals(w[0])){
				return true;
			}else if ("false".equals(w[0])){
				return false;
			}		
			return w[0];
		}
		if ("int".equals(w[0])) {
			return (int)Integer.parseInt(w[1]);
		}else if ("long".equals(w[0])) {
			return (long) Long.parseLong(w[1]);
		}else if ("double".equals(w[0])) {
			return  (double)Double.parseDouble(w[1]);
		}else if ("short".equals(w[0])) {
			return  (short)Short.parseShort(w[1]);
		}else if ("byte".equals(w[0])) {
			return  (byte)Byte.parseByte(w[1]);
		}else if ("float".equals(w[0])) {
			return  (float)Float.parseFloat(w[1]);
		}else if ("char".equals(w[0])) {
			return  (char)w[1].charAt(0);
		}else if ("boolean".equals(w[0])) {
			return  (boolean)Boolean.parseBoolean(w[1]);
		}
		
		return null;
	}
	private static String[] splitePhraser(String path) {
		return path.split(Pharchar_DASH);
	}
	private static boolean isMethod(String part) {
		return part.endsWith(METHOD_END_DASH);
	}
	
	private static boolean isArray(String part) {
		return part.endsWith(ARRAY_END_DASH);
	}
	
	private static String[] spliteMethodParameter(String part) {
		return part.split(DASH);
	}

	
	
}
