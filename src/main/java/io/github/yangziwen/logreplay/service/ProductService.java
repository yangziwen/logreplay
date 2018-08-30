package io.github.yangziwen.logreplay.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.yangziwen.logreplay.bean.Product;
import io.github.yangziwen.logreplay.dao.ProductDao;
import io.github.yangziwen.logreplay.dao.base.QueryParamMap;

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
