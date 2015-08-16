package com.sogou.map.logreplay.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sogou.map.logreplay.bean.PageInfo;
import com.sogou.map.logreplay.dao.TagInfoDao;
import com.sogou.map.logreplay.dao.base.DaoConstant;
import com.sogou.map.logreplay.dao.base.Page;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.mappers.PageInfoMapper;
import com.sogou.map.logreplay.util.ProductUtil;

@Service
public class PageInfoService {
	
	@Autowired
	private PageInfoMapper pageInfoMapper;
	
	@Autowired
	private TagInfoDao tagInfoDao;
	
	public Page<PageInfo> getPageInfoPageResult(int start, int limit, Map<String, Object> params) {
		int count = pageInfoMapper.count(params);
		List<PageInfo> list = getPageInfoListResult(start, limit, params);
		return new Page<PageInfo>(start, limit, count, list);
	}
	
	public List<PageInfo> getPageInfoListResult(int start, int limit, Map<String, Object> params) {
		DaoConstant.offset(start, params);
		DaoConstant.limit(limit, params);
		return getPageInfoListResult(params);
	}
	
	public List<PageInfo> getPageInfoListResult(Map<String, Object> params) {
		return pageInfoMapper.list(params);
	}
	
	public PageInfo getPageInfoById(Long id) {
		return pageInfoMapper.getById(id);
	}
	
	public PageInfo getPageInfoByPageNoAndProductId(int pageNo, long productId) {
		return pageInfoMapper.first(new QueryParamMap()
			.addParam("pageNo", pageNo)
			.addParam("productId", productId)
		);
	}
	
	@Transactional
	public void updatePageInfo(PageInfo info) {
		info.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		pageInfoMapper.update(info);
		tagInfoDao.updatePageNoByPageInfoId(info.getId(), info.getPageNo());
	}
	
	public void createPageInfo(PageInfo info) {
		info.setProductId(ProductUtil.getProductId());
		info.setCreateTime(new Timestamp(System.currentTimeMillis()));
		pageInfoMapper.save(info);
	}

}
