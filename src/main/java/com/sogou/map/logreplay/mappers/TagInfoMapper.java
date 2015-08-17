package com.sogou.map.logreplay.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;

import com.sogou.map.logreplay.bean.TagInfo;
import com.sogou.map.logreplay.dao.base.BeanMapping;

public interface TagInfoMapper {
	
	@InsertProvider(type = SqlProvider.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public void save(TagInfo tagInfo);
	
	@Update("update tag_info set page_no = #{pageNo} where page_info_id = #{pageInfoId}")
	public void updatePageNoByPageInfoId(@Param("pageInfoId") Long pageInfoId, @Param("pageNo") Integer pageNo);
	
	@UpdateProvider(type = SqlProvider.class, method = "update")
	public void update(TagInfo tagInfo);
	
	@Delete("delete from tag_info where id = #{id}")
	public void deleteById(Long id);
	
	public TagInfo getById(Long id);
	
	public TagInfo first(Map<String, Object> params);
	
	public List<TagInfo> list(Map<String, Object> params);
	
	public int count(Map<String, Object> params);
	
	public class SqlProvider extends AbstractSqlProvider<TagInfo> {

		BeanMapping<TagInfo> beanMapping = new BeanMapping<TagInfo>(TagInfo.class);
		@Override
		public BeanMapping<TagInfo> getBeanMapping() {
			return beanMapping;
		}
		
	}
	
}
