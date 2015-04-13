package com.surpass.aisave.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtil {

	/**
	 * 执行setXXX方法
	 * 
	 * @param obj
	 * @param field
	 * @param value
	 */
	public static void callSetMethod(Object obj, Field field, Object value){
		try {
			String methodName = getSetMethod(field.getName());
			Method method = obj.getClass().getDeclaredMethod(methodName, Object.class);
			method.invoke(obj, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 通过isXXX()或是getXXX()获得obj中对应的属性值
	 * 
	 * @param obj
	 * @param field
	 * @return
	 */
	public static Object callIsOrGetMethod(Object obj, Field field){
		Object value = null;
		try {
			if(obj != null){
				Class<?> clazz = obj.getClass();
				Object type = field .getType();
				//属性名称
				String fieldName = field.getName();
				String methodName = null;
				if(type instanceof Boolean && fieldName.startsWith("is")){
					methodName = getIsMethod(fieldName);
				}else{
					methodName = getGetMethod(fieldName);
				}
				Method method = clazz.getDeclaredMethod(methodName);
				value = method.invoke(obj);
			}
		} catch (Exception e) {}
		return value;
	}

	/**
	 * 调用指定方法名称的方法并获得返回值
	 * 
	 * @param obj
	 * @param methodName
	 * @return
	 */
	public static Object callAssignedMethod(Object obj, String methodName){
		Object value = null;
		try {
			if(obj != null){
				Class<?> clazz = obj.getClass();
				Method method = clazz.getDeclaredMethod(methodName);
				value = method.invoke(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	/**
	 * 获得boolean类型值得取值方法
	 * @param fieldName
	 * @return
	 */
	public static String getIsMethod(String fieldName){
		return "is" + toUpcaseFirstLetter(fieldName);
	}

	public static String getGetMethod(String fieldName){
		return "get" + toUpcaseFirstLetter(fieldName);
	}

	public static String getSetMethod(String fieldName){
		return "set" + toUpcaseFirstLetter(fieldName);
	}
	
	/**
	 * 将field首个字母转为大写
	 * @param filed
	 * @return
	 */
	public static String toUpcaseFirstLetter(String fieldName){
		//获得首字符
		String head = fieldName.substring(0,1);
		//将首字符转为大写
		String upperCaseHead = head.toUpperCase();
		return fieldName.replaceFirst(head, upperCaseHead);
	}
}
