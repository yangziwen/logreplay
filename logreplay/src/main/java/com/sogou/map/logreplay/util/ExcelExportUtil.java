package com.sogou.map.logreplay.util;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;


public class ExcelExportUtil {

	private static final int MAX_DATAROW_PER_SHEET = 65000;
	
	private ExcelExportUtil() {}
	
	public static Response generateExcelResponse(final HSSFWorkbook workbook, String filename) {
		try {
			filename = new String(filename.getBytes("GBK"),"iso-8859-1");
		} catch (UnsupportedEncodingException e) {}
		
		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				workbook.write(output);
			}
		};
		return Response
				.ok(stream, MediaType.APPLICATION_OCTET_STREAM)
				.header("Cache-Control", "no-cache")
				.header("Pragma", "public")
				.header("Content-Type", "application/vnd.ms-excel; charset=UTF-8")
				.header("Content-Disposition", String.format("attachment; filename = \"%s\"", filename))
				.build();
	}
	
	public static HSSFWorkbook exportDataList(List<Column> columnList, List<? extends DataContainer> dataList) {
		if(dataList == null) {
			dataList = Collections.emptyList();
		}
		final HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFCellStyle headerStyle = buildHeaderStyle(workbook);
		
		int sheetIndex = -1;
		int rowIndex = 0;
		String sheetName = "sheet";
		HSSFSheet sheet = null;
		Map<String, Object> context = new HashMap<String, Object>();
		if(CollectionUtils.isEmpty(dataList)) {	// 如果没有数据，则至少创建一个空的sheet，不然excel文件打开时会出错
			createNewSheet(workbook, ++ sheetIndex, sheetName + (sheetIndex + 1), columnList, headerStyle);
		} else {
			for(int i=0, l=dataList.size(); i<l; i++) {
				if(i / MAX_DATAROW_PER_SHEET > sheetIndex) {
					sheetIndex ++;
					createNewSheet(workbook, sheetIndex, sheetName + (sheetIndex + 1), columnList, headerStyle);
					sheet = workbook.getSheetAt(sheetIndex);
					rowIndex = 1;
				}
				DataContainer dc = dataList.get(i);
				HSSFRow row = sheet.createRow(rowIndex);
				
				for(int j = 0, m = columnList.size(); j < m; j++) {
					Column column = columnList.get(j);
					String columnKey = column.getKey();
					HSSFCell cell = row.createCell(j);
					Object value = dc.getColumnValue(columnKey);
					if(value == null) {
						cell.setCellValue("");
					} else {
						column.getType().handle(workbook, cell, value, context);
					}
				}
				rowIndex ++;
			}
		}
		return workbook;
	}
	
	public static HSSFWorkbook exportMapList(List<Column> columnList, List<Map<String, Object>> dataList) {
		if(dataList == null) {
			dataList = Collections.emptyList();
		}
		List<DataContainer> list = new ArrayList<DataContainer>(dataList.size());
		for(Map<String, Object> m: dataList) {
			list.add(MapDataContainer.newInstance(m));
		}
		return exportDataList(columnList, list);
	}
	
	private static void createNewSheet(HSSFWorkbook workbook, int sheetIndex, String sheetName, List<Column> columnList, HSSFCellStyle headerStyle) {
		HSSFSheet sheet = workbook.createSheet(sheetName);
		workbook.setSheetName(sheetIndex, sheetName);
		HSSFRow titleRow = sheet.createRow(0);
		for(int i=0, l=columnList.size(); i<l; i++) {
			Column column = columnList.get(i);
			if(column.getWidth() > 1000) {
				sheet.setColumnWidth(i, column.getWidth());
			}
			HSSFCell cell = titleRow.createCell(i);
			cell.setCellStyle(headerStyle);
			cell.setCellValue(column.getTitle());
		}
	}
	
	private static HSSFCellStyle buildHeaderStyle(HSSFWorkbook workbook) {
		HSSFFont font = workbook.createFont(); 
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); 
		font.setFontName("宋体"); 
		font.setFontHeightInPoints((short) 10); 
		
		HSSFCellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		headerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		headerStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		headerStyle.setFont(font);
		setCellBorder(headerStyle);
		
		return headerStyle;
	}
	
	private static void setCellBorder(HSSFCellStyle cellStyle){
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
	}
	
	public static Column column(String title, String key, int width, CellType type) {
		return new Column(title, key, width, type);
	}
	
	public static class Column {
		private String title;
		private String key;
		private int width;
		private CellType type;
		
		public Column(String title, String key, int width, CellType type) {
			this.title = title;
			this.key = key;
			this.width = width;
			this.type = type;
		}

		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public int getWidth() {
			return width;
		}
		public void setWidth(int width) {
			this.width = width;
		}
		public CellType getType() {
			return type;
		}
		public void setType(CellType type) {
			this.type = type;
		}
	}
	
	public static enum CellType{
		currency_cny() {
			@Override
			public void handle(HSSFWorkbook workbook, HSSFCell cell, Object value, Map<String, Object> context) {
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				HSSFCellStyle currencyCnyStyle = (HSSFCellStyle) context.get(KEY_OF_CURRENCY_CNY_STYLE);
				if(currencyCnyStyle == null) {
					HSSFDataFormat format = workbook.createDataFormat();
					currencyCnyStyle = workbook.createCellStyle();
					currencyCnyStyle.setDataFormat(format.getFormat("￥#,##0.00"));
					context.put(KEY_OF_CURRENCY_CNY_STYLE, currencyCnyStyle);
				}
				cell.setCellStyle(currencyCnyStyle);
				cell.setCellValue(NumberUtils.toDouble(value == null? "": value.toString()));
			}
		},
		currency_usd() {
			@Override
			public void handle(HSSFWorkbook workbook, HSSFCell cell, Object value, Map<String, Object> context) {
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				HSSFCellStyle currencyUsdStyle = (HSSFCellStyle) context.get(KEY_OF_CURRENCY_USD_STYLE);
				if(currencyUsdStyle == null) {
					HSSFDataFormat format =  workbook.createDataFormat();
					currencyUsdStyle = workbook.createCellStyle();
					currencyUsdStyle.setDataFormat(format.getFormat("$#,##0.00"));
					context.put(KEY_OF_CURRENCY_USD_STYLE, currencyUsdStyle);
				}
				cell.setCellStyle(currencyUsdStyle);
				cell.setCellValue(NumberUtils.toDouble(value == null? "": value.toString()));
			}
		},
		text() {
			@Override
			public void handle(HSSFWorkbook workbook, HSSFCell cell, Object value, Map<String, Object> context) {
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(value == null? "": value.toString());
			}
		},
		datetime() {
			@Override
			public void handle(HSSFWorkbook workbook, HSSFCell cell, Object value, Map<String, Object> context) {
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(value == null? "": value.toString());
			}
		},
		bool() {
			@Override
			public void handle(HSSFWorkbook workbook, HSSFCell cell, Object value, Map<String, Object> context) {
				cell.setCellType(HSSFCell.CELL_TYPE_BOOLEAN);
				cell.setCellValue(BooleanUtils.toBoolean(value == null? "": value.toString()));
			}
		},
		formatted_number() {
			@Override
			public void handle(HSSFWorkbook workbook, HSSFCell cell, Object value, Map<String, Object> context) {
				HSSFCellStyle numberStyle = (HSSFCellStyle) context.get(KEY_OF_FORMATTED_NUMBER_STYLE);
				if(numberStyle == null) {
					numberStyle = workbook.createCellStyle();
					HSSFDataFormat format = workbook.createDataFormat();
					numberStyle.setDataFormat(format.getFormat("#,##0"));
					context.put(KEY_OF_FORMATTED_NUMBER_STYLE, numberStyle);
				}
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(numberStyle);	// 注意，同一个workbook中不能产生过多的style
				cell.setCellValue(NumberUtils.toInt(value == null? "": value.toString()));
			}
		},
		number() {
			@Override
			public void handle(HSSFWorkbook workbook, HSSFCell cell, Object value, Map<String, Object> context) {
				HSSFCellStyle numberStyle = (HSSFCellStyle) context.get(KEY_OF_NUMBER_STYLE);
				if(numberStyle == null) {
					numberStyle = workbook.createCellStyle();
					context.put(KEY_OF_NUMBER_STYLE, numberStyle);
				}
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(numberStyle);	// 注意，同一个workbook中不能产生过多的style
				cell.setCellValue(NumberUtils.toInt(value == null? "": value.toString()));
			}
		},
		percent() {
			@Override
			public void handle(HSSFWorkbook workbook, HSSFCell cell, Object value, Map<String, Object> context) {
				HSSFCellStyle percentStyle = (HSSFCellStyle) context.get(KEY_OF_PERCENT_STYLE);
				if(percentStyle == null) {
					percentStyle = workbook.createCellStyle();
					HSSFDataFormat format = workbook.createDataFormat();
					percentStyle.setDataFormat(format.getFormat("0.00%"));
					context.put(KEY_OF_PERCENT_STYLE, percentStyle);
				}
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(percentStyle);
				cell.setCellValue(NumberUtils.toDouble(value == null? "": value.toString()));
			}
			
		};
		
		private static final String KEY_OF_CURRENCY_CNY_STYLE = "workbook_currency_cny_style";
		private static final String KEY_OF_CURRENCY_USD_STYLE = "workbook_currency_usd_style";
		private static final String KEY_OF_PERCENT_STYLE = "workbook_percent_style";
		private static final String KEY_OF_NUMBER_STYLE="workbook_number_style";
		private static final String KEY_OF_FORMATTED_NUMBER_STYLE = "workbook_formatted_number_style";
		
		public abstract void handle(HSSFWorkbook workbook, HSSFCell cell, Object value, Map<String, Object> context);
	}
	
	public static interface DataContainer {
		
		public Object getColumnValue(String columnKey);
		
	}
	
	public static class MapDataContainer implements DataContainer {
		
		private Map<String, Object> dataMap;
		
		private MapDataContainer(Map<String, Object> dataMap) {
			this.dataMap = MapUtils.isNotEmpty(dataMap)? dataMap: Collections.<String, Object>emptyMap();
		}

		@Override
		public Object getColumnValue(String columnKey) {
			return dataMap.get(columnKey);
		}
		
		public static DataContainer newInstance(Map<String, Object> dataMap) {
			return new MapDataContainer(dataMap);
		}
		
	}
	
}
