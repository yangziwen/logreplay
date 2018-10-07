package io.github.yangziwen.logreplay.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.yangziwen.logreplay.bean.PageInfo;
import io.github.yangziwen.logreplay.bean.TagInfo;
import io.github.yangziwen.logreplay.dao.PageInfoDao;
import io.github.yangziwen.logreplay.dao.TagInfoDao;
import io.github.yangziwen.logreplay.dao.base.Page;
import io.github.yangziwen.logreplay.dao.base.QueryParamMap;
import io.github.yangziwen.logreplay.util.ProductUtil;

@Service
public class PageInfoService {

	private static final String PAGE_INFO_CACHE_NAME = "pageInfoCache";

	@Autowired
	private PageInfoDao pageInfoDao;

	@Autowired
	private TagInfoDao tagInfoDao;

	@Autowired
	private TagInfoService tagInfoService;

	public Page<PageInfo> getPageInfoPageResult(int start, int limit, Map<String, Object> params) {
		return pageInfoDao.paginate(start, limit, params);
	}

	public List<PageInfo> getPageInfoListResult(int start, int limit, Map<String, Object> params) {
		return pageInfoDao.list(start, limit, params);
	}

	public List<PageInfo> getPageInfoListResult(Map<String, Object> params) {
		return pageInfoDao.list(params);
	}

	@Cacheable(cacheNames = PAGE_INFO_CACHE_NAME, key = "#id")
	public PageInfo getPageInfoById(Long id) {
		return pageInfoDao.getById(id);
	}

	public PageInfo getPageInfoByPageNoAndProductId(int pageNo, long productId) {
		return pageInfoDao.first(new QueryParamMap()
			.addParam("pageNo", pageNo)
			.addParam("productId", productId)
		);
	}

	@CacheEvict(cacheNames = PAGE_INFO_CACHE_NAME, key = "#info.id")
	@Transactional
	public void updatePageInfo(PageInfo info) {
		info.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		pageInfoDao.update(info);
		tagInfoDao.updatePageNoByPageInfoId(info.getId(), info.getPageNo());
		List<Long> tagInfoIds = tagInfoDao.list(new QueryParamMap().addParam("page_info_id", info.getId()))
				.stream().map(TagInfo::getId).collect(Collectors.toList());
		tagInfoService.clearTagInfoCacheByIds(tagInfoIds);
	}

	public void createPageInfo(PageInfo info) {
		info.setProductId(ProductUtil.getProductId());
		info.setCreateTime(new Timestamp(System.currentTimeMillis()));
		pageInfoDao.save(info);
	}

}
