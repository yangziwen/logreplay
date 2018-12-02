define(function(require, exports, module) {
	
	"use strict";
	
	var $ = require('jquery'),
		coreValidator = require('app/core-validator');
	
	var options = {
		rules: {
			pageNo: {
				required: true,
				number: true,
				min: 1,
				remote: {
					url: CTX_PATH + '/pageInfo/checkDuplication',
					type: 'get',
					dataType: 'json',
					data: {
						id: function() {
							return $('#P_id').val();
						},
						pageNo: function() {
							return $("#P_pageNo").val();
						}
					}
				}
			},
			name: {
				required: true,
			}
		}, 
		messages: {
			pageNo: {
				required: '页面编号不能为空!',
				number: '页面编号必须为数字!',
				min: '页面编号不能小于{0}!',
				remote: '页面编号重复!'
			},
			name: {
				required: '页面名称不能为空!',
			}
		}
	};
	
	module.exports = {
		validate: function(form) {
			return coreValidator.validate(form, options);
		}
	};
	
});
