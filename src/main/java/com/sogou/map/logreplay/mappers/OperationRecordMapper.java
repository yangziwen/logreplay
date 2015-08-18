package com.sogou.map.logreplay.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.session.RowBounds;

import com.sogou.map.logreplay.bean.OperationRecord;
import com.sogou.map.logreplay.dao.base.BeanMapping;

public interface OperationRecordMapper {

	@InsertProvider(type = SqlProvider.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public void save(OperationRecord record);
	
	@UpdateProvider(type = SqlProvider.class, method = "update")
	public void update(OperationRecord record);
	
	public OperationRecord first(Map<String, Object> params);
	
	public List<OperationRecord> list(Map<String, Object> params);
	
	public List<OperationRecord> list(Map<String, Object> params, RowBounds rowBounds);
	
	public int batchSave(List<OperationRecord> list);
	
	public class SqlProvider extends AbstractSqlProvider<OperationRecord> {

		BeanMapping<OperationRecord> beanMapping = new BeanMapping<OperationRecord>(OperationRecord.class);
		@Override
		public BeanMapping<OperationRecord> getBeanMapping() {
			return beanMapping;
		}
		
	}
	
}
