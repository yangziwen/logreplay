define(function(require, exports, module) {
	
	"use strict";
	
	var $ = require('jquery');
	var coreValidator = require('app/core-validator');
	
	var options = {
		rules: {
			tagNo: {
				required: true,
				number: true,
				min: 1,
				remote: {
					url: CTX_PATH + '/tagInfo/checkDuplication',
					type: 'get',
					dataType: 'json',
					data: {
						id: function() {
							return $('#T_id').val();
						},
						tagNo: function() {
							return $("#T_tagNo").val();
						},
						pageInfoId: function() {
							return $('#T_pageInfoId').val();
						}
					}
				}
			},
			name: {
				required: true,
			},
			originVersion: {
				required: true,
				appVersion: true
			},
			comment: {
				maxlength: 100
			}
		},
		messages: {
			tagNo: {
				required: '操作项编号不能为空!',
				number: '操作项编号必须为数字!',
				min: '操作项编号不能小于{0}!',
				remote: '操作项编号重复!'
			},
			name: {
				required: '操作项名称不能为空!',
			},
			originVersion: {
				required: '初始版本号不能为空!',
				appVersion: '版本号格式有误!'
			},
			comment: {
				maxlength: '备注字数不能多于{0}!'
			}
		}
	};
	
	module.exports = {
		validate: function(form, modifyFn) {
			if ($.isFunction(modifyFn)) {
				modifyFn(options);
			}
			return coreValidator.validate(form, options);
		}
	};
	
});
