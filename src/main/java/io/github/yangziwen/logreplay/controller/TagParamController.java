package io.github.yangziwen.logreplay.controller;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import io.github.yangziwen.logreplay.bean.ParamInfo;
import io.github.yangziwen.logreplay.bean.TagParam;
import io.github.yangziwen.logreplay.controller.base.BaseController;
import io.github.yangziwen.logreplay.exception.LogReplayException;
import io.github.yangziwen.logreplay.service.TagInfoService;
import io.github.yangziwen.logreplay.service.TagParamService;

@RestController
@RequestMapping("/tagParam")
public class TagParamController extends BaseController {

	@Autowired
	private TagParamService tagParamService;

	@Autowired
	private TagInfoService tagInfoService;

	@GetMapping("/detail")
	@RequiresPermissions("tag_info:view")
	public ModelMap detail(@RequestParam Long tagInfoId) {
		return successResult(tagParamService.getTagParamByTagInfoId(tagInfoId));
	}

	@PostMapping("/update")
	@RequiresPermissions("tag_info:modify")
	public ModelMap update(
			@RequestParam Long tagInfoId,
			@RequestParam(required = false) String comment,
			@RequestParam(value = "paramInfoList", required = false) String paramInfoListJson) {
		if (tagInfoId == null || tagInfoService.getTagInfoById(tagInfoId) == null) {
			throw LogReplayException.invalidParameterException("TagInfo[%d] does not exist!", tagInfoId);
		}
		List<ParamInfo> paramInfoList = JSON.parseArray(paramInfoListJson, ParamInfo.class);
		TagParam tagParam = tagParamService.getTagParamByTagInfoId(tagInfoId);
		if (tagParam == null) {
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
