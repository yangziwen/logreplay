package com.sogou.map.logreplay.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sogou.map.logreplay.bean.Avatar;
import com.sogou.map.logreplay.bean.Image;
import com.sogou.map.logreplay.dao.base.AbstractEditPropertyJdbcDaoImpl;
import com.sogou.map.logreplay.dao.base.CustomPropertyEditor;
import com.sogou.map.logreplay.dao.base.QueryParamMap;

@Repository
public class AvatarDao extends AbstractEditPropertyJdbcDaoImpl<Avatar> {
	
	@SuppressWarnings("serial")
	private Map<Class<?>, CustomPropertyEditor> propertyEditorMap = new HashMap<Class<?>, CustomPropertyEditor>(){{
		put(Image.Type.class, new Image.TypePropertyEditor());
	}};
	
	@Override
	protected Map<Class<?>, CustomPropertyEditor> getPropertyEditorMap() {
		return propertyEditorMap;
	}

	public int deleteByUserId(Long userId) {
		String sql = "delete from avatar where user_id = :userId";
		return jdbcTemplate.update(sql, new QueryParamMap().addParam("userId", userId));
	}

}
