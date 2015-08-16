package com.sogou.map.logreplay.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.SelectProvider;

import com.sogou.map.logreplay.bean.PageInfo;
import com.sogou.map.logreplay.dao.base.BeanMapping;

public interface PageInfoMapper {
	
	@SelectProvider(type = SqlProvider.class, method = "getById")
	public PageInfo getById(Long id);
	
	@InsertProvider(type = SqlProvider.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public void save(PageInfo pageInfo);
	
	@SelectProvider(type = SqlProvider.class, method = "update")
	public void update(PageInfo pageInfo);
	
	public PageInfo first(Map<String, Object> params);

	public List<PageInfo> list(Map<String, Object> params);
	
	public int count(Map<String, Object> params);
	
	public class SqlProvider extends AbstractSqlProvider<PageInfo> {
		
		BeanMapping<PageInfo> beanMapping = new BeanMapping<PageInfo>(PageInfo.class);

		@Override
		public BeanMapping<PageInfo> getBeanMapping() {
			return beanMapping;
		}
		
	}
	
}
