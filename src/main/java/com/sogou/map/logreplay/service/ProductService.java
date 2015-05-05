package com.sogou.map.logreplay.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.map.logreplay.bean.Product;
import com.sogou.map.logreplay.dao.ProductDao;
import com.sogou.map.logreplay.dao.base.QueryParamMap;

@Service
public class ProductService {

	@Autowired
	private ProductDao productDao;
	
	public List<Product> getProductListResult(Map<String, Object> params) {
		return productDao.list(params);
	}
	
	public List<Product> getProductListResult() {
		return getProductListResult(QueryParamMap.emptyMap());
	}
	
}
