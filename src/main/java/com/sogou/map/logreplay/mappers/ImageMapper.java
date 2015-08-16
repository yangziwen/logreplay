package com.sogou.map.logreplay.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import com.sogou.map.logreplay.bean.Image;
import com.sogou.map.logreplay.dao.base.BeanMapping;


public interface ImageMapper {
	
	String IMAGE_RESULT = "ImageResult";
	
	@ResultMap(IMAGE_RESULT)
	@SelectProvider(type = SqlProvider.class, method = "getById")
	Image getById(Long id);
	
	@ResultMap(IMAGE_RESULT)
	@Select("select * from image where checksum = #{checksum}")
	Image getByChecksum(String checksum);
	
	@InsertProvider(type = SqlProvider.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void save(Image image);
	
	List<Image> list(Map<String, Object> params);
	
	int count(Map<String, Object> params);
	
	int batchSave(List<Image> list);
	
	public class SqlProvider extends AbstractSqlProvider<Image> {

		public BeanMapping<Image> beanMapping = new BeanMapping<Image>(Image.class);
		@Override
		public BeanMapping<Image> getBeanMapping() {
			return beanMapping;
		}

	}
	
}
