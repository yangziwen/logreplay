package com.sogou.map.logreplay.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.map.logreplay.bean.OperationRecord;
import com.sogou.map.logreplay.dao.OperationRecordDao;

@Service
public class OperationRecordService {

	@Autowired
	private OperationRecordDao operationRecordDao;
	
	public List<OperationRecord> getOperationRecordListResult(int start, int limit, Map<String, Object> param) {
		return operationRecordDao.list(start, limit, param);
	}
}
