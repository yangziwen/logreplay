define(function(require, exports, module) {
	
	"use strict";
	
	var $ = require('jquery'),
		coreValidator = require('app/core-validator');
	
	$.validator.addMethod('username', function(value, element) {
		return this.optional(element) || /^[a-zA-Z][a-zA-Z0-9_]{2,15}$/.test(value);
	});
	
	var options = {
		rules: {
			username: {
				required: true,
				rangelength: [3, 16],
				username: true,
				remote: {
					url: CTX_PATH + '/admin/user/checkDuplication',
					type: 'get',
					dataType: 'json',
					data: {
						id: function() {
							return $('#U_id').val();
						},
						username: function() {
							return $("#U_username").val();
						}
					}
				}
			}
		}, 
		messages: {
			username: {
				required: '用户名不能为空!',
				rangelength: '用户名长度必须在{0}到{1}之间',
				username: '用户名只能包含英文字母、数字和下划线，且必须以字母开头',
				remote: '用户名重复!'
			}
		}
	};
	
	module.exports = {
		validate: function(form) {
			return coreValidator.validate(form, options);
		},
		validatePassword: function(form) {
			return coreValidator.validate(form, {
				rules: {
					password: {
						required: true,
						minlength: 4
					}
				}, 
				messages: {
					password: {
						required: '密码不能为空!',
						minlength: '密码长度不能小于{0}'
					}
				}
			});
		}
	};
	
});