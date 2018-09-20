package io.github.yangziwen.logreplay.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import io.github.yangziwen.logreplay.bean.Image;
import io.github.yangziwen.logreplay.dao.base.AbstractEditPropertyJdbcDaoImpl;
import io.github.yangziwen.logreplay.dao.base.CustomPropertyEditor;

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
