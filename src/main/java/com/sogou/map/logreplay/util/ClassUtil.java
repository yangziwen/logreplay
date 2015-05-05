package com.sogou.map.logreplay.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ClassUtil {

	private ClassUtil(){}
	
	@SuppressWarnings("rawtypes")
	public static Type[] getSuperClassGenericTypes(Class clazz) {
		Type genType = clazz.getGenericSuperclass();
		if(!(genType instanceof ParameterizedType)){
			throw new IllegalStateException("Seems no valid ParameterizedType exist!");
		}
		return ((ParameterizedType)genType).getActualTypeArguments();
	}
	
	@SuppressWarnings("rawtypes")
	public static Class getSuperClassGenericType(Class clazz, int index){
		Type[] params = getSuperClassGenericTypes(clazz);
		if(index < 0 || index >= params.length){
			throw new IllegalStateException("Seems no valid ParameterizedType exist at the position of " + index + "!");
		}
		return (Class)params[index];
	}
	
}
