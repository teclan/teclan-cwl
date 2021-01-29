/**
 * 
 */
package com.teclan.cwl.utils;



public class Assert {
	

	public static boolean assertNotNull(Object value) {
		return !assertNull(value);
	}
	
	public static boolean assertNull(Object value) {
		return value==null||"".equals(value.toString().trim());
	}
	
	public static boolean assertEquals(Object value1,Object value2) {
		
		if(value1==null&&value2==null){
			return true;
		}
		if(value1==null&&value2!=null){
			return false;
		}
		
		return value1.equals(value2);
	}
	
public static boolean assertNotEquals(Object value1,Object value2) {
		return !assertEquals(value1, value2);
	}
}
