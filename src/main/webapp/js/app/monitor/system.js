define(function(require, exports, module) {
	
	"use strict";

	require('jquery.tmpl');
	var $ = require('jquery'),
		common = require('app/common'),
		moment = require('moment');
	
	function initSystemInfo() {
		return $.get(CTX_PATH + '/monitor/systemInfo', function(data) {
			if(data.code !== 0 || !data.response) {
				return;
			}
			$('#J_systemInfoTmpl').tmpl(data.response)
				.appendTo('#J_systemInfoTbody')
		});
	}
	
	function initShowTypeBtnGroup() {
		$('#J_showTypeBtnGroup').on('change', 'label', function() {
			refreshMemoryChart();
		});
	}
	
	function refreshMemoryChart() {
		fetchMemoryData().done(drawMemoryChart);
	}
	
	function drawMemoryChart(data) {
		drawJvmMemoryChart(data);
		drawSystemMemoryChart(data);
	}
	
	var defaultToolTipOption = {
		trigger: 'axis',
		backgroundColor: 'rgba(255, 255, 255, 0.6)',
		borderColor: '#ccc',
		borderWidth: 1,
		textStyle: {
			color: '#666'
		}
	}
	
	var defaultGridOption = {
		y: 25,
		x: 50,
		x2: 50
	};
	
	var showTypeFormats = {
		halfDay: 'HH:mm',
		day: 'HH:mm',
		week: 'ddd HH:mm',
		month: 'Do HH:mm'
	};
	
	function drawJvmMemoryChart(data) {
		var option = {
			tooltip: $.extend({
				formatter: function(params) {
					var usedMemory = params[0],
						usedNonHeapMemory = params[1];
					return [
						'时间：' + usedMemory[1],
						'已用内存：' + usedMemory[2] + ' MB',
						'非堆内存：' + usedNonHeapMemory[2] + ' MB'
					].join('<br/>');
				}
			}, defaultToolTipOption),
			legend: {
				y: 'bottom',
				data: ['used memory', 'used non-heap memory']
			},
			toolbox: {
				show: false
			},
			grid: defaultGridOption,
			xAxis: [{
				name: '时间',
				type: 'category',
				boundaryGap: false,
				data: $.map(data.usedMemoryDataList, function(d) {
					return moment(d.key).format(showTypeFormats[data.showType]);
				})
			}],
			yAxis: [{
				name: '内存',
				type: 'value',
				axisLabel: {
					formatter: '{value} MB'
				}
			}],
			series: [{
				name: 'used memory',
				type: 'line',
				data: $.map(data.usedMemoryDataList, function(data) {
					return (data.value / 1024 / 1024).toFixed(2);
				})
			}, {
				name: 'used non-heap memory',
				type: 'line',
				data: $.map(data.usedNonHeapMemoryDataList, function(data) {
					return (data.value / 1024 / 1024).toFixed(2);
				})
			}]
			
		};
		echarts
			.init($('#J_jvmMemoryChart')[0], 'macarons')
			.setOption(option);
	}
	
	function drawSystemMemoryChart(data) {
		var option = {
			tooltip: $.extend({formatter: function(params) {
					var physicalMemory = params[0],
						swapSpace = params[1];
					return [
						'时间：' + physicalMemory[1],
						'物理内存：' + physicalMemory[2] + ' GB',
						'交换空间：' + swapSpace[2] + ' GB'
					].join('<br/>');
				}
			}, defaultToolTipOption),
			legend: {
				y: 'bottom',
				data: ['used physical memory', 'used swap space']
			},
			toolbox: {
				show: false
			},
			grid: defaultGridOption,
			xAxis: [{
				name: '时间',
				type: 'category',
				boundaryGap: false,
				data: $.map(data.usedMemoryDataList, function(d) {
					return moment(d.key).format(showTypeFormats[data.showType]);
				})
			}],
			yAxis: [{
				name: '内存',
				type: 'value',
				axisLabel: {
					formatter: '{value} GB'
				}
			}],
			series: [{
				name: 'used physical memory',
				type: 'line',
				data: $.map(data.usedPhysicalMemoryDataList, function(data) {
					return (data.value / Math.pow(1024, 3)).toFixed(2);
				})
			}, {
				name: 'used swap space',
				type: 'line',
				data: $.map(data.usedSwapSpaceDataList, function(data) {
					return (data.value / Math.pow(1024, 3)).toFixed(2);
				})
			}]
			
		};
		echarts
			.init($('#J_systemMemoryChart')[0], 'macarons')
			.setOption(option);
	}
	
	function getShowTypeParam(showType) {
		var now = moment();
		switch(showType) {
			case 'halfDay': return {
				startTime: moment(now).subtract(12, 'h').format('x'),
				endTime: now.format('x'),
				step: 600
			}
			case 'day': return {
				startTime: moment(now).subtract(24, 'h').format('x'),
				endTime: now.format('x'),
				step: 1800
			}
			case 'week': return {
				startTime: moment(now).subtract(7, 'd').format('x'),
				endTime: now.format('x'),
				step: 3600
			}
			case 'month': return {
				startTime: moment(now).subtract(1, 'M').format('x'),
				endTime: now.format('x'),
				step: 3600
			}
			default: return {}
		}
	}
	
	function fetchMemoryData() {
		var showType = $('#J_showTypeBtnGroup input:checked').val();
		var showTypeParam = getShowTypeParam(showType);
		showTypeParam.showType = showType;
		var d = $.Deferred();
		$.get(CTX_PATH + '/monitor/memoryData', showTypeParam, function(data) {
			if(data.code !== 0 || !data.response) {
				d.reject(data);
			} else {
				d.resolve(data.response);
			}
		});
		return d.promise();
	}
	
	function init() {
		initSystemInfo();
		initShowTypeBtnGroup();
		refreshMemoryChart();
	}
	
	module.exports = {init: init};
	
});