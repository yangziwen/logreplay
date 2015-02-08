define(function(require, exports, module) {
	
	"use strict";

	require('jquery.tmpl');
	require('bootstrap.pagebar');
	var $ = require('jquery'),
		common = require('app/common');
	
	var start = 0, limit = 30;	// 翻页信息
	
	function loadPageInfoResult(callback) {
		var params = {start: start, limit: limit};
		var url = CTX_PATH + '/pageInfo/list';
		$.get(url, params, function(data) {
			if(!data || !data.response || !data.response.list) {
				alert('failed!');
				return;
			}
			$.isFunction(callback) && callback(data);
		});
	}
	
	function renderPageInfoTbody(list) {
		$('#J_pageInfoTbody').empty().append($('#J_pageInfoTmpl').tmpl(list));
		initOpenUpdateModalBtn();
	}
	
	function refreshPageInfoTbl() {
		loadPageInfoResult(function(data) {
			var result = data.response;
			common.buildPageBar('#J_pagebar', result.start, result.limit, result.count, function(i, pageNum) {
				start = (pageNum - 1) * limit;
				refreshPageInfoTbl();
			});
			renderPageInfoTbody(result.list);
		});
	}
	
	function initOpenCreateModalBtn() {
		$("#J_openCreateModalBtn").on('click', function() {
			var $modal = $('#J_pageInfoModal');
			common.clearForm($modal.find('form'));
			$modal.find('.modal-title > strong').html('新增页面信息');
			$modal.find('.modal-dialog').css({
				width: 400,
				'margin-top': function() {
					return ( $(window).height() - $(this).height() ) / 3;
				}
			});
			$modal.find('button.create-page-info').show();
			$modal.find('button.update-page-info').hide();
			$modal.modal({
				backdrop: 'static'
			});
		});
	}
	
	function initCreatePageInfoBtn() {
		$('#J_createPageInfoBtn').on('click', function() {
			var params = {
				pageNo: $('#J_pageNo').val(),
				name: $('#J_name').val()
			};
			doCreate(params);
		});
	}
	
	function doCreate(params) {
		$.ajax({
			url: CTX_PATH + '/pageInfo/create',
			type: 'POST',
			dataType: 'json',
			data: params,
			success: function(data) {
				if(data.code !== 0) {
					common.alertMsg('创建失败!');
					return;
				} else {
					common.alertMsg('创建成功!').done(function() {
						$('#J_pageInfoModal').modal('hide');
					});
					refreshPageInfoTbl();
				}
			},
			error: function() {
				common.alertMsg('请求失败!');
			}
		});
	}
	
	function initOpenUpdateModalBtn() {
		$('#J_pageInfoTbody button.open-update-modal').on('click', function() {
			var $this = $(this);
			var $tr = $this.parents('tr').eq(0),
				$tds = $tr.children();
			var id = $tr.data('id'),
				pageNo = $tds.eq(0).html(),
				name = $tds.eq(1).html();
			var $modal = $('#J_pageInfoModal');
			$modal.find('.modal-title > strong').html('修改页面信息');
			$modal.find('input[name=id]').val(id);
			$modal.find('input[name=pageNo]').val(pageNo);
			$modal.find('input[name=name]').val(name);
			$modal.find('.modal-dialog').css({
				width: 400,
				'margin-top': function() {
					return ( $(window).height() - $(this).height() ) / 3;
				}
			});
			$modal.find('button.create-page-info').hide();
			$modal.find('button.update-page-info').show();
			$modal.modal({
				backdrop: 'static'
			});
		});
	}
	
	function initUpdatePageInfoBtn() {
		$('#J_updatePageInfoBtn').on('click', function() {
			var params = {
				id: $('#J_id').val(),
				pageNo: $('#J_pageNo').val(),
				name: $('#J_name').val()
			};
			doUpdate(params);
		});
	}
	
	function doUpdate(params) {
		$.ajax({
			url: CTX_PATH + '/pageInfo/update/' + params['id'],
			type: 'POST',
			dataType: 'json',
			data: params,
			success: function(data) {
				if(data.code !== 0) {
					common.alertMsg('更新失败!');
					return;
				} else {
					common.alertMsg('更新成功!').done(function() {
						$('#J_pageInfoModal').modal('hide');
					});
					refreshPageInfoTbl();
				}
			},
			error: function() {
				common.alertMsg('请求失败!');
			}
		});
	}
	
	function init() {
		refreshPageInfoTbl();
		initOpenCreateModalBtn();
		initCreatePageInfoBtn();
		initUpdatePageInfoBtn();
	}
	
	module.exports = {init: init};
	
});