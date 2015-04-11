package com.surpass.aisave.aifactory;


import com.nikhaldimann.inieditor.IniEditor;

/**
 * 自动将object/List<Object>对象中指定的属性持久化到配置文件中或是从配置文件加载
 * @author surpassE
 * @version 1.0.0
 * @since 2015-04-11
 *
 */
public interface AiSaveI {
	
	/**
	 * 执行对象持久化操作
	 * @param obj
	 * @param conf
	 */
	public void saveObjectToConfig(Object obj, IniEditor conf);

	/**
	 * 获得boolean类型值得取值方法
	 * @param fieldName
	 * @return
	 */
	public String getIsMethod(String fieldName);

	/**
	 * 获得getXXX方法
	 * @param fieldName
	 * @return
	 */
	public String getGetMethod(String fieldName);

	/**
	 * 获得setXXX方法
	 * @param fieldName
	 * @return
	 */
	public String getSetMethod(String fieldName);
	
	/**
	 * 将field首个字母转为大写
	 * @param filed
	 * @return
	 */
	public String toUpcaseFirstLetter(String fieldName);
}
