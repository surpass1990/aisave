package com.surpass.aisave.aifactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.nikhaldimann.inieditor.IniEditor;
import com.surpass.aisave.annotion.IgnoreProperties;
import com.surpass.aisave.annotion.Se;
import com.surpass.aisave.model.Options;
import com.surpass.aisave.model.Section;
import com.surpass.aisave.model.SectionField;


public class AiSaveImpl implements AiSaveI{

	
	public static AiSaveI regist(){
		return new AiSaveImpl();
	}
	
	public void saveObjectToConfig(Object obj, IniEditor conf){
		if(obj instanceof List<?>){
			List<?> objList = (List<?>)obj;
			List<SectionField> list = this.getSectionFieldList(objList);
			List<Section> sectionList = this.getSectionList(list);
			this.saveObjectListToConfig(sectionList, conf);
		}else{
			SectionField sectionField = this.getSectionField(obj);
			Section section = this.getSection(sectionField);
			this.saveSingleObjectToConfig(section, conf);
		}
	}
	
	/**
	 * 将list中所有的属性持久化指定的配置文件中
	 * 
	 * @params conf
	 * @param list
	 */
	private void saveObjectListToConfig(List<Section> list, IniEditor conf){
		for(Section section : list){
			this.saveSingleObjectToConfig(section, conf);
		}
	}
	
	/**
	 * 将一个section对象保存到配置文件中
	 * @param section
	 * @param conf
	 */
	private void saveSingleObjectToConfig(Section section, IniEditor conf){
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
		this.callAssignedMethod(obj, se.assignedMethod());
		//获得节点name的值
		String sectionName = this.callIsOrGetMethod(obj, sectionNameField).toString();
		section.setSectionName(sectionName);
		//解析除节点以外的所有属性的值及注解含义
		if(sectionField.getSectionOptionsList() != null){
			for(Field optField : sectionField.getSectionOptionsList()){
				Options opt = new Options();
				se = optField.getAnnotation(Se.class);
				//调用注解中assignedMethod指定的方法
				this.callAssignedMethod(obj, se.assignedMethod());
				//获得属性值
				Object value = this.callIsOrGetMethod(obj, optField);
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

	/**
	 * 通过isXXX()或是getXXX()获得obj中对应的属性值
	 * 
	 * @param obj
	 * @param field
	 * @return
	 */
	private Object callIsOrGetMethod(Object obj, Field field){
		Object value = null;
		try {
			if(obj != null){
				Class<?> clazz = obj.getClass();
				Object type = field .getType();
				//属性名称
				String fieldName = field.getName();
				String methodName = null;
				if(type instanceof Boolean && fieldName.startsWith("is")){
					methodName = this.getIsMethod(fieldName);
				}else{
					methodName = this.getGetMethod(fieldName);
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
	private Object callAssignedMethod(Object obj, String methodName){
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
	public String getIsMethod(String fieldName){
		return "is" + this.toUpcaseFirstLetter(fieldName);
	}

	public String getGetMethod(String fieldName){
		return "get" + this.toUpcaseFirstLetter(fieldName);
	}

	public String getSetMethod(String fieldName){
		return "set" + this.toUpcaseFirstLetter(fieldName);
	}
	
	/**
	 * 将field首个字母转为大写
	 * @param filed
	 * @return
	 */
	public String toUpcaseFirstLetter(String fieldName){
		//获得首字符
		String head = fieldName.substring(0,1);
		//将首字符转为大写
		String upperCaseHead = head.toUpperCase();
		return fieldName.replaceFirst(head, upperCaseHead);
	}
}
