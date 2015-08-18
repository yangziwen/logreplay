package com.sogou.map.logreplay.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.session.RowBounds;

import com.sogou.map.logreplay.bean.Image;
import com.sogou.map.logreplay.dao.base.BeanMapping;


public interface ImageMapper {
	
	@SelectProvider(type = SqlProvider.class, method = "getById")
	Image getById(Long id);
	
	@Select("select * from image where checksum = #{checksum}")
	Image getByChecksum(String checksum);
	
	@InsertProvider(type = SqlProvider.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void save(Image image);
	
	int count(Map<String, Object> params);
	
	List<Image> list(Map<String, Object> params);
	
	List<Image> list(Map<String, Object> params, RowBounds rowBounds);
	
	int batchSave(List<Image> list);
	
	public class SqlProvider extends AbstractSqlProvider<Image> {

		public BeanMapping<Image> beanMapping = new BeanMapping<Image>(Image.class);
		@Override
		public BeanMapping<Image> getBeanMapping() {
			return beanMapping;
		}

	}
	
}
