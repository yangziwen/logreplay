package com.sogou.map.logreplay.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;

import com.sogou.map.logreplay.bean.Avatar;
import com.sogou.map.logreplay.dao.base.BeanMapping;

public interface AvatarMapper {

	@InsertProvider(type = SqlProvider.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public void save(Avatar avatar);
	
	@Delete("delete from avatar where user_id = #{userId}")
	public void deleteByUserId(Long userId);
	
	public int batchSave(List<Avatar> list);
	
	public Avatar first(Map<String, Object> params);
	
	public class SqlProvider extends AbstractSqlProvider<Avatar> {

		public BeanMapping<Avatar> beanMapping = new BeanMapping<Avatar>(Avatar.class);
		@Override
		public BeanMapping<Avatar> getBeanMapping() {
			return beanMapping;
		}

	}
	
}
