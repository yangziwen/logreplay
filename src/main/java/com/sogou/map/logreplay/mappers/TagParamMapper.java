package com.sogou.map.logreplay.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.UpdateProvider;

import com.sogou.map.logreplay.bean.TagParam;
import com.sogou.map.logreplay.dao.base.BeanMapping;

public interface TagParamMapper {

	@InsertProvider(type = SqlProvider.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public void save(TagParam tagParam);
	
	@UpdateProvider(type = SqlProvider.class, method = "update")
	public void update(TagParam tagParam);
	
	@Delete("delete from tag_param where id = #{id}")
	public void delete(TagParam tagParam);
	
	@Select("select * from tag_param where tag_info_id = #{tagInfoId}")
	public TagParam getByTagInfoId(Long tagInfoId);
	
	public List<TagParam> list(Map<String, Object> params);
	
	public class SqlProvider extends AbstractSqlProvider<TagParam> {

		BeanMapping<TagParam> beanMapping = new BeanMapping<TagParam>(TagParam.class);
		@Override
		public BeanMapping<TagParam> getBeanMapping() {
			return beanMapping;
		}
		
	}
	
}
