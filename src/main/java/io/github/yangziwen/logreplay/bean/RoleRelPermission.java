package io.github.yangziwen.logreplay.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import io.github.yangziwen.logreplay.bean.base.AbstractBean;

@Table(name = "role_rel_permission")
public class RoleRelPermission extends AbstractBean {

	@Id
	@Column
	private Long id;
	
	@Column(name = "role_id")
	private Long roleId;
	
	@Column(name = "permission_id")
	private Long permissionId;
	
	public RoleRelPermission() {}
	
	public RoleRelPermission(Long roleId, Long permissionId) {
		this.roleId = roleId;
		this.permissionId = permissionId;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	
	public Long getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(Long permissionId) {
		this.permissionId = permissionId;
	}

}
