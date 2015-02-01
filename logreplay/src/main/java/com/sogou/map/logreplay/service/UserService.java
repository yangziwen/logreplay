package com.sogou.map.logreplay.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.map.logreplay.bean.User;
import com.sogou.map.logreplay.dao.UserDao;
import com.sogou.map.logreplay.dao.base.QueryParamMap;

@Service
public class UserService {
	
	@Autowired
	private UserDao userDao;

	public User getUserByUsername(String username) {
		return userDao.first(new QueryParamMap().addParam("username", username)); 
	}
}
