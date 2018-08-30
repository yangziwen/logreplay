package io.github.yangziwen.logreplay.dao.base;

public class OperationParsedResult {

	private String key;
	private String oper;
	private String placeholder;
	private String keyWithOper;
	
	public OperationParsedResult(String key, String oper, String placeholder, String keyWithOper) {
		this.key = key;
		this.oper = oper;
		this.placeholder = placeholder;
		this.keyWithOper = keyWithOper;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getOper() {
		return oper;
	}
	public void setOper(String oper) {
		this.oper = oper;
	}
	public String getPlaceholder() {
		return placeholder;
	}
	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}
	public String getKeyWithOper() {
		return keyWithOper;
	}
	public void setKeyWithOper(String keyWithOper) {
		this.keyWithOper = keyWithOper;
	}
	
	public String toSql() {
		return new StringBuilder().append(key).append(oper).append(placeholder).toString();
	}
	
}
