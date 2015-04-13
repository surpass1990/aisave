package com.surpass.aisave.ai;

import java.util.List;

import com.nikhaldimann.inieditor.IniEditor;
import com.surpass.aisave.section.SectionServiceI;
import com.surpass.aisave.section.SectionServiceImpl;
/**
 * 读写配置信息
 * 
 * @author surpassE
 * @version 1.0.0
 * @since 2015-04-13
 *
 */
public class AiReadAndSaveImpl implements AiReadAndSaveI{
	
	private static SectionServiceI sectionServiceI = SectionServiceImpl.regist();
	private static AiReadAndSaveI aiReadAndSaveI = new AiReadAndSaveImpl();
	
	public static AiReadAndSaveI regist(){
		return aiReadAndSaveI;
	}
	
	/**
	 * 将object持久化到配置文件中
	 */
	public void saveObjectToConfig(Object obj, IniEditor conf){
		if(obj instanceof List<?>){
			List<?> objList = (List<?>)obj;
			sectionServiceI.saveObjectListToConfig(objList, conf);
		}else{
			sectionServiceI.saveSingleObjectToConfig(obj, conf);
		}
	}
	
	/**
	 * 将section解析并保存到一个新的Object对象中
	 * @param conf
	 * @param clazz
	 * @param params 如果params不为空那么第一个参数必须是0或是1，1表示查询params中匹配的节点、0表示除params中指定的节点以外的所有节点
	 * @return
	 */
	public List<Object> readSectionToNewObject(IniEditor conf, Class<?> clazz, String... params ){
		return sectionServiceI.readSectionToObject(conf, null, clazz, params);
	}
	
	/**
	 * 将section解析并保存到参数的obj中
	 * @param conf
	 * @param obj
	 * @param params	如果params不为空那么第一个参数必须是0或是1，1表示查询params中匹配的节点、0表示除params中指定的节点以外的所有节点
	 * @return
	 */
	public List<Object> readSectionToExistObject(IniEditor conf, Object obj, String... params){
		return sectionServiceI.readSectionToObject(conf, obj, null, params);
	}
}
