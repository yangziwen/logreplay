package com.sogou.map.logreplay.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.map.logreplay.bean.OperationRecord;
import com.sogou.map.logreplay.dao.OperationRecordDao;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.util.ProductUtil;

@Service
public class OperationRecordService {

	@Autowired
	private OperationRecordDao operationRecordDao;
	
	public OperationRecord getLatestOperationRecord() {
		return operationRecordDao.first(new QueryParamMap().orderByDesc("id"));
	}
	
	public List<OperationRecord> getOperationRecordListResult(int start, int limit, Map<String, Object> params) {
		return operationRecordDao.list(start, limit, params);
	}
	
	public void saveOrUpdateOperationRecord(OperationRecord record) {
		operationRecordDao.saveOrUpdate(record);
	}
	
	public int batchSaveOperationRecord(List<OperationRecord> recordList) {
		Long productId = ProductUtil.getProductId();
		for(OperationRecord record: recordList) {
			record.setProductId(productId);
		}
		return operationRecordDao.batchSaveByDirectSql(recordList.toArray(new OperationRecord[]{}), 500);
	}
}
