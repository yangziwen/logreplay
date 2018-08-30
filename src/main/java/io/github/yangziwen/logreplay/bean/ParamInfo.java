package io.github.yangziwen.logreplay.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import io.github.yangziwen.logreplay.bean.base.AbstractBean;

/**
 * 单个参数信息
 */
@Table(name = "param_info")
public class ParamInfo extends AbstractBean {

	@Id
	@Column
	private Long id;
	
	@Column(name = "tag_param_id")
	private Long tagParamId;
	
	/**
	 * 参数的名称
	 * 一个参数名可以对应多个参数值
	 * 如果一个参数名只对应一个参数值，
	 * 则表明此参数的值必须存在，但可以是任意内容
	 */
	@Column
	private String name;
	
	/**
	 * 参数的值
	 * 如果一个参数名只对应一个参数值，
	 * 则表明此参数的值必须存在，但可以是任意内容
	 */
	@Column
	private String value;
	
	/**
	 * 针对当前参数名和参数值的描述
	 */
	@Column
	private String description;
	
	public ParamInfo() {}
	
	public ParamInfo(String name, String value, String description) {
		this.name = name;
		this.value = value;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTagParamId() {
		return tagParamId;
	}

	public void setTagParamId(Long tagParamId) {
		this.tagParamId = tagParamId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
