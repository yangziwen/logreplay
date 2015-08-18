package com.sogou.map.logreplay.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.session.RowBounds;

import com.sogou.map.logreplay.bean.User;
import com.sogou.map.logreplay.dao.base.BeanMapping;

public interface UserMapper {
	
	@Select("select * from user where id = #{id}")
	public User getById(Long id);
	
	@Select("select * from user where username = #{username}")
	public User getByUsername(String username);
	
	public int count(Map<String, Object> params);
	
	public List<User> list(Map<String, Object> params);
	
	public List<User> list(Map<String, Object> params, RowBounds rowBounds);
	
	@InsertProvider(type = SqlProvider.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public void save(User user);
	
	@UpdateProvider(type = SqlProvider.class, method = "update")
	public void update(User user);
	
	public int batchSave(List<User> list);
	
	public class SqlProvider extends AbstractSqlProvider<User> {
		
		BeanMapping<User> beanMapping = new BeanMapping<User>(User.class);
		@Override
		public BeanMapping<User> getBeanMapping() {
			return beanMapping;
		}
		
	}
	
}
