define(function(require, exports, module) {
	
	"use strict";
	
	var $ = require('jquery'),
		coreValidator = require('app/core-validator');
	
	var options = {
		rules: {
			username: {
				required: true,
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
				remote: '用户名重复!'
			}
		}
	}
	
	module.exports = {
		validate: function(form) {
			return coreValidator.validate(form, options);
		}
	};
	
});