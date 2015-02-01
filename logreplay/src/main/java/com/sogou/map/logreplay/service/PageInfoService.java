package com.sogou.map.logreplay.service;

import java.sql.Timestamp;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.map.logreplay.bean.PageInfo;
import com.sogou.map.logreplay.dao.PageInfoDao;
import com.sogou.map.logreplay.dao.base.Page;
import com.sogou.map.logreplay.dao.base.QueryParamMap;

@Service
public class PageInfoService {
	
	@Autowired
	private PageInfoDao pageInfoDao;
	
	public Page<PageInfo> getPageInfoPaginateResult(int start, int limit, Map<String, Object> param) {
		return pageInfoDao.paginate(start, limit, param);
	}
	
	public PageInfo getPageInfoById(Long id) {
		return pageInfoDao.getById(id);
	}
	
	public PageInfo getPageInfoByPageNo(int pageNo) {
		return pageInfoDao.first(new QueryParamMap().addParam("pageNo", pageNo));
	}
	
	public void update(PageInfo info) {
		info.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		pageInfoDao.update(info);
	}
	
	public void create(PageInfo info) {
		info.setCreateTime(new Timestamp(System.currentTimeMillis()));
		pageInfoDao.save(info);
	}

}
