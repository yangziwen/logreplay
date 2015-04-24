package com.sogou.map.logreplay.bean;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.beanutils.PropertyUtils;

import com.sogou.map.logreplay.util.ExcelUtil.DataContainer;

/**
 * 页面信息
 */
@Table(name = "page_info")
public class PageInfo extends AbstractBean implements DataContainer {

	@Id
	@Column
	private Long id;
	
	@Column(name = "product_id")
	private Long productId;
	
	/** 页面编号 **/
	@Column
	private Integer pageNo;
	
	/** 页面名称 **/
	@Column
	private String name;
	
	@Column(name = "create_time")
	private Timestamp createTime;
	
	@Column(name = "update_time")
	private Timestamp updateTime;

	public PageInfo() {}
	
	public PageInfo(Integer pageNo, String name) {
		this.pageNo = pageNo;
		this.name = name;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public Object getColumnValue(String columnKey) {
		try {
			return PropertyUtils.getProperty(this, columnKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
