package com.surpass.aisave.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SectionField {

	private Object obj;
	private Field sectionNameField;
	private List<Field> sectionOptionsList = new ArrayList<Field>();
	
	public Object getObj() {
		return obj;
	}
	public void setObj(Object obj) {
		this.obj = obj;
	}
	public Field getSectionNameField() {
		return sectionNameField;
	}
	public void setSectionNameField(Field sectionNameField) {
		this.sectionNameField = sectionNameField;
	}
	public List<Field> getSectionOptionsList() {
		return sectionOptionsList;
	}
	public void setSectionOptionsList(List<Field> sectionOptionsList) {
		this.sectionOptionsList = sectionOptionsList;
	}
	
	
	
	
}
