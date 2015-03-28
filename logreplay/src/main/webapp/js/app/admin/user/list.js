define(function(require, exports, module) {
	
	"use strict";

	require('jquery.tmpl');
	require('bootstrap.pagebar');
	var $ = require('jquery'),
		common = require('app/common');
	var validator = require('app/admin/user/validator');
	var userValidator = validator.validate($('#J_userModal form')),
		passwordValidator = validator.validatePassword($('#J_passwordModal form'));
	
	$('#J_userModal').on('hide.bs.modal', function() {
		$('#J_userModal form').cleanValidateStyle();
	});
	$('#J_passwordModal').on('hide.bs.modal', function() {
		$('#J_passwordModal form').cleanValidateStyle();
	});
	
	var start = 0, limit = 30;	// 翻页信息
	
	function refreshRoleOptions() {
		var url = CTX_PATH + '/role/list';
		$.get(url, function(data) {
			if(!data || !data.response) {
				return;
			}
			$('#J_roleNames, #U_roleNames').empty().append($.map(data.response, function(role) {
				return $('<option>' + role.name + '</option>');
			})).eq(0).prepend('<option value="">全部</option>');
		});
	}
	
	function loadUserResult(callback) {
		var params = $.extend({
			start: start, limit: limit
		}, common.collectParams('#J_queryArea input[type!=button][type!=submit][type!=reset], #J_queryArea select'));
		var url = CTX_PATH + '/admin/user/list';
		$.get(url, params, function(data) {
			if(!data || !data.response || !data.response.list) {
				common.alertMsg('加载失败!');
				return;
			}
			$.isFunction(callback) && callback(data);
		});
	}
	
	function loadUserById(id, callback) {
		if(!id) {
			return;
		}
		var url = CTX_PATH + '/admin/user/detail/' + id;
		$.get(url, function(data) {
			if(!data || !data.response) {
				common.alertMsg('加载失败!');
				return;
			}
			$.isFunction(callback) && callback(data.response);
		});
	}
	
	function renderUserTbody(list) {
		$('#J_userTbody').empty().append($('#J_userTmpl').tmpl(list));
	}
	
	function refreshUserTbl() {
		loadUserResult(function(data) {
			var result = data.response;
			common.buildPageBar('#J_pagebar', result.start, result.limit, result.count, function(i, pageNum) {
				start = (pageNum - 1) * limit;
				refreshUserTbl();
			});
			renderUserTbody(result.list);
		});
	}
	
	/** 新增user开始 **/
	function initOpenCreateModalBtn() {
		$("#J_openCreateModalBtn").on('click', function() {
			var $modal = $('#J_userModal');
			common.clearForm($modal.find('form'));
			$modal.find('.modal-title > strong').html('新增用户');
			$modal.find('select[name=roleNames]').children(':first-child').prop('selected', true);
			$modal.find('input[name=username]').attr({disabled: false});
			$modal.find('select[name=enabled]').val('true');
			$modal.find('.modal-dialog').css({
				width: 400,
				'margin-top': function() {
					return ( $(window).height() - $(this).height() ) / 4;
				}
			});
			$modal.find('button.create-user-info').show();
			$modal.find('button.update-user-info').hide();
			$modal.modal({
				backdrop: 'static'
			});
		});
	}
	
	function initCreateUserBtn() {
		$('#J_createUserBtn').on('click', function() {
			if(!userValidator.form()) {
				// common.alertMsg('参数有误，请检查!');
				return;
			}
			var params = {
				username: $('#U_username').val(),
				password: '1234',	// 初始密码为1234
				screenName: $('#U_screenName').val(),
				roleNames: $('#U_roleNames').val(),
				enabled: $('#U_enabled').val()
			};
			doCreateUser(params);
		});
	}
	
	function doCreateUser(params) {
		$.ajax({
			url: CTX_PATH + '/admin/user/create',
			type: 'POST',
			dataType: 'json',
			data: params,
			success: function(data) {
				if(data.code !== 0) {
					common.alertMsg('创建失败!');
					return;
				} else {
					common.alertMsg('创建成功!').done(function() {
						$('#J_userModal').modal('hide');
					});
					refreshUserTbl();
				}
			},
			error: function() {
				common.alertMsg('请求失败!');
			}
		});
	}
	/** 新增user结束 **/
	
	/** 修改user开始 **/
	function initOpenUpdateModalBtn() {
		$('#J_userTbody').on('click', 'button.open-update-modal', function() {
			var $this = $(this);
			var $tr = $this.parents('tr').eq(0);
			var id = $tr.data('id');
			var $modal = $('#J_userModal');
			common.clearForm($modal.find('form'));
			$modal.find('.modal-title > strong').html('修改用户');
			$modal.find('.modal-dialog').css({
				width: 400,
				'margin-top': function() {
					return ( $(window).height() - $(this).height() ) / 4;
				}
			});
			$modal.find('button.create-user-info').hide();
			$modal.find('button.update-user-info').show();
			
			loadUserById(id, function(user) {
				$modal.find('input[name=id]').val(user.id);
				$modal.find('input[name=username]').val(user.username).attr({disabled: true});
				$modal.find('input[name=screenName]').val(user.screenName);
				$modal.find('select[name=roleNames]').val(user.roles[0].name);
				$modal.find('select[name=enabled]').val(user.enabled + '');
				$modal.modal({
					backdrop: 'static'
				});
			});
		});
	}
	
	function initUpdateUserBtn() {
		$('#J_updateUserBtn').on('click', function() {
			if(!userValidator.form()) {
				// common.alertMsg('参数有误，请检查!');
				return;
			}
			var params = {
				id: $('#U_id').val(),
				username: $('#U_username').val(),
				screenName: $('#U_screenName').val(),
				roleNames: $('#U_roleNames').val(),
				enabled: $('#U_enabled').val()
			};
			doUpdateUser(params);
		});
	}
	
	function doUpdateUser(params) {
		$.ajax({
			url: CTX_PATH + '/admin/user/update/' + params['id'],
			type: 'POST',
			dataType: 'json',
			data: params,
			success: function(data) {
				if(data.code !== 0) {
					common.alertMsg('更新失败!');
					return;
				} else {
					common.alertMsg('更新成功!').done(function() {
						$('#J_userModal').modal('hide');
					});
					refreshUserTbl();
				}
			},
			error: function() {
				common.alertMsg('请求失败!');
			}
		});
	}
	/** 修改user结束 **/
	
	/** 重置用户密码开始 **/
	function initOpenUpdatePasswordModalBtn() {
		$('#J_userTbody').on('click', 'button.open-update-password-modal', function() {
			var $this = $(this);
			var $tr = $this.parents('tr').eq(0);
			var id = $tr.data('id'),
				username = $tr.children(':first-child').html();
			var $modal = $('#J_passwordModal');
			common.clearForm($modal.find('form'));
			$modal.find('.modal-title > strong').html('重置 [ ' + username + ' ] 的密码');
			$modal.find('input[name=id]').val(id);
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
				id: $('#PW_id').val(),
				password: $('#PW_password').val()
			};
			doUpdatePassword(params);
		});
	}
	
	function doUpdatePassword(params) {
		$.ajax({
			url: CTX_PATH + '/admin/user/password/update/' + params['id'],
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
	/** 重置用户密码结束 **/
	
	function initQueryBtn() {
		var $queryBtn = $('#J_queryBtn');
		$('#J_queryArea').on('keyup', 'input[type!=button][type!=submit][type!=reset]', function(ev) {
			if(ev.which == 13) {
				$queryBtn.trigger('click');
			}
		});
		$queryBtn.on('click', function() {
			start = 0;
			refreshUserTbl();
		});
	}
	
	function initClearBtn() {
		$('#J_clearBtn').on('click', function() {
			start = 0;
			common.clearForm($('#J_queryArea form'));
			refreshUserTbl();
		});
	}
	
	function init() {
		refreshRoleOptions();
		refreshUserTbl();
		initOpenCreateModalBtn();
		initOpenUpdateModalBtn();
		initOpenUpdatePasswordModalBtn();
		initCreateUserBtn();
		initUpdateUserBtn();
		initUpdatePasswordBtn();
		initQueryBtn();
		initClearBtn();
	}
	
	module.exports = {init: init};
	
});