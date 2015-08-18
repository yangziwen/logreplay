package com.sogou.map.logreplay.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.session.RowBounds;

import com.sogou.map.logreplay.bean.Role;
import com.sogou.map.logreplay.dao.base.BeanMapping;

public interface RoleMapper {
	
	@Select("select * from role where id = #{id}")
	public Role getById(Long id);
	
	@UpdateProvider(type = SqlProvider.class, method = "update")
	public void update(Role role);
	
	public List<Role> list(Map<String, Object> params);
	
	public List<Role> list(Map<String, Object> params, RowBounds rowBounds);
	
	public class SqlProvider extends AbstractSqlProvider<Role> {
		
		BeanMapping<Role> beanMapping = new BeanMapping<Role>(Role.class);
		@Override
		public BeanMapping<Role> getBeanMapping() {
			return beanMapping;
		}
		
	}

}
