package io.github.yangziwen.logreplay.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelUtil {

	private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

	private static final int MAX_DATAROW_PER_SHEET = 65000;

	private ExcelUtil() {}

	// ------------ 导入相关 -------------- //
	public static List<Map<String, String>> importMapList(InputStream in) {
		Workbook workbook = null;
		try {
			workbook = new HSSFWorkbook(in);
			Sheet sheet = workbook.getSheetAt(0);
			return readMapListFromSheet(sheet);
		} catch (Exception e) {
			logger.error("error happens when import data via excel", e);
			return Collections.emptyList();
		} finally {
			IOUtils.closeQuietly(workbook);
			IOUtils.closeQuietly(in);
		}
	}

	private static List<Map<String, String>> readMapListFromSheet(Sheet sheet) {
		if (sheet == null) {
			return Collections.emptyList();
		}
		Iterator<Row> rowIter = sheet.rowIterator();
		if (!rowIter.hasNext()) {
			return Collections.emptyList();
		}
		Iterator<Cell> headerIter = rowIter.next().cellIterator();
		List<String> headerList = new ArrayList<String>();
		while(headerIter.hasNext()) {
			headerList.add(headerIter.next().toString());
		}
		int len = headerList.size();

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		while(rowIter.hasNext()) {
			Row row = rowIter.next();
			Map<String, String> map = new HashMap<String, String>();
			list.add(map);
			for(int i = 0; i < len; i++) {
				Cell cell = row.getCell(i);
				String key = headerList.get(i);
				String value = cell != null? cell.toString(): null;
				map.put(key, value);
			}
		}
		return list;
	}

	// ------------ 导出相关 -------------- //
	public static void outputExcelToResponse(Workbook workbook, String filename, HttpServletResponse response) {
		try {
			filename = new String(filename.getBytes("GBK"),"iso-8859-1");
		} catch (UnsupportedEncodingException e) {}

		response.setContentType("application/vnd.ms-excel; charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "public");
		response.setHeader("Content-Disposition", String.format("attachment; filename = \"%s\"", filename));
		try {
			workbook.write(response.getOutputStream());
		} catch (IOException e) {
			logger.error("error happens when export data to excel", e);
		}
	}

	public static Workbook exportDataList(List<Column> columnList, List<? extends DataContainer> dataList) {
		if (dataList == null) {
			dataList = Collections.emptyList();
		}
		final Workbook workbook = new HSSFWorkbook();
		CellStyle headerStyle = buildHeaderStyle(workbook);

		int sheetIndex = -1;
		int rowIndex = 0;
		String sheetName = "sheet";
		Sheet sheet = null;
		Map<String, Object> context = new HashMap<String, Object>();
		if (CollectionUtils.isEmpty(dataList)) {	// 如果没有数据，则至少创建一个空的sheet，不然excel文件打开时会出错
			createNewSheet(workbook, ++ sheetIndex, sheetName + (sheetIndex + 1), columnList, headerStyle);
		} else {
			for(int i=0, l=dataList.size(); i<l; i++) {
				if (i / MAX_DATAROW_PER_SHEET > sheetIndex) {
					sheetIndex ++;
					createNewSheet(workbook, sheetIndex, sheetName + (sheetIndex + 1), columnList, headerStyle);
					sheet = workbook.getSheetAt(sheetIndex);
					rowIndex = 1;
				}
				DataContainer dc = dataList.get(i);
				Row row = sheet.createRow(rowIndex);

				for(int j = 0, m = columnList.size(); j < m; j++) {
					Column column = columnList.get(j);
					String columnKey = column.getKey();
					Cell cell = row.createCell(j);
					Object value = dc.getColumnValue(columnKey);
					if (value == null) {
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

	public static Workbook exportMapList(List<Column> columnList, List<Map<String, Object>> dataList) {
		if (dataList == null) {
			dataList = Collections.emptyList();
		}
		List<DataContainer> list = new ArrayList<DataContainer>(dataList.size());
		for(Map<String, Object> m: dataList) {
			list.add(MapDataContainer.newInstance(m));
		}
		return exportDataList(columnList, list);
	}

	private static void createNewSheet(Workbook workbook, int sheetIndex, String sheetName, List<Column> columnList, CellStyle headerStyle) {
		Sheet sheet = workbook.createSheet(sheetName);
		workbook.setSheetName(sheetIndex, sheetName);
		Row titleRow = sheet.createRow(0);
		for(int i=0, l=columnList.size(); i<l; i++) {
			Column column = columnList.get(i);
			if (column.getWidth() > 1000) {
				sheet.setColumnWidth(i, column.getWidth());
			}
			Cell cell = titleRow.createCell(i);
			cell.setCellStyle(headerStyle);
			cell.setCellValue(column.getTitle());
		}
	}

	private static CellStyle buildHeaderStyle(Workbook workbook) {
		Font font = workbook.createFont();
		font.setBold(true);
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 10);

		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headerStyle.setFillForegroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setFont(font);
		setCellBorder(headerStyle);

		return headerStyle;
	}

	private static void setCellBorder(CellStyle cellStyle){
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
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

	public static enum CellType {
		currency_cny() {
			@Override
			public void handle(Workbook workbook, Cell cell, Object value, Map<String, Object> context) {
				cell.setCellType(org.apache.poi.ss.usermodel.CellType.NUMERIC);
				CellStyle currencyCnyStyle = (CellStyle) context.get(KEY_OF_CURRENCY_CNY_STYLE);
				if (currencyCnyStyle == null) {
					DataFormat format = workbook.createDataFormat();
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
			public void handle(Workbook workbook,Cell cell, Object value, Map<String, Object> context) {
				cell.setCellType(org.apache.poi.ss.usermodel.CellType.NUMERIC);
				CellStyle currencyUsdStyle = (CellStyle) context.get(KEY_OF_CURRENCY_USD_STYLE);
				if (currencyUsdStyle == null) {
					DataFormat format =  workbook.createDataFormat();
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
			public void handle(Workbook workbook, Cell cell, Object value, Map<String, Object> context) {
				cell.setCellType(org.apache.poi.ss.usermodel.CellType.STRING);
				cell.setCellValue(value == null? "": value.toString());
			}
		},
		datetime() {
			@Override
			public void handle(Workbook workbook, Cell cell, Object value, Map<String, Object> context) {
				cell.setCellType(org.apache.poi.ss.usermodel.CellType.NUMERIC);
				cell.setCellValue(value == null? "": value.toString());
			}
		},
		bool() {
			@Override
			public void handle(Workbook workbook, Cell cell, Object value, Map<String, Object> context) {
				cell.setCellType(org.apache.poi.ss.usermodel.CellType.BOOLEAN);
				cell.setCellValue(BooleanUtils.toBoolean(value == null? "": value.toString()));
			}
		},
		formatted_number() {
			@Override
			public void handle(Workbook workbook, Cell cell, Object value, Map<String, Object> context) {
				CellStyle numberStyle = (CellStyle) context.get(KEY_OF_FORMATTED_NUMBER_STYLE);
				if (numberStyle == null) {
					numberStyle = workbook.createCellStyle();
					DataFormat format = workbook.createDataFormat();
					numberStyle.setDataFormat(format.getFormat("#,##0"));
					context.put(KEY_OF_FORMATTED_NUMBER_STYLE, numberStyle);
				}
				cell.setCellType(org.apache.poi.ss.usermodel.CellType.NUMERIC);
				cell.setCellStyle(numberStyle);	// 注意，同一个workbook中不能产生过多的style
				cell.setCellValue(NumberUtils.toInt(value == null? "": value.toString()));
			}
		},
		number() {
			@Override
			public void handle(Workbook workbook, Cell cell, Object value, Map<String, Object> context) {
				CellStyle numberStyle = (CellStyle) context.get(KEY_OF_NUMBER_STYLE);
				if (numberStyle == null) {
					numberStyle = workbook.createCellStyle();
					context.put(KEY_OF_NUMBER_STYLE, numberStyle);
				}
				cell.setCellType(org.apache.poi.ss.usermodel.CellType.NUMERIC);
				cell.setCellStyle(numberStyle);	// 注意，同一个workbook中不能产生过多的style
				cell.setCellValue(NumberUtils.toInt(value == null? "": value.toString()));
			}
		},
		percent() {
			@Override
			public void handle(Workbook workbook, Cell cell, Object value, Map<String, Object> context) {
				CellStyle percentStyle = (CellStyle) context.get(KEY_OF_PERCENT_STYLE);
				if (percentStyle == null) {
					percentStyle = workbook.createCellStyle();
					DataFormat format = workbook.createDataFormat();
					percentStyle.setDataFormat(format.getFormat("0.00%"));
					context.put(KEY_OF_PERCENT_STYLE, percentStyle);
				}
				cell.setCellType(org.apache.poi.ss.usermodel.CellType.NUMERIC);
				cell.setCellStyle(percentStyle);
				cell.setCellValue(NumberUtils.toDouble(value == null? "": value.toString()));
			}

		};

		private static final String KEY_OF_CURRENCY_CNY_STYLE = "workbook_currency_cny_style";
		private static final String KEY_OF_CURRENCY_USD_STYLE = "workbook_currency_usd_style";
		private static final String KEY_OF_PERCENT_STYLE = "workbook_percent_style";
		private static final String KEY_OF_NUMBER_STYLE="workbook_number_style";
		private static final String KEY_OF_FORMATTED_NUMBER_STYLE = "workbook_formatted_number_style";

		public abstract void handle(Workbook workbook, Cell cell, Object value, Map<String, Object> context);
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
