package com.sogou.map.logreplay.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.map.logreplay.bean.OperationRecord;
import com.sogou.map.logreplay.dao.base.DaoConstant;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.mappers.OperationRecordMapper;
import com.sogou.map.logreplay.util.ProductUtil;

@Service
public class OperationRecordService {

	@Autowired
	private OperationRecordMapper operationRecordMapper;
	
	public OperationRecord getLatestOperationRecord() {
		return operationRecordMapper.first(new QueryParamMap().orderByDesc("id"));
	}
	
	public List<OperationRecord> getOperationRecordListResult(int start, int limit, Map<String, Object> params) {
		DaoConstant.offset(start, params);
		DaoConstant.limit(limit, params);
		return operationRecordMapper.list(params);
	}
	
	public void saveOrUpdateOperationRecord(OperationRecord record) {
		if(record.getId() == null) {
			operationRecordMapper.save(record);
		} else {
			operationRecordMapper.update(record);
		}
	}
	
	public int batchSaveOperationRecord(List<OperationRecord> recordList) {
		Long productId = ProductUtil.getProductId();
		for(OperationRecord record: recordList) {
			record.setProductId(productId);
		}
		int batchSize = 500, cnt = 0;
		for (int i = 0, l = recordList.size(); i < l; i += batchSize ) {
			int toIndex = Math.min(i + batchSize, l);
			cnt += operationRecordMapper.batchSave(recordList.subList(i, toIndex));
		}
		return cnt;
	}
}
