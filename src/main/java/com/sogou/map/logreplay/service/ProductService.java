package com.sogou.map.logreplay.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.map.logreplay.bean.Product;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.mappers.ProductMapper;

@Service
public class ProductService {

	@Autowired
	private ProductMapper productMapper;
	
	public List<Product> getProductListResult(Map<String, Object> params) {
		return productMapper.list(params);
	}
	
	public List<Product> getProductListResult() {
		return getProductListResult(QueryParamMap.emptyMap());
	}
	
}
