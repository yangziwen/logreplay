define(function(require, exports, module) {
	
	'use strict';
	
	require('jquery.tmpl');
	var $ = require('jquery'),
		common = require('app/common');
	
	function refreshRoleTbl() {
		var url = CTX_PATH + '/role/list';
		$.get(url, function(data) {
			if(!data || !data.response) {
				common.alertMsg('加载失败!');
				return;
			}
			var list = data.response;
			$('#J_roleTbody').empty().append($("#J_roleTmpl").tmpl(list));
		});
	}
	
	/** 角色关联权限相关代码 开始 **/
	function initOpenRelatePermissionModalBtn() {
		$('#J_roleTbody').on('click', '.open-relate-permission-modal', function() {
			var $tr = $(this).parents('tr').eq(0);
			var $tds = $tr.children();
			var roleId = $tr.data('id'),
				roleName = $tds.eq(0).text();
			var $modal = $('#J_relatePermissionModal');
			renderRelatePermissionModal($modal, roleId, roleName);
		});
	}
	
	function renderRelatePermissionModal($modal, roleId, roleName) {
		$modal.data('id', roleId);
		$modal.find('.modal-title > strong').html('修改角色[' + roleName + ']</strong>关联权限');
		$modal.find('.modal-dialog').css({
			width: 560,
			'margin-top': 150
		});
		$modal.modal({backdrop: 'static'});
		var url = CTX_PATH + '/permission/list';
		$.get(url, {roleId: roleId}, function(data) {
			if(!data || !data.response) return;
			$modal.find('.left-tbody').empty().append(buildPermissionTrs(data.response));
		});
		$.get(url, {roleId: roleId, excluded: true}, function(data) {
			if(!data || !data.response) return;
			$modal.find('.right-tbody').empty().append(buildPermissionTrs(data.response));
		});
	}
	
	function buildPermissionTrs(permissionList)  {
		return $.map(permissionList, function(permission) {
			return $('<tr data-id="' + permission.id + '"><td>' + permission.target + ':' + permission.action + '</td></tr>');
		});
	}
	
	function initRelatePermissionModalElements($modal) {
		
		initRelatePermissionModalElements = $.noop;		// 当前方法只应执行一次
		
		(function toggleSelect() {
			$modal.find('.modal-body').on('click', 'tbody tr', function() {
				var $this = $(this);
				$this.hasClass('info') && $this.removeClass('info') || $this.addClass('info');
			});
		})();
		
		function transferSelectedPermission($oldTbody, $newTbody) {
			$newTbody.append($oldTbody.children('tr.info').removeClass('info'));
		}
		
		var $leftTbody = $modal.find('tbody.left-tbody'),
			$rightTbody = $modal.find('tbody.right-tbody');
		
		(function initLeftToRightBtn() {
			$modal.find('button.left-to-right-btn').on('click', function() {
				transferSelectedPermission($leftTbody, $rightTbody);
			});
		})();
		
		(function initRightToLeftBtn() {
			$modal.find('button.right-to-left-btn').on('click', function() {
				transferSelectedPermission($rightTbody, $leftTbody);
			});
		})();
		
		(function initUpdateBtn() {
			$modal.find('button.update-btn').on('click', function() {
				var url = CTX_PATH + '/role/updatePermissions/' + $modal.data('id');
				var permissionIds = $.map($leftTbody.children('tr'), function(tr) {
					return $(tr).data('id');
				}).join(',');
				$.post(url, {permissionIds: permissionIds}, function(data) {
					if(data && data.code == 0) {
						common.alertMsg('更新成功!');
					} else {
						common.alertMsg('更新失败!');
					}
				});
			});
		})();
	}

	
	/** 角色关联权限相关代码 结束 **/
	
	function init() {
		refreshRoleTbl();
		initOpenRelatePermissionModalBtn();
		initRelatePermissionModalElements($('#J_relatePermissionModal'));
	}
	
	return {init: init};
	
});