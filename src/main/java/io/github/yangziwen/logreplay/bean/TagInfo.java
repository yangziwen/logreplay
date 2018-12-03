package io.github.yangziwen.logreplay.bean;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import io.github.yangziwen.logreplay.bean.base.AbstractBean;

/**
 * 操作项信息，分为“普通操作项”和“公共操作项”
 * “公共操作项”不关联特定的页面信息(PageInfo)，操作编号>10000
 */
@Table(name = "tag_info")
public class TagInfo extends AbstractBean {

	/** 公共操作项的操作编号必须大于此值 **/
	public static final Integer COMMON_TAG_NO_MIN_VALUE = 10000;

	@Id
	@Column
	private Long id;

	@Column(name = "product_id")
	private Long productId;

	/**  操作项编号 **/
	@Column(name = "tag_no")
	private Integer tagNo;

	/** 操作项名称 **/
	@Column
	private String name;

	/** 对应的页面信息的id **/
	@Column(name = "page_info_id")
	private Long pageInfoId;

	/** 对应的页面编号 **/
	@Column(name = "page_no")	// 冗余，为了查询方便
	private Integer pageNo;

	/** 页面信息对象 **/
	@Transient
	private transient PageInfo pageInfo;

	/** 操作动作id，请见类TagAction **/
	@Column(name = "action_id")
	private Long actionId;

	/** 操作目标id，请见类TagTarget **/
	@Column(name = "target_id")
	private Long targetId;

	/** 注释信息 **/
	@Column
	private String comment;

	/** 首次在app中引入此操作项时，app的版本号 **/
	@Column(name = "origin_version")
	private Integer originVersion;

	/** 校验状态，各种状态请见枚举类InspectStatus **/
	@Column(name = "inspect_status")
	private Integer inspectStatus;

	/** 开发人员校验状态，机制同inspectStatus **/
	@Column(name = "dev_inspect_status")
	private Integer devInspectStatus;

	@Column(name = "create_time")
	private Timestamp createTime;

	@Column(name = "update_time")
	private Timestamp updateTime;

	/** 此操作项是否有相关的参数信息(TagParam) **/
	@Transient
	private Boolean hasParams;

	public TagInfo() {}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Integer getTagNo() {
		return tagNo;
	}

	public void setTagNo(Integer tagNo) {
		this.tagNo = tagNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getPageInfoId() {
		return pageInfoId;
	}

	public void setPageInfoId(Long pageInfoId) {
		this.pageInfoId = pageInfoId;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	public Long getActionId() {
		return actionId;
	}

	public void setActionId(Long actionId) {
		this.actionId = actionId;
	}

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getOriginVersion() {
		return originVersion;
	}

	public void setOriginVersion(Integer originVersion) {
		this.originVersion = originVersion;
	}

	public Integer getInspectStatus() {
		return inspectStatus;
	}

	public void setInspectStatus(Integer inspectStatus) {
		this.inspectStatus = inspectStatus;
	}

	public Integer getDevInspectStatus() {
		return devInspectStatus;
	}

	public void setDevInspectStatus(Integer devInspectStatus) {
		this.devInspectStatus = devInspectStatus;
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

	public Boolean getHasParams() {
		return hasParams;
	}

	public void setHasParams(Boolean hasParams) {
		this.hasParams = hasParams;
	}

	/** 操作项校验结果类型 **/
	public enum InspectStatus {

		SUCCESS(1, "校验正确"), ERROR(2, "校验错误"), UNCHECKED(0, "未校验"), UNKNOWN(-1, "未知状态");

		private int intValue;
		private String description;

		private InspectStatus(int value, String description) {
			this.intValue = value;
			this.description = description;
		}

		public int getIntValue() {
			return intValue;
		}

		public String getDescription() {
			return description;
		}

		public static InspectStatus from(Integer intValue) {
			if (intValue == null) {
				return UNKNOWN;
			}
			for (InspectStatus status: values()) {
				if (intValue.equals(status.intValue)) {
					return status;
				}
			}
			return UNKNOWN;
		}

		public static InspectStatus fromDescription(String description) {
			if (StringUtils.isBlank(description)) {
				return UNKNOWN;
			}
			for (InspectStatus status: values()) {
				if (description.equals(status.description)) {
					return status;
				}
			}
			return UNKNOWN;
		}

		public static String toDescription(Integer intValue) {
			return from(intValue).getDescription();
		}

	}

}
