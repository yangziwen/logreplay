package com.sogou.map.logreplay.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sogou.map.logreplay.bean.PageInfo;
import com.sogou.map.logreplay.dao.PageInfoDao;
import com.sogou.map.logreplay.dao.TagInfoDao;
import com.sogou.map.logreplay.dao.base.Page;
import com.sogou.map.logreplay.dao.base.QueryParamMap;

@Service
public class PageInfoService {
	
	@Autowired
	private PageInfoDao pageInfoDao;
	
	@Autowired
	private TagInfoDao tagInfoDao;
	
	public Page<PageInfo> getPageInfoPageResult(int start, int limit, Map<String, Object> params) {
		return pageInfoDao.paginate(start, limit, params);
	}
	
	public List<PageInfo> getPageInfoListResult(int start, int limit, Map<String, Object> params) {
		return pageInfoDao.list(start, limit, params);
	}
	
	public List<PageInfo> getPageInfoListResult(Map<String, Object> params) {
		return pageInfoDao.list(params);
	}
	
	public PageInfo getPageInfoById(Long id) {
		return pageInfoDao.getById(id);
	}
	
	public PageInfo getPageInfoByPageNo(int pageNo) {
		return pageInfoDao.first(new QueryParamMap().addParam("pageNo", pageNo));
	}
	
	@Transactional
	public void updatePageInfo(PageInfo info) {
		info.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		pageInfoDao.update(info);
		tagInfoDao.updatePageNoByPageInfoId(info.getId(), info.getPageNo());
	}
	
	public void createPageInfo(PageInfo info) {
		info.setCreateTime(new Timestamp(System.currentTimeMillis()));
		pageInfoDao.save(info);
	}

}
