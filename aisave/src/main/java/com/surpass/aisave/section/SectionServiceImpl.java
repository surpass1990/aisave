package com.surpass.aisave.section;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nikhaldimann.inieditor.IniEditor;
import com.surpass.aisave.annotion.IgnoreProperties;
import com.surpass.aisave.annotion.Se;
import com.surpass.aisave.model.Options;
import com.surpass.aisave.model.Section;
import com.surpass.aisave.model.SectionField;
import com.surpass.aisave.util.ReflectUtil;

public class SectionServiceImpl implements SectionServiceI{
	private static SectionServiceI sectionServiceI = new SectionServiceImpl();
	
	private SectionServiceImpl(){}
	
	public static SectionServiceI regist(){
		return sectionServiceI;
	}

	/**
	 * 将list中所有的属性持久化指定的配置文件中
	 * 
	 * @params conf
	 * @param list
	 */
	public void saveObjectListToConfig(List<?> objList, IniEditor conf){
		List<SectionField> list = this.getSectionFieldList(objList);
		List<Section> sectionList = this.getSectionList(list);
		for(Section section : sectionList){
			this.saveSingleObjectToConfig(section, conf);
		}
	}
	
	/**
	 * 将一个section对象保存到配置文件中
	 * @param section
	 * @param conf
	 */
	public void saveSingleObjectToConfig(Object obj, IniEditor conf){
		SectionField sectionField = this.getSectionField(obj);
		Section section = this.getSection(sectionField);
		String sectionName = section.getSectionName();
		conf.addSection(sectionName);
		List<String> optList = section.getSectionOptOrder();
		for(String name : optList){
			Options opt = section.getSectionOptMap().get(name);
			if(opt.isBlankLine()){
				conf.addBlankLine(sectionName);
			}
			if(opt.getComment() != null && opt.getComment().length() > 0){
				conf.addComment(sectionName, opt.getComment());
			}
			conf.set(sectionName, opt.getName(), opt.getValue());
		}
		if(section.isBlankLine()){
			conf.addBlankLine(sectionName);
		}
	}

	/**
	 * 获得list集合里对象中含有@Se注解的所有属性
	 * 
	 * @param list
	 * @return
	 */
	private List<SectionField> getSectionFieldList(List<?> list){
		List<SectionField> sectionFieldList = new ArrayList<SectionField>();
		if(list != null && list.size() > 0){
			for(Object obj : list){
				SectionField sectionField = this.getSectionField(obj);
				if(sectionField != null){
					sectionFieldList.add(sectionField);
				}
			}
		}
		return sectionFieldList;
	}

	/**
	 * 
	 * 获得obj中含有@Se注解的所有属性
	 * 
	 * @param obj
	 * @return
	 */
	private SectionField getSectionField(Object obj){
		SectionField sectionField = null;
		try {
			sectionField = new SectionField();
			Class<?> clazz = obj.getClass();
			Field[] fieldArr = clazz.getDeclaredFields();
			if(fieldArr != null){
				for(Field field : fieldArr){
					//获得属性上的@Se注解
					Se se = field.getAnnotation(Se.class);
					if(se != null){
						//判断属性是不section节点属性
						if(se.isSection()){
							sectionField.setSectionNameField(field);
						}else{
							sectionField.getSectionOptionsList().add(field);
							//将属性的名称或是se中的name作为key field作为value，存入map中
							//以便在掉那个set方法时可以直接通过name获得对应的属性
							if(se.name() != null && se.name() != "" && se.name().length() > 0){
								sectionField.getSectionOptionsMap().put(se.name(), field);
							}else{
								sectionField.getSectionOptionsMap().put(field.getName(), field);
							}
						}
						sectionField.setObj(obj);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sectionField;
	}

	/**
	 * 获得section集合
	 * 
	 * @param list
	 * @return
	 */
	private List<Section> getSectionList(List<SectionField> list){
		List<Section> sectionList = new ArrayList<Section>();
		if(list != null){
			for(SectionField sectionField : list){
				Section section = this.getSection(sectionField);
				sectionList.add(section);
			}
		}
		return sectionList;
	}

	/**
	 * 通过属性及属性上的注解获得要持久化到配置的name和value
	 * 
	 * @param sectionField
	 * @return
	 */
	private Section getSection(SectionField sectionField){
		Section section = new Section();
		Object obj = sectionField.getObj();
		//获得节点name的属性
		Field sectionNameField = sectionField.getSectionNameField();
		Se se = sectionNameField.getAnnotation(Se.class);
		//调用注解中指定方法名称的方法，获得的返回值暂时不做处理，一般情况下，是不会有返回值的
		ReflectUtil.callAssignedMethod(obj, se.assignedMethod());
		//获得节点name的值
		String sectionName = ReflectUtil.callIsOrGetMethod(obj, sectionNameField).toString();
		section.setSectionName(sectionName);
		//解析除节点以外的所有属性的值及注解含义
		if(sectionField.getSectionOptionsList() != null){
			for(Field optField : sectionField.getSectionOptionsList()){
				Options opt = new Options();
				se = optField.getAnnotation(Se.class);
				//调用注解中assignedMethod指定的方法
				ReflectUtil.callAssignedMethod(obj, se.assignedMethod());
				//获得属性值
				Object value = ReflectUtil.callIsOrGetMethod(obj, optField);
				//如果value为空，且注解中nullToSave=false则忽略此属性
				if(value == null && !se.nullToSave()){
					continue;
				}
				//如果需要忽略属性的set集合中包含这个属性，那么忽略此属性
				if(section.getIgnoreSet().contains(optField.getName())){
					section.getSectionOptOrder().remove(optField.getName());
					section.getSectionOptMap().remove(optField.getName());
					continue;
				}
				//设置name=value中name值,如果注解中没有指定，那么name为属性名称，否则name为注解中name值
				opt.setName(optField.getName());
				String name = se.name();
				if(name != null && name != "" && name.length() > 0){
					opt.setName(name);
				}

				//设置name=value中value值
				opt.setValue("");
				if(value != null){
					opt.setValue(value.toString());
				}
				//通过value与注解中flag比较判断 ，如果匹配，params中指定的属性值将不会被保存到配置文件中
				IgnoreProperties[] iproArr = se.ignoreProperties();
				if(iproArr != null){
					for(IgnoreProperties ipro : iproArr){
						String flag = ipro.flag();
						String[] params = ipro.params();
						if(params != null){
							//如果flag定义为NULL那么判断value是不是为null如果为null那么操作需要忽略的属性
							//如果flag定义为非NULL值，那么判断value值时一定要要判断value是否为null，因为要执行value.toString()方法
							if("NULL".equals(flag)){
								if(value == null){
									this.addParamsToSet(section, params);
								}
							}else{
								if(value != null && flag.equals(value.toString())){
									this.addParamsToSet(section, params);
								}
							}
						}
					}
				}
				section.setBlankLine(se.blankLine());
				section.setComment(se.comment());
				section.getSectionOptOrder().add(opt.getName());
				section.getSectionOptMap().put(opt.getName(), opt);
			}
		}
		return section;
	}

	/**
	 * 将集合中参数存储到section属性的ignoreSet中
	 * @param section
	 * @param params
	 */
	private void addParamsToSet(Section section, String[] params){
		if(params != null){
			for(String param : params){
				section.getIgnoreSet().add(param);
			}
		}
	}
	
	
	
	//********************************* 读取配置信息***************************************************
	
	
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
	public List<Object> readSectionToObject(IniEditor conf, Object obj, Class<?> clazz, String... params){
		List<Object> objList = new ArrayList<Object>();
		List<Section> sectionList = this.getSectionAssignedOrExcept(conf, params);
		try {
			for(Section section : sectionList){
				if(obj == null && clazz != null){
					obj = clazz.newInstance();
				}
				//获得obj下所有含@Se注解的属性及对应关系
				SectionField sectionField = this.getSectionField(obj);
				
				//设置默认的sectionName值
				Field sectionNameField = sectionField.getSectionNameField();
				ReflectUtil.callSetMethod(obj, sectionNameField, section.getSectionName());
				
				//设置section节点下对应属性的值
				Map<String, Options> map = section.getSectionOptMap();
				for(String name : map.keySet()){
					String value = map.get(name).getValue();
					Field sectionOptionsField = sectionField.getSectionOptionsMap().get(name);
					ReflectUtil.callSetMethod(obj, sectionOptionsField, value);
				}
				objList.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objList;
	}
	
	
	/**
	 * 取config中包含params中的属性值，或是取config配置文件中属性值后剔除params中指定的属性值
	 * 
	 * @param conf
	 * @param flag true表示取params中指定的属性
	 * @param params	如果params不为空那么第一个参数必须是0或是1，1表示查询params中匹配的节点、0表示除params中指定的节点以外的所有节点
	 * @return
	 */
	private List<Section> getSectionAssignedOrExcept(IniEditor conf, String... params){
		List<Section> sectionList = new ArrayList<Section>();
		List<String> sectionNameList = conf.sectionNames();
		if(sectionNameList != null){
			for(String sectionName : sectionNameList){
				if(this.containSection(sectionName, params)){
					sectionList.add(this.getSingleSection(conf, sectionName));
				}
			}
		}
		return sectionList;
	}
	
	/**
	 * 获得单独节点属性节点信息
	 * 
	 * @param conf
	 * @param sectionName
	 * @return
	 */
	private Section getSingleSection(IniEditor conf, String sectionName){
		Section section = new Section();
		section.setSectionName(sectionName);
		List<String> optList = conf.optionNames(sectionName);
		if(optList != null){
			for(String optName : optList){
				Options opt = new Options();
				opt.setName(optName);
				opt.setValue(conf.get(sectionName, optName));
				section.getSectionOptMap().put(optName, opt);
				section.getSectionOptOrder().add(optName);
			}
		}
		return section;
	}
	
	/**
	 * 判断params是否包含指定sectionName值
	 * 
	 * @param sectionName
	 * @param params	params第一参数只能是1/0,1表示sectionName在params的数组中匹配时返回true， 0表示在params中匹配值返回false
	 * @return
	 */
	private boolean containSection(String sectionName, String... params){
		if(params != null && params.length > 0){
			if("1".equals(params[0])){
				for(int i = 1; i < params.length; i++){
					if(sectionName != null && sectionName.equals(params[i])){
						return true;
					}
				}
				return false;
			}else if("0".equals(params[0])){
				for(int i = 1; i < params.length; i++){
					if(sectionName != null && sectionName.equals(params[i])){
						return false;
					}
				}
				return true;
			}
		}
		return true;
	}
}
