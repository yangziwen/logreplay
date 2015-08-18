package com.sogou.map.logreplay.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.session.RowBounds;

import com.sogou.map.logreplay.bean.InspectionRecord;
import com.sogou.map.logreplay.dao.base.BeanMapping;

public interface InspectionRecordMapper {

	@InsertProvider(type = SqlProvider.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public void save(InspectionRecord record);
	
	@UpdateProvider(type = SqlProvider.class, method = "update")
	public void update(InspectionRecord record);
	
	@Select("select * from inspection_record where id = #{id}")
	public InspectionRecord getById(Long id);
	
	public int count(Map<String, Object> params);
	
	public List<InspectionRecord> list(Map<String, Object> params);
	
	public List<InspectionRecord> list(Map<String, Object> params, RowBounds rowBounds);
	
	public class SqlProvider extends AbstractSqlProvider<InspectionRecord> {

		BeanMapping<InspectionRecord> beanMapping = new BeanMapping<InspectionRecord>(InspectionRecord.class);
		@Override
		public BeanMapping<InspectionRecord> getBeanMapping() {
			return beanMapping;
		}
		
	}
	
}
