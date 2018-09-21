package io.github.yangziwen.logreplay.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import io.github.yangziwen.logreplay.bean.base.AbstractBean;

@Table
public class Product extends AbstractBean implements Cloneable {

	@Id
	@Column
	private Long id;

	@Column
	private String name;

	public Product() {}

	@Override
	public Long getId() {
		return id;
	}

	@Override
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
			logger.error("error happens when clone Product[{}]", this, e);
			return null;
		}
	}

}
