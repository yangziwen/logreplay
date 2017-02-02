package com.sogou.map.logreplay.audit;

import java.util.List;

import org.audit4j.core.ObjectSerializer;
import org.audit4j.core.annotation.DeIdentify;
import org.audit4j.core.annotation.DeIdentifyUtil;
import org.audit4j.core.dto.Field;

import com.alibaba.fastjson.JSON;

public class ObjectToJsonSerializer implements ObjectSerializer {
	
	@Override
	public void serialize(List<Field> auditFields,
			Object object, String objectName, DeIdentify deidentify) {
		String json = toJson(object, deidentify);
		auditFields.add(new Field('"' + objectName + '"', json, object.getClass().getName()));
	}

	public final static String toJson(Object object, DeIdentify deidentify) {
		if (isPrimitive(object)) {
			String primitiveValue = String.valueOf(deidentifyValue(object, deidentify));
			if (object instanceof String) {
				primitiveValue = '"' + primitiveValue + '"';
			}
			return primitiveValue;
		}
		return JSON.toJSONString(object, AuditFastjsonFilter.instance());
	}
	
    public final static boolean isPrimitive(Object object) {
        if (object instanceof String || object instanceof Number || object instanceof Boolean
                || object instanceof Character) {
            return true;
        }
        return false;
    }
    
	public final static Object deidentifyValue(Object value, DeIdentify deIdentify) {
		if (value == null || deIdentify == null) {
			return value;
		}
		return DeIdentifyUtil.deidentify(String.valueOf(value),
				deIdentify.left(), deIdentify.right(),
				deIdentify.fromLeft(), deIdentify.fromRight());
	}

}
