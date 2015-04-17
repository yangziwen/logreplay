package com.sogou.map.logreplay.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sogou.map.logreplay.bean.Product;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.service.ProductService;

public class ProductUtil {
	
	public static final String COOKIE_KEY = "product_id";
	
	private static final ThreadLocal<Long> PRODUCT_ID_HOLDER = new ThreadLocal<Long>();
	
	private static Map<Long, Product> productMap = null;
	
	private ProductUtil() {}
	
	public static void setProductId(Long productId) {
		PRODUCT_ID_HOLDER.set(productId);
	}
	
	public static Long getProductId() {
		return PRODUCT_ID_HOLDER.get();
	}
	
	public Product getProductById(Long id) {
		return ensureProductMap().get(id);
	}
	
	public static List<Product> getProductList() {
		List<Product> newList = new ArrayList<Product>();
		for(Product product: ensureProductMap().values()) {
			newList.add(product.clone());
		}
		return Collections.unmodifiableList(newList);
	}
	
	private static Map<Long, Product> ensureProductMap() {
		if(productMap == null) {
			List<Product> list = doGetProductList();
			Map<Long, Product> map = new LinkedHashMap<Long, Product>();
			for(Product product: list) {
				map.put(product.getId(), product);
			}
			productMap = map;
		}
		return productMap;
	}
	
	private static List<Product> doGetProductList() {
		return SpringUtil.getBean(ProductService.class).getProductListResult(QueryParamMap.emptyMap());
	}
	
}
