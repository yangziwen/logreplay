package io.github.yangziwen.logreplay.dao;

import org.springframework.stereotype.Repository;

import io.github.yangziwen.logreplay.bean.User;
import io.github.yangziwen.logreplay.dao.base.AbstractJdbcDaoImpl;

@Repository
public class UserDao extends AbstractJdbcDaoImpl<User> {
	
}