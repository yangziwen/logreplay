package com.sogou.map.logreplay.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.map.logreplay.bean.TagInfo;
import com.sogou.map.logreplay.dao.TagInfoDao;
import com.sogou.map.logreplay.dao.base.Page;
import com.sogou.map.logreplay.dao.base.QueryParamMap;

@Service
public class TagInfoService {
	
	@Autowired
	private TagInfoDao tagInfoDao;
	
	public List<TagInfo> getTagInfoListResult(int start, int limit, Map<String, Object> params) {
		return tagInfoDao.list(start, limit, params);
	}
	
	public List<TagInfo> getTagInfoListResult(Map<String, Object> params) {
		return tagInfoDao.list(params);
	}
	
	public Page<TagInfo> getTagInfoPageResult(int start, int limit, Map<String, Object> params) {
		return tagInfoDao.paginate(start, limit, params);
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
