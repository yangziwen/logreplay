package io.github.yangziwen.logreplay.service;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.yangziwen.logreplay.bean.ParamInfo;
import io.github.yangziwen.logreplay.bean.TagInfo;
import io.github.yangziwen.logreplay.bean.TagParam;
import io.github.yangziwen.logreplay.dao.ParamInfoDao;
import io.github.yangziwen.logreplay.dao.TagInfoDao;
import io.github.yangziwen.logreplay.dao.TagParamWithInfosDao;
import io.github.yangziwen.logreplay.dao.base.Page;
import io.github.yangziwen.logreplay.dao.base.QueryParamMap;
import io.github.yangziwen.logreplay.util.ProductUtil;

@Service
public class TagInfoService {

	private static final String TAG_INFO_CACHE_NAME = "tagInfoCache";

	@Autowired
	private TagInfoDao tagInfoDao;

	@Autowired
	private TagParamWithInfosDao tagParamWithInfosDao;

	@Autowired
	private ParamInfoDao paramInfoDao;

	@Autowired
	private EhCacheCacheManager cacheManager;

	public List<TagInfo> getTagInfoListResult(int start, int limit, Map<String, Object> params) {
		return tagInfoDao.list(start, limit, params);
	}

	public List<TagInfo> getTagInfoListResult(Map<String, Object> params) {
		return tagInfoDao.list(params);
	}

	public Page<TagInfo> getTagInfoPageResult(int start, int limit, Map<String, Object> params) {
		return tagInfoDao.paginate(start, limit, params);
	}

	@Cacheable(cacheNames = TAG_INFO_CACHE_NAME, key = "#id")
	public TagInfo getTagInfoById(Long id) {
		return tagInfoDao.getById(id);
	}

	/**
	 * 如果是"公共操作项"，则直接忽略pageNo
	 */
	public TagInfo getTagInfoByPageNoTagNoAndProductId(Integer pageNo, Integer tagNo, Long productId) {
		return tagInfoDao.first(new QueryParamMap()
			.addParam("productId", productId)
			.addParam("tagNo", tagNo)
			.addParam(tagNo < TagInfo.COMMON_TAG_NO_MIN_VALUE && pageNo != null && pageNo > 0, "pageNo", pageNo)
		);
	}

	@CacheEvict(cacheNames = TAG_INFO_CACHE_NAME, key = "#info.id")
	public void updateTagInfo(TagInfo info) {
		info.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		tagInfoDao.update(info);
	}

	public void createTagInfo(TagInfo info) {
		info.setProductId(ProductUtil.getProductId());
		info.setCreateTime(new Timestamp(System.currentTimeMillis()));
		tagInfoDao.save(info);
	}

	@CacheEvict(cacheNames = TAG_INFO_CACHE_NAME, key = "#id")
	@Transactional
	public void deleteTagInfoById(Long id) {
		List<TagParam> tagParamList = tagParamWithInfosDao.list(new QueryParamMap().addParam("tagInfoId", id));
		if (CollectionUtils.isNotEmpty(tagParamList)) {
			TagParam tagParam = tagParamList.get(0);
			paramInfoDao.batchDeleteByIds(collectParamInfoId(tagParam.getParamInfoList()));
			tagParamWithInfosDao.delete(tagParam);
		}
		tagInfoDao.deleteById(id);
	}

	private Set<Long> collectParamInfoId(List<ParamInfo> paramInfoList) {
		if (CollectionUtils.isEmpty(paramInfoList)) {
			return Collections.emptySet();
		}
		Set<Long> existedIdSet = new HashSet<Long>();
		for (ParamInfo paramInfo: paramInfoList) {
			if (paramInfo == null || paramInfo.getId() == null) {
				continue;
			}
			existedIdSet.add(paramInfo.getId());
		}
		return existedIdSet;
	}

	public void clearTagInfoCacheByIds(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return;
		}
		Cache cache = cacheManager.getCache(TAG_INFO_CACHE_NAME);
		ids.stream().forEach(cache::evict);
	}

}
