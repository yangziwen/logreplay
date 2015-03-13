package com.sogou.map.logreplay.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.sogou.map.mengine.common.service.BaseService;

@Component
@Path("/common")
public class CommonController extends BaseService {

	/**
	 * 第一次发起实时校验请求时，需要客户端跟服务端校对时间
	 */
	@GET
	@Path("/serverTimestamp")
	public Response getServerTimestamp() {
		return successResultToJson(new ModelMap("timestamp", System.currentTimeMillis()), true);
	}
}
