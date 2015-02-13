package com.sogou.map.logreplay.logprocess.parser;

public abstract class Parser {

	protected String name;
	
	protected int beginIndex;
	
	protected int endIndex;
	
	protected String content;
	
	public String getContent() {
		return content;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public void reset() {
		this.beginIndex = 0;
		this.endIndex = 0;
		this.content = "";
	}
	
	public abstract boolean parse(String log, int offset);
	
}
