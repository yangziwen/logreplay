package com.sogou.map.logreplay.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.map.logreplay.dao.OperationRecordDao;

@Service
public class OperationRecordService {

	@Autowired
	private OperationRecordDao operationRecordDao;
	
}
