package com.sogou.map.logreplay.service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sogou.map.logreplay.bean.InspectionRecord;
import com.sogou.map.logreplay.bean.PageInfo;
import com.sogou.map.logreplay.bean.Role;
import com.sogou.map.logreplay.bean.TagInfo;
import com.sogou.map.logreplay.bean.TagInfo.InspectStatus;
import com.sogou.map.logreplay.bean.User;
import com.sogou.map.logreplay.dao.InspectionRecordDao;
import com.sogou.map.logreplay.dao.PageInfoDao;
import com.sogou.map.logreplay.dao.TagInfoDao;
import com.sogou.map.logreplay.dao.UserDao;
import com.sogou.map.logreplay.dao.base.Page;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.util.AuthUtil;
import com.sogou.map.logreplay.util.ProductUtil;

@Service
public class InspectionRecordService {

	@Autowired
	private InspectionRecordDao inspectionRecordDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private PageInfoDao pageInfoDao;
	
	@Autowired
	private TagInfoDao tagInfoDao;
	
	@Transactional
	public void createInspectionRecord(InspectionRecord record) {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		TagInfo tagInfo = record.getTagInfo();
		if(tagInfo != null) {
			updateInspectStatusOfTagInfo(tagInfo, record.getValid());
		}
		record.setProductId(ProductUtil.getProductId());
		record.setCreateTime(ts);
		inspectionRecordDao.save(record);
	}
	
	@Transactional
	public void updateInspectionRecord(InspectionRecord record) {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		TagInfo tagInfo = record.getTagInfo();
		if(tagInfo != null) {
			updateInspectStatusOfTagInfo(tagInfo, record.getSolved());
		}
		record.setUpdateTime(ts);
		inspectionRecordDao.update(record);
	}
	
	private void updateInspectStatusOfTagInfo(TagInfo tagInfo, Boolean correct) {
		int status = Boolean.TRUE.equals(correct)
				? InspectStatus.SUCCESS.getIntValue()
				: InspectStatus.ERROR.getIntValue();
		
		// 校验模式分为“开发模式”和“测试模式”
		if(AuthUtil.hasRole(Role.DEV)) {
			tagInfo.setDevInspectStatus(status);
		} else {
			tagInfo.setInspectStatus(status);
		}
		tagInfoDao.update(tagInfo);
	}
	
	public InspectionRecord getInspectionRecordById(Long id) {
		InspectionRecord record = inspectionRecordDao.getById(id);
		Map<Long, User> userMap = getUserMapByIdList(Arrays.asList(record.getSubmitterId(), record.getSolverId())); 
		PageInfo pageInfo = record.getPageInfoId() != null? pageInfoDao.getById(record.getPageInfoId()): null;
		TagInfo tagInfo = record.getTagInfoId() != null? tagInfoDao.getById(record.getTagInfoId()): null;
		record.setPageInfo(pageInfo);
		record.setTagInfo(tagInfo);
		record.setSubmitter(userMap.get(record.getSubmitterId()));
		record.setSolver(userMap.get(record.getSolverId()));
		return record;
	}
	
	public Page<InspectionRecord> getInspectionRecordPaginateResult(int start, int limit, Map<String, Object> params) {
		return inspectionRecordDao.paginate(start, limit, params);
	}
	
	public Page<InspectionRecord> getInspectionRecordPaginateResultWithTransientFields(int start, int limit, Map<String, Object> params) {
		Page<InspectionRecord> page = getInspectionRecordPaginateResult(start, limit, params);
		List<Long> userIdList = Lists.newArrayList();
		List<Long> pageInfoIdList = Lists.newArrayList();
		List<Long> tagInfoIdList = Lists.newArrayList();
		for(InspectionRecord record: page.getList()) {
			if(record.getSubmitterId() != null) userIdList.add(record.getSubmitterId());
			if(record.getSolverId() != null) userIdList.add(record.getSolverId());
			if(record.getPageInfoId() != null) pageInfoIdList.add(record.getPageInfoId());
			if(record.getTagInfoId() != null) tagInfoIdList.add(record.getTagInfoId());
		}
		Map<Long, User> userMap = getUserMapByIdList(userIdList);
		Map<Long, PageInfo> pageInfoMap = getPageInfoMapByIdList(pageInfoIdList);
		Map<Long, TagInfo> tagInfoMap = getTagInfoMapByIdList(tagInfoIdList);
		for(InspectionRecord record: page.getList()) {
			record.setSubmitter(userMap.get(record.getSubmitterId()));
			record.setSolver(userMap.get(record.getSolverId()));
			record.setPageInfo(pageInfoMap.get(record.getPageInfoId()));
			record.setTagInfo(tagInfoMap.get(record.getTagInfoId()));
		}
		return page;
	}
	
	private Map<Long, User> getUserMapByIdList(List<Long> userIdList) {
		List<User> userList = CollectionUtils.isNotEmpty(userIdList)
				? userDao.list(new QueryParamMap().addParam("id__in", userIdList))
				: Collections.<User>emptyList();
				
		return Maps.uniqueIndex(userList, new Function<User, Long>() {
			@Override
			public Long apply(User user) {
				return user.getId();
			}
		});
	}
	
	private Map<Long, PageInfo> getPageInfoMapByIdList(List<Long> pageInfoIdList) {
		List<PageInfo> pageInfoList = CollectionUtils.isNotEmpty(pageInfoIdList)
				? pageInfoDao.list(new QueryParamMap().addParam("id__in", pageInfoIdList))
				: Collections.<PageInfo>emptyList();
		return Maps.uniqueIndex(pageInfoList, new Function<PageInfo, Long>() {
			@Override
			public Long apply(PageInfo pageInfo) {
				return pageInfo.getId();
			}
		});
	}
	
	private Map<Long, TagInfo> getTagInfoMapByIdList(List<Long> tagInfoIdList) {
		List<TagInfo> tagInfoList = CollectionUtils.isNotEmpty(tagInfoIdList)
				? tagInfoDao.list(new QueryParamMap().addParam("id__in", tagInfoIdList))
				: Collections.<TagInfo>emptyList();
		return Maps.uniqueIndex(tagInfoList, new Function<TagInfo, Long>() {
			@Override
			public Long apply(TagInfo tagInfo) {
				return tagInfo.getId();
			}
		});
	}
	
}
