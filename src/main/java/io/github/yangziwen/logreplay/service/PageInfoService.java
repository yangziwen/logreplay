package io.github.yangziwen.logreplay.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.yangziwen.logreplay.bean.PageInfo;
import io.github.yangziwen.logreplay.dao.PageInfoDao;
import io.github.yangziwen.logreplay.dao.TagInfoDao;
import io.github.yangziwen.logreplay.dao.base.Page;
import io.github.yangziwen.logreplay.dao.base.QueryParamMap;
import io.github.yangziwen.logreplay.util.ProductUtil;

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
	
	public PageInfo getPageInfoByPageNoAndProductId(int pageNo, long productId) {
		return pageInfoDao.first(new QueryParamMap()
			.addParam("pageNo", pageNo)
			.addParam("productId", productId)
		);
	}
	
	@Transactional
	public void updatePageInfo(PageInfo info) {
		info.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		pageInfoDao.update(info);
		tagInfoDao.updatePageNoByPageInfoId(info.getId(), info.getPageNo());
	}
	
	public void createPageInfo(PageInfo info) {
		info.setProductId(ProductUtil.getProductId());
		info.setCreateTime(new Timestamp(System.currentTimeMillis()));
		pageInfoDao.save(info);
	}

}
