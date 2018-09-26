define(function(require, exports, module) {
	
	"use strict";
	
	var $ = require('jquery'),
		coreValidator = require('app/core-validator');
	
	var options = {
		rules: {
			pageNo: {
				required: true,
				min: 1,
				number: true,
				remote: {
					url: CTX_PATH + '/pageInfo/checkExist',
					type: 'get',
					dataType: 'json',
					data: {
						pageNo: function() {
							return $('#S_pageNo').val();
						}
					}
				}
			},
			tagNo: {
				required: true,
				min: 1,
				number: true,
				remote: {
					url: CTX_PATH + '/tagInfo/checkExist',
					type: 'get',
					dataType: 'json',
					data: {
						pageNo: function() {
							return $('#S_pageNo').val();
						},
						tagNo: function() {
							return $('#S_tagNo').val();
						}
					}
				}
			}
		}, 
		messages: {
			pageNo: {
				required: '页面编号不能为空!',
				number: '页面编号必须为数字!',
				min: '页面编号不能小于{0}!',
				remote: '页面信息不存在!'
			},
			tagNo: {
				required: '操作编号不能为空!',
				number: '操作编号必须为数字!',
				min: '操作编号不能小于{0}!',
				remote: '操作信息不存在!'
			}
		}
	};
	
	module.exports = {
		validate: function(form) {
			return coreValidator.validate(form, options);
		}
	};
	
});