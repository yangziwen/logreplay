package com.sogou.map.logreplay.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sogou.map.logreplay.bean.base.AbstractBean;

@Table
public class Product extends AbstractBean implements Cloneable {

	@Id
	@Column
	private Long id;
	
	@Column
	private String name;
	
	public Product() {}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public Product clone() {
		try {
			return (Product) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

}
