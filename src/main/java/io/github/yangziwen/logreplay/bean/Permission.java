package io.github.yangziwen.logreplay.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import io.github.yangziwen.logreplay.bean.base.AbstractBean;

@Table(name = "permission")
public class Permission extends AbstractBean {
	
	@Id
	@Column
	private Long id;
	
	@Column
	private String target;
	
	@Column
	private String action;
	
	public Permission() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	public String toString() {
		return target.toLowerCase() + ":" + action.toLowerCase();
	}
	
	public enum Target {
		Page_Info,
		Tag_Info,
		User,
		Role,
		Inspection_Record,
		Operation_Record
		;
		
		public String oper(Action action) {
			return this.name().toLowerCase() + ":" + action.name().toLowerCase();
		}
		
		public String oper(Action... actions) {
			StringBuilder buff = new StringBuilder()
				.append(this.name().toLowerCase())
				.append(":")
				.append(actions[0].name().toLowerCase())
			;
			for(int i = 1; i < actions.length; i++) {
				buff.append(",").append(actions[i].name().toLowerCase());
			}
			return buff.toString();
		}
		
		public String view() {
			return oper(Action.View);
		}
		
		public String modify() {
			return oper(Action.Modify);
		}
	}
	
	public enum Action {
		View,
		Modify,
		;
	}
	
}
