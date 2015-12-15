define(function(require, exports, module) {
	
	"use strict";

	require('jquery.tmpl');
	var $ = require('jquery'),
		common = require('app/common'),
		moment = require('moment');
	
	function refreshCharts() {
		fetchAppData().done(drawCharts);
	}
	
	function drawCharts(data) {
		drawThreadChart(data);
		drawHttpChart(data);
		drawClassChart(data);
		drawSqlChart(data);
	}
	
	var defaultToolTip = {
		trigger: 'axis',
		backgroundColor: 'rgba(255, 255, 255, 0.6)',
		borderColor: '#ccc',
		borderWidth: 1,
		textStyle: {
			color: '#666'
		}
	}
	
	function drawThreadChart(data) {
		var option = {
			tooltip: $.extend({
				formatter: function(params) {
					var data = params[0]
					return [
						'时间：' + data[1],
						'线程数：' + data[2]
					].join('<br/>');
				}
			}, defaultToolTip),
			legend: {
				data: ['线程数'],
				y: 'bottom'
			},
			toolbox: {
				show: false
			},
			xAxis: [{
				name: '时间',
				type: 'category',
				boundaryGap: false,
				data: $.map(data.threadCountDataList, function(data) {
					return moment(data.key).format('HH:mm');
				})
			}],
			yAxis: [{
				name: '线程数',
				type: 'value'
			}],
			series: [{
				name: '线程数',
				type: 'line',
				data: $.map(data.threadCountDataList, function(data) {
					return parseInt(data.value);
				})
			}]
			
		};
		echarts
			.init($('#J_threadChart')[0], 'macarons')
			.setOption(option);
	}
	
	function drawHttpChart(data) {
		var option = {
			tooltip: $.extend({
				formatter: function(params) {
					var data = params[0]
					return [
						'时间：' + data[1],
						'会话数：' + data[2]
					].join('<br/>');
				}
			}, defaultToolTip),
			legend: {
				data: ['http会话数'],
				y: 'bottom'
			},
			toolbox: {
				show: false
			},
			xAxis: [{
				name: '时间',
				type: 'category',
				boundaryGap: false,
				data: $.map(data.httpSessionsDataList, function(data) {
					return moment(data.key).format('HH:mm');
				})
			}],
			yAxis: [{
				name: '会话数',
				type: 'value'
			}],
			series: [{
				name: 'http会话数',
				type: 'line',
				data: $.map(data.httpSessionsDataList, function(data) {
					return parseInt(data.value);
				})
			}]
			
		};
		echarts
			.init($('#J_httpChart')[0], 'macarons')
			.setOption(option);
	}
	
	function drawClassChart(data) {
		
		var option = {
			tooltip: $.extend({
				formatter: function(params) {
					var data = params[0]
					return [
						'时间：' + data[1],
						'类加载数：' + data[2]
					].join('<br/>');
				}
			}, defaultToolTip),
			legend: {
				data: ['类加载数'],
				y: 'bottom'
			},
			toolbox: {
				show: false
			},
			xAxis: [{
				name: '时间',
				type: 'category',
				boundaryGap: false,
				data: $.map(data.loadedClassesCountDataList, function(data) {
					return moment(data.key).format('HH:mm');
				})
			}],
			yAxis: [{
				name: '类加载数',
				type: 'value'
			}],
			series: [{
				name: '类加载数',
				type: 'line',
				data: $.map(data.loadedClassesCountDataList, function(data) {
					return parseInt(data.value);
				})
			}]
			
		};
		echarts
			.init($('#J_classChart')[0], 'macarons')
			.setOption(option);
	}
	
	function drawSqlChart(data) {
		var option = {
			tooltip: $.extend({
				formatter: function(params) {
					var sqlHitsRate = params[0],
						transactionsRate = params[1];
					return [
						'时间：' + sqlHitsRate[1],
						'sql数：' + sqlHitsRate[2],
						'事务数：' + transactionsRate[2]
					].join('<br/>');
				}
			}, defaultToolTip),
			legend: {
				data: ['sql数', '事务数'],
				y: 'bottom'
			},
			toolbox: {
				show: false
			},
			xAxis: [{
				name: '时间',
				type: 'category',
				boundaryGap: false,
				data: $.map(data.threadCountDataList, function(data) {
					return moment(data.key).format('HH:mm');
				})
			}],
			yAxis: [{
				name: '数量',
				type: 'value'
			}],
			series: [{
				name: 'sql数',
				type: 'line',
				data: $.map(data.sqlHitsRateDataList, function(data) {
					return parseInt(data.value);
				})
			}, {
				name: '事务数',
				type: 'line',
				data: $.map(data.transactionsRateDataList, function(data) {
					return parseInt(data.value);
				})
			}]
			
		};
		echarts
			.init($('#J_sqlChart')[0], 'macarons')
			.setOption(option);
	}
	
	
	function fetchAppData() {
		var d = $.Deferred();
		$.get(CTX_PATH + '/monitor/appData', function(data) {
			if(data.code !== 0 || !data.response) {
				d.reject(data);
			} else {
				d.resolve(data.response);
			}
		});
		return d.promise();
	}
	
	function init() {
		refreshCharts();
	}
	
	module.exports = {init: init};
	
});