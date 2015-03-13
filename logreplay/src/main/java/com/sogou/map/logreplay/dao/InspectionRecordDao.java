package com.sogou.map.logreplay.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sogou.map.logreplay.bean.InspectionRecord;
import com.sogou.map.logreplay.dao.base.AbstractJdbcDaoImpl;

@Repository
public class InspectionRecordDao extends AbstractJdbcDaoImpl<InspectionRecord> {

	/**
	 * 请注意，由于有“公共操作项”的存在
	 * 所以针对pageNo和tagNo的查询应该以tagInfoId为准
	 * 只有在为“公共操作项”填充pageInfo的时候，才会去参考pageInfoId的值
	 */
	@Override
	protected String generateSqlByParam(int start, int limit, Map<String, Object> param) {
		Integer pageNo = (Integer) param.remove("pageNo");
		Integer tagNo = (Integer) param.remove("tagNo");
		if(pageNo != null || tagNo != null) {
			StringBuilder existsClauseBuff = new StringBuilder(" SELECT * FROM tag_info WHERE tag_info.id = tag_info_id ");
			if(pageNo != null) {
				existsClauseBuff.append(" and page_no = " + pageNo);
			}
			if(tagNo != null) {
				existsClauseBuff.append(" and tag_no = " + tagNo);
			}
			param.put(existsClauseBuff.append(" __exists").toString(), null);
		}
		return super.generateSqlByParam(start, limit, param);
	}
	
}
