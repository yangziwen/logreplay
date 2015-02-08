package com.sogou.map.logreplay.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.map.logreplay.bean.PageInfo;
import com.sogou.map.logreplay.bean.TagInfo;
import com.sogou.map.logreplay.dao.PageInfoDao;
import com.sogou.map.logreplay.dao.TagInfoDao;
import com.sogou.map.logreplay.dao.base.Page;
import com.sogou.map.logreplay.dao.base.QueryParamMap;

@Service
public class TagInfoService {
	
	@Autowired
	private TagInfoDao tagInfoDao;
	
	@Autowired
	private PageInfoDao pageInfoDao;
	
	public Page<TagInfo> getTagInfoPageResult(int start, int limit, Map<String, Object> param) {
		Page<TagInfo> page = tagInfoDao.paginate(start, limit, param);
		List<TagInfo> list = page.getList();
		fillPageInfoToTagInfo(list);
		return page;
	}
	
	void fillPageInfoToTagInfo(List<TagInfo> tagInfoList) {
		if(CollectionUtils.isEmpty(tagInfoList)) {
			return;
		}
		List<Long> pageInfoIdList = new ArrayList<Long>();
		Map<Long, List<TagInfo>> tagInfoListMap = new HashMap<Long, List<TagInfo>>();	// ÒÔpageInfoIdÎªkey
		for(TagInfo tagInfo: tagInfoList) {
			Long pageInfoId = tagInfo.getPageInfoId();
			List<TagInfo> list = tagInfoListMap.get(pageInfoId);
			if(list != null) {
				list.add(tagInfo);
				continue;
			}
			list = new ArrayList<TagInfo>();
			list.add(tagInfo);
			tagInfoListMap.put(pageInfoId, list);
			pageInfoIdList.add(pageInfoId);
		}
		List<PageInfo> pageInfoList = pageInfoDao.list(new QueryParamMap().addParam("id__in", pageInfoIdList));
		for(PageInfo pageInfo: pageInfoList) {
			for(TagInfo tagInfo: tagInfoListMap.get(pageInfo.getId())) {
				tagInfo.setPageInfo(pageInfo);
			}
		}
	}
	
	public TagInfo getTagInfoById(Long id) {
		TagInfo tagInfo = tagInfoDao.getById(id);
		if(tagInfo == null) {
			return null;
		}
		PageInfo pageInfo = pageInfoDao.getById(tagInfo.getPageInfoId());
		tagInfo.setPageInfo(pageInfo);
		return tagInfo;
	}
	
	public TagInfo getTagInfoByTagNo(int tagNo) {
		TagInfo tagInfo = tagInfoDao.first(new QueryParamMap().addParam("tagNo", tagNo));
		if(tagInfo == null) {
			return null;
		}
		PageInfo pageInfo = pageInfoDao.getById(tagInfo.getPageInfoId());
		tagInfo.setPageInfo(pageInfo);
		return tagInfo;
	}
	
	public void updateTagInfo(TagInfo info) {
		info.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		tagInfoDao.update(info);
	}
	
	public void createTagInfo(TagInfo info) {
		info.setCreateTime(new Timestamp(System.currentTimeMillis()));
		tagInfoDao.save(info);
	}

}
