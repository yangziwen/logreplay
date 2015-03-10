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
	
	public List<TagInfo> getTagInfoListResult(int start, int limit, Map<String, Object> param) {
		return tagInfoDao.list(start, limit, param);
	}
	
	public List<TagInfo> getTagInfoListResult(Map<String, Object> param) {
		return tagInfoDao.list(param);
	}
	
	public Page<TagInfo> getTagInfoPageResult(int start, int limit, Map<String, Object> param) {
		Page<TagInfo> page = tagInfoDao.paginate(start, limit, param);
//		List<TagInfo> list = page.getList();
//		fillPageInfoToTagInfo(list);
		return page;
	}
	
	@Deprecated
	void fillPageInfoToTagInfo(List<TagInfo> tagInfoList) {
		if(CollectionUtils.isEmpty(tagInfoList)) {
			return;
		}
		List<Long> pageInfoIdList = new ArrayList<Long>();
		Map<Long, List<TagInfo>> tagInfoListMap = new HashMap<Long, List<TagInfo>>();	// 以pageInfoId为key
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
		return tagInfoDao.getById(id);
	}
	
	/**
	 * 如果是"公共操作项"，则直接忽略pageNo
	 */
	public TagInfo getTagInfoByPageNoAndTagNo(Integer pageNo, Integer tagNo) {
		return tagInfoDao.first(new QueryParamMap()
			.addParam("tagNo", tagNo)
			.addParam(tagNo < TagInfo.COMMON_TAG_NO_MIN_VALUE && pageNo != null && pageNo > 0, "pageNo", pageNo)
		);
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
