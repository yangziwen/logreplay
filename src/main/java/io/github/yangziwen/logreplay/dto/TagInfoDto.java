package io.github.yangziwen.logreplay.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.yangziwen.logreplay.bean.ParamInfo;
import io.github.yangziwen.logreplay.bean.TagAction;
import io.github.yangziwen.logreplay.bean.TagInfo;
import io.github.yangziwen.logreplay.bean.TagParam;
import io.github.yangziwen.logreplay.bean.TagTarget;
import io.github.yangziwen.logreplay.bean.TagInfo.InspectStatus;
import io.github.yangziwen.logreplay.util.ProductUtil;
import io.github.yangziwen.logreplay.util.ExcelUtil.DataContainer;

/**
 * 仅用于excel导出
 */
public class TagInfoDto implements DataContainer {

	private static final Logger logger = LoggerFactory.getLogger(TagInfoDto.class);

	private Long id;
	private Long productId;
	private String productName;

	private Integer pageNo;
	private String pageName;
	private Integer tagNo;
	private String tagName;

	private Long actionId;
	private String actionName;
	private Long targetId;
	private String targetName;

	private Integer originVersion;
	private String originVersionDisplay;
	private String comment;

	private String inspectStatus;
	private String devInspectStatus;

	private Timestamp createTime;
	private Timestamp updateTime;

	private TagParam tagParam;
	private String tagParamDisplay;
	private String tagParamComment;

	public TagInfoDto() {}

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

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public Integer getTagNo() {
		return tagNo;
	}

	public void setTagNo(Integer tagNo) {
		this.tagNo = tagNo;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public Long getActionId() {
		return actionId;
	}

	public void setActionId(Long actionId) {
		this.actionId = actionId;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public Integer getOriginVersion() {
		return originVersion;
	}

	public void setOriginVersion(Integer originVersion) {
		this.originVersion = originVersion;
	}

	public String getOriginVersionDisplay() {
		return originVersionDisplay;
	}

	public void setOriginVersionDisplay(String originVersionDisplay) {
		this.originVersionDisplay = originVersionDisplay;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getInspectStatus() {
		return inspectStatus;
	}

	public void setInspectStatus(String inspectStatus) {
		this.inspectStatus = inspectStatus;
	}

	public String getDevInspectStatus() {
		return devInspectStatus;
	}

	public void setDevInspectStatus(String devInspectStatus) {
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

	public TagParam getTagParam() {
		return tagParam;
	}

	public void setTagParam(TagParam tagParam) {
		this.tagParam = tagParam;
	}

	public String getTagParamDisplay() {
		return tagParamDisplay;
	}

	public void setTagParamDisplay(String tagParamDisplay) {
		this.tagParamDisplay = tagParamDisplay;
	}

	public String getTagParamComment() {
		return tagParamComment;
	}

	public void setTagParamComment(String tagParamComment) {
		this.tagParamComment = tagParamComment;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	@Override
	public Object getColumnValue(String columnKey) {
		try {
			return PropertyUtils.getProperty(this, columnKey);
		} catch (Exception e) {
			logger.error("failed to get value of {} from TagInfo[{}]", columnKey, this, e);
		}
		return null;
	}

	public TagInfoDto from(TagInfo info,
			Map<Long, TagAction> actionMap, // Map<tagAction.id, tagAction>
			Map<Long, TagTarget> targetMap, // Map<tagTarget.id, tagTarget>
			Map<Long, TagParam> tagParamMap // Map<tagInfoId, tagParam>
			) {

		TagAction action = actionMap.get(info.getActionId());
		TagTarget target = targetMap.get(info.getTargetId());

		this.id = info.getId();
		this.productId = info.getProductId();
		this.productName = ProductUtil.getProductById(this.productId).getName();

		this.pageNo = info.getPageNo();
		if (info.getPageInfo() != null) {
			this.pageName = info.getPageInfo().getName();
		}
		this.tagNo = info.getTagNo();
		this.tagName = info.getName();

		this.actionId = info.getActionId();
		this.actionName = action != null? action.getName(): null;
		this.targetId = info.getTargetId();
		this.targetName = target != null? target.getName(): null;

		this.originVersion = info.getOriginVersion();
		if (this.originVersion != null) {
			this.originVersionDisplay = ProductUtil.formatAppVersion(this.originVersion);
		}
		this.comment = info.getComment();

		this.inspectStatus = InspectStatus.toDescription(info.getInspectStatus());
		this.devInspectStatus = InspectStatus.toDescription(info.getDevInspectStatus());

		this.createTime = info.getCreateTime();
		this.updateTime = info.getUpdateTime();
		this.comment = info.getComment();

		this.tagParam = tagParamMap.get(info.getId());
		if (tagParam != null && CollectionUtils.isNotEmpty(tagParam.getParamInfoList())) {
			this.tagParamComment = tagParam.getComment();
			Iterator<ParamInfo> iter = tagParam.getParamInfoList().iterator();
			ParamInfo param = iter.next();
			String value = StringUtils.isNotBlank(param.getValue())
					? param.getValue()
					: "?";
			StringBuilder buff = new StringBuilder()
				.append(param.getName()).append("{").append(value).append("}")
				.append("=").append(param.getDescription());
			while(iter.hasNext()) {
				param = iter.next();
				value = StringUtils.isNotBlank(param.getValue())
						? param.getValue()
						: "?";
				buff.append("; ")
					.append(param.getName()).append("{").append(value).append("}")
					.append("=").append(param.getDescription());
			}
			tagParamDisplay = buff.toString();
		}

		return this;
	}

	public static List<TagInfoDto> from(List<TagInfo> list,
			Map<Long, TagAction> actionMap, // Map<tagAction.id, tagAction>
			Map<Long, TagTarget> targetMap, // Map<tagTarget.id, tagTarget>
			Map<Long, TagParam> tagParamMap // Map<tagInfoId, tagParam>
			) {
		List<TagInfoDto> dtoList = new ArrayList<TagInfoDto>(list.size());
		for (TagInfo tagInfo: list) {
			dtoList.add(new TagInfoDto().from(tagInfo, actionMap, targetMap, tagParamMap));
		}
		return dtoList;
	}

}
