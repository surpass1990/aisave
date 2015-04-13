package com.surpass.aisave.section;

import java.util.List;

import com.nikhaldimann.inieditor.IniEditor;

public interface SectionServiceI {
	
	/**
	 * 将list中所有的属性持久化指定的配置文件中
	 * 
	 * @params conf
	 * @param list
	 */
	public void saveObjectListToConfig(List<?> list, IniEditor conf);
	
	/**
	 * 将一个obj对象保存到配置文件中
	 * @param obj
	 * @param conf
	 */
	public void saveSingleObjectToConfig(Object obj, IniEditor conf);

	/**
	 * 
	 * 解析section并设置obj属性值
	 * 
	 * @param conf
	 * @param obj
	 * @param clazz
	 * @param params	如果params不为空那么第一个参数必须是0或是1，1表示查询params中匹配的节点、0表示除params中指定的节点以外的所有节点
	 * @return
	 */
	public List<Object> readSectionToObject(IniEditor conf, Object obj, Class<?> clazz, String... params);

}
