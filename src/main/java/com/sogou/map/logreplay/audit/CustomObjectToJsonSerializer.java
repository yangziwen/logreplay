package com.sogou.map.logreplay.audit;

import java.util.List;

import org.audit4j.core.ObjectSerializer;
import org.audit4j.core.ObjectToJsonSerializer;
import org.audit4j.core.annotation.DeIdentify;
import org.audit4j.core.dto.Field;

public class CustomObjectToJsonSerializer implements ObjectSerializer {

	@Override
	public void serialize(List<Field> auditFields,
			Object object, String objectName, DeIdentify deidentify) {
		String json = ObjectToJsonSerializer.toJson(object, deidentify);
		auditFields.add(new Field('"' + objectName + '"', json, "\b"));
	}

}
