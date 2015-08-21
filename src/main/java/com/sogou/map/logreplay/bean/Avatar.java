package com.sogou.map.logreplay.bean;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sogou.map.logreplay.bean.Image.Type;
import com.sogou.map.logreplay.bean.base.AbstractBean;

@Table(name = "avatar")
public class Avatar extends AbstractBean {
	
	public static final String DEFAULT_AVATAR = "/img/default-avatar.jpg";
	
	public static Image.Type[] IMAGE_TYPES = {
		Image.Type.small, Image.Type.middle, Image.Type.large
	};

	@Id
	@Column
	private Long id;
	
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "image_id")
	private Long imageId;
	
	@Column
	private Type type;
	
	@Column(name = "create_time")
	private Timestamp createTime;
	
	public Avatar() {}
	
	public Avatar(Long userId, Long imageId, Image.Type type) {
		this.userId = userId;
		this.imageId = imageId;
		this.type = type;
		this.createTime = new Timestamp(System.currentTimeMillis());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getImageId() {
		return imageId;
	}

	public void setImageId(Long imageId) {
		this.imageId = imageId;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
	
	
}
