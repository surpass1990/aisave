package com.surpass.aisave.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Section {

	private String sectionName;
	private boolean isBlankLine;
	private String comment;
	
	private Map<String, Options> sectionOptMap = new HashMap<String, Options>();
	private List<String> sectionOptOrder = new ArrayList<String>();
	
	private List<String> ignoreList = new ArrayList<String>();
	private Set<String> ignoreSet = new HashSet<String>();

	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public boolean isBlankLine() {
		return isBlankLine;
	}
	public void setBlankLine(boolean isBlankLine) {
		this.isBlankLine = isBlankLine;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public List<String> getIgnoreList() {
		return ignoreList;
	}
	public void setIgnoreList(List<String> ignoreList) {
		this.ignoreList = ignoreList;
	}
	public Set<String> getIgnoreSet() {
		return ignoreSet;
	}
	public void setIgnoreSet(Set<String> ignoreSet) {
		this.ignoreSet = ignoreSet;
	}
	public Map<String, Options> getSectionOptMap() {
		return sectionOptMap;
	}
	public void setSectionOptMap(Map<String, Options> sectionOptMap) {
		this.sectionOptMap = sectionOptMap;
	}
	public List<String> getSectionOptOrder() {
		return sectionOptOrder;
	}
	public void setSectionOptOrder(List<String> sectionOptOrder) {
		this.sectionOptOrder = sectionOptOrder;
	}
}
