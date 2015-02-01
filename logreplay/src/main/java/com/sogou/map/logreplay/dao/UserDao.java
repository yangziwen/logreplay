package com.sogou.map.logreplay.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.sogou.map.logreplay.bean.User;

@Repository
public class UserDao {
	
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	private final RowMapper<User> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(User.class);
	
	public User getByUsername(String username) {
		String sql = "select id, username, password from user where username = :username";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("username", username);
		List<User> list = jdbcTemplate.query(sql, paramMap, rowMapper);
		return CollectionUtils.isNotEmpty(list)? list.get(0): null;
	}
}