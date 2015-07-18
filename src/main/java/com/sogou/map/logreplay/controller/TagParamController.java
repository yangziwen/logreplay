package com.sogou.map.logreplay.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.sogou.map.logreplay.bean.ParamInfo;
import com.sogou.map.logreplay.bean.Permission.Target;
import com.sogou.map.logreplay.bean.TagParam;
import com.sogou.map.logreplay.controller.base.BaseController;
import com.sogou.map.logreplay.exception.LogReplayException;
import com.sogou.map.logreplay.service.TagInfoService;
import com.sogou.map.logreplay.service.TagParamService;
import com.sogou.map.logreplay.util.AuthUtil;

@Controller
@RequestMapping("/tagParam")
public class TagParamController extends BaseController {
	
	@Autowired
	private TagParamService tagParamService;
	
	@Autowired
	private TagInfoService tagInfoService;

	@ResponseBody
	@RequestMapping("/detail")
	public ModelMap detail(@RequestParam Long tagInfoId) {
		return successResult(tagParamService.getTagParamByTagInfoId(tagInfoId));
	}
	
	@ResponseBody
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ModelMap update(
			@RequestParam Long tagInfoId,
			@RequestParam(required = false) String comment,
			@RequestParam(value = "paramInfoList", required = false) String paramInfoListJson) {
		if(!AuthUtil.isPermitted(Target.Tag_Info.modify())) {
			throw LogReplayException.unauthorizedException("Role[admin] is required!");
		}
		if(tagInfoId == null || tagInfoService.getTagInfoById(tagInfoId) == null) {
			throw LogReplayException.invalidParameterException("TagInfo[%d] does not exist!", tagInfoId);
		}
		List<ParamInfo> paramInfoList = JSON.parseArray(paramInfoListJson, ParamInfo.class);
		TagParam tagParam = tagParamService.getTagParamByTagInfoId(tagInfoId);
		if(tagParam == null) {
			tagParam = new TagParam(tagInfoId, comment);
		} else {
			tagParam.setComment(comment);
		}
		try {
			tagParamService.renewTagParamAndParamInfo(tagParam, paramInfoList);
			return successResult("TagParam[%d] is renewed successfully!", tagParam.getId());
		} catch (Exception e) {
			throw LogReplayException.operationFailedException("Failed to renew TagParam!");
		}
	}
	
	
}
