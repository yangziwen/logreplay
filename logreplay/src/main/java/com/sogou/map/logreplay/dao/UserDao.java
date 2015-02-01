package com.sogou.map.logreplay.dao;

import org.springframework.stereotype.Repository;

import com.sogou.map.logreplay.bean.User;
import com.sogou.map.logreplay.dao.base.AbstractJdbcDaoImpl;
import com.sogou.map.logreplay.dao.base.QueryParamMap;

@Repository
public class UserDao extends AbstractJdbcDaoImpl<User> {
	
	public User getByUsername(String username) {
		return first(new QueryParamMap().addParam("username", username));
	}
	
}