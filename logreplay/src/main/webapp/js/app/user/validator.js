define(function(require, exports, module) {
	
	"use strict";
	
	var $ = require('jquery'),
		coreValidator = require('app/core-validator');
	
	module.exports = {
		validatePassword: function(form) {
			return coreValidator.validate(form, {
				rules: {
					oldPassword: {
						required: true,
						remote: {
							url: CTX_PATH + '/user/checkPassword',
							type: 'get',
							dataType: 'json',
							data: {
								password: function() {
									return $('#PW_oldPassword').val();
								}
							}
						}
					},
					newPassword: {
						required: true,
						minlength: 4
					},
					newPasswordAgain: {
						required: true,
						equalTo: '#PW_newPassword'
					}
				}, 
				messages: {
					oldPassword: {
						required: '原密码不能为空!',
						remote: '原密码不正确!'
					},
					newPassword: {
						required: '新密码不能为空!',
						minlength: '密码长度不能少于{0}'
					},
					newPasswordAgain: {
						required: '新密码不能为空!',
						equalTo: '两次输入的新密码不一致!'
					}
				}
			});
		}
	};
	
});