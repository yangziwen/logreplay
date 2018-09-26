define(function(require, exports, module) {
	
	"use strict";

	require('jquery.tmpl');
	require('bootstrap.pagebar');
	var $ = require('jquery'),
		common = require('app/common'),
		passwordValidator = require('app/user/validator').validatePassword($('#J_passwordModal form'));
	
	$('#J_passwordModal').on('hide.bs.modal', function() {
		$('#J_passwordModal form').cleanValidateStyle();
	});
	
	/** 修改密码开始 **/
	function initOpenUpdatePasswordModalBtn() {
		$('#J_openUpdatePasswordModalBtn').on('click', function() {
			var $modal = $('#J_passwordModal');
			common.clearForm($modal.find('form'));
			$modal.find('.modal-title > strong').html('修改密码');
			$modal.find('.modal-dialog').css({
				width: 400,
				'margin-top': function() {
					return ( $(window).height() - $(this).height() ) / 3;
				}
			});
			$modal.modal({
				backdrop: 'static'
			});
		});
	}
	function initUpdatePasswordBtn() {
		$('#J_updatePasswordBtn').on('click', function() {
			if(!passwordValidator.form()) {
				// common.alertMsg('参数有误，请检查!');
				return;
			}
			var params = {
				oldPassword: $('#PW_oldPassword').val(),
				newPassword: $('#PW_newPassword').val()
			};
			doUpdatePassword(params);
		});
	}
	
	function doUpdatePassword(params) {
		$.ajax({
			url: CTX_PATH + '/user/password/update',
			type: 'POST',
			dataType: 'json',
			data: params,
			success: function(data) {
				if(data.code !== 0) {
					common.alertMsg('更新失败!');
					return;
				} else {
					common.alertMsg('更新成功!').done(function() {
						$('#J_passwordModal').modal('hide');
					});
				}
			},
			error: function() {
				common.alertMsg('请求失败!');
			}
		});
	}
	/** 修改密码结束 **/
	
	function init() {
		initOpenUpdatePasswordModalBtn();
		initUpdatePasswordBtn();
		require('app/user/uploadAvatar').init();
	}
	
	module.exports = {init: init};
	
});