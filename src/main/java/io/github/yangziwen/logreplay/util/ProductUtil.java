package io.github.yangziwen.logreplay.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.google.common.collect.Lists;

import io.github.yangziwen.logreplay.bean.Product;
import io.github.yangziwen.logreplay.service.ProductService;

/**
 * 请求的cookie中所携带的product_id会通过
 * io.github.yangziwen.logreplay.filter.ProductFilter
 * 绑定到当前线程的PRODUCT_ID_HOLDER上
 * 程序中可通过此工具类的方法来获取productId
 * 需要变更产品类型时，前端改变cookie中product_id的值即可
 */
public class ProductUtil {

	/** app的主版本单位 **/
	private static final int MAJOR_UNIT = 10000000;

	/** app的小版本单位 **/
	private static final int MINOR_UNIT = 100000;

	/** app的修订版本单位 **/
	private static final int REVISION_UNIT = 1000;

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

	public static Product getCurrentProduct() {
		Long productId = getProductId();
		return getProductById(productId);
	}

	public static Product getProductById(Long id) {
		Product product = ensureProductMap().get(id);
		product = product != null? product.clone(): new Product();
		return product;
	}

	public static List<Product> getProductList() {
		List<Product> newList = new ArrayList<Product>();
		for (Product product: ensureProductMap().values()) {
			newList.add(product.clone());
		}
		return Collections.unmodifiableList(newList);
	}

	private static Map<Long, Product> ensureProductMap() {
		if (productMap == null) {
			List<Product> list = doGetProductList();
			Map<Long, Product> map = new LinkedHashMap<Long, Product>();
			for (Product product: list) {
				map.put(product.getId(), product);
			}
			productMap = map;
		}
		return productMap;
	}

	private static List<Product> doGetProductList() {
		return SpringUtil.getBean(ProductService.class).getProductListResult();
	}

	public static String formatAppVersion(Integer version) {
		if (version == null || version < MAJOR_UNIT) {
			return "";
		}
		int major = version / MAJOR_UNIT;
		int minor = ( version % MAJOR_UNIT ) /  MINOR_UNIT;
		int revision = ( version % MINOR_UNIT ) / REVISION_UNIT;
		List<Integer> list = Lists.newArrayList(major, minor);
		if (revision > 0) {
			list.add(revision);
		}
		return StringUtils.join(list, '.');
	}

	public static Integer parseAppVersion(String version) {
		if (StringUtils.isBlank(version)) {
			return 0;
		}
		try {	// version是科学记数法的情形
			int v = Double.valueOf(Double.parseDouble(version)).intValue();
			if (v > MAJOR_UNIT) {
				return v;
			}
		} catch (NumberFormatException e) {}
		if (NumberUtils.isNumber(version) && NumberUtils.toInt(version) > MAJOR_UNIT) {
			return NumberUtils.toInt(version);
		}
		String[] arr = StringUtils.split(version, '.');
		for (int i = 0, l = arr.length; i < l; i++) {
			if (!NumberUtils.isNumber(arr[i])) {
				return 0;
			}
		}
		int major = NumberUtils.toInt(arr[0]),
			minor = arr.length > 1? NumberUtils.toInt(arr[1]): 0,
			revision = arr.length > 2? NumberUtils.toInt(arr[2]): 0;
		return major * MAJOR_UNIT + minor * MINOR_UNIT + revision * REVISION_UNIT;
	}

}
