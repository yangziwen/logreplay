package com.sogou.map.logreplay.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;

import com.sogou.map.logreplay.bean.Product;

public interface ProductMapper {

	List<Product> list(Map<String, Object> params);
	
	List<Product> list(Map<String, Object> params, RowBounds rowBounds);
	
}
