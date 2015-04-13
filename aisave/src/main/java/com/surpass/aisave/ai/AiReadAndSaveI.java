package com.surpass.aisave.ai;

import java.util.List;

import com.nikhaldimann.inieditor.IniEditor;
/**
 * 解析配置文件中内容转为object
 * 自动将object/List<Object>对象中指定的属性持久化到配置文件中或是从配置文件加载
 * 
 * @author surpassE
 * @version 1.0.0
 * @since 2015-04-12
 *
 */
public interface AiReadAndSaveI {
	/**
	 * 执行对象持久化操作
	 * @param obj
	 * @param conf
	 */
	public void saveObjectToConfig(Object obj, IniEditor conf);
	
	/**
	 * 将section解析并保存到一个新的Object对象中
	 * @param conf
	 * @param clazz
	 * @param params 如果params不为空那么第一个参数必须是0或是1，1表示查询params中匹配的节点、0表示除params中指定的节点以外的所有节点
	 * @return
	 */
	public List<Object> readSectionToNewObject(IniEditor conf, Class<?> clazz, String... params );
	
	/**
	 * 将section解析并保存到参数的obj中
	 * @param conf
	 * @param obj
	 * @param params	如果params不为空那么第一个参数必须是0或是1，1表示查询params中匹配的节点、0表示除params中指定的节点以外的所有节点
	 * @return
	 */
	public List<Object> readSectionToExistObject(IniEditor conf, Object obj, String... params);
}
