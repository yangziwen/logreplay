package com.sogou.map.logreplay.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sogou.map.logreplay.bean.Image;
import com.sogou.map.logreplay.dao.base.AbstractEditPropertyJdbcDaoImpl;
import com.sogou.map.logreplay.dao.base.CustomPropertyEditor;

@Repository
public class ImageDao extends AbstractEditPropertyJdbcDaoImpl<Image> {

	@SuppressWarnings("serial")
	private Map<Class<?>, CustomPropertyEditor> propertyEditorMap = new HashMap<Class<?>, CustomPropertyEditor>(){{
		put(Image.Type.class, new Image.TypePropertyEditor());
	}};
	
	@Override
	protected Map<Class<?>, CustomPropertyEditor> getPropertyEditorMap() {
		return propertyEditorMap;
	}

}
