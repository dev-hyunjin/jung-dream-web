/**
 * Model : ExcelWriter
 */
package com.app.jungdreamweb.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelWriter {

    private final Workbook workbook;
    private final Map<String, Object> data;
    private final HttpServletResponse response;
    private final Map<String, CellStyle> styleCache = new HashMap<>(); // 스타일 캐시

    /* 생성자 */
    public ExcelWriter(Workbook workbook, Map<String, Object> data, HttpServletResponse response) {
        this.workbook = workbook;
        this.data = data;
        this.response = response;
    }

    /* 엑셀 파일 생성 */
    public void create() {
        setFileName(response, mapToFileName());
        Sheet sheet = workbook.createSheet();
        createHead(sheet, mapToHeadList());
        createBody(sheet, mapToBodyList());
    }

    /* 모델 객체에서 파일 이름 꺼내기 */
    private String mapToFileName() {
        return (String) data.get("filename");
    }

    /* 모델 객체에서 헤더 이름 리스트 꺼내기 */
    @SuppressWarnings("unchecked")
    private List<String> mapToHeadList() {
        return (List<String>) data.get("head");
    }

    /* 모델 객체에서 바디 데이터 리스트 꺼내기 */
    @SuppressWarnings("unchecked")
    private List<List<String>> mapToBodyList() {
        return (List<List<String>>) data.get("body");
    }

    /* 파일 이름 지정 */
    private void setFileName(HttpServletResponse response, String fileName) {
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + getFileExtension(fileName) + "\"");
    }

    /* 넘어온 뷰에 따라서 확장자 결정 */
    private String getFileExtension(String fileName) {
        if (workbook instanceof XSSFWorkbook) {
            fileName += ".xlsx";
        }
        if (workbook instanceof SXSSFWorkbook) {
            fileName += ".xlsx";
        }
        if (workbook instanceof HSSFWorkbook) {
            fileName += ".xls";
        }

        return fileName;
    }

    /* 스타일 캐싱 및 재사용 */
    private CellStyle getOrCreateStyle(String styleKey, boolean isHeader) {
        if (!styleCache.containsKey(styleKey)) {
            CellStyle style = workbook.createCellStyle();
            // 테두리 설정
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);

            if (isHeader) {
                // 헤더 스타일 추가 설정
                style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                style.setAlignment(HorizontalAlignment.CENTER);
                style.setVerticalAlignment(VerticalAlignment.CENTER);
            }

            styleCache.put(styleKey, style);
        }
        return styleCache.get(styleKey);
    }

    /* 엑셀 헤더 생성 */
    private void createHead(Sheet sheet, List<String> headList) {
        createHeadRow(sheet, headList, 0);
    }

    /* 엑셀 바디 생성 */
    private void createBody(Sheet sheet, List<List<String>> bodyList) {
        int rowSize = bodyList.size();
        for (int i = 0; i < rowSize; i++) {
            createBodyRow(sheet, bodyList.get(i), i + 1);
//        	createRow(sheet, bodyList.get(i), i + 1);
        }
    }

    /* 행 생성(Body : 넓이 자동 조절있음, 생성시간 기~임) */
//    @SuppressWarnings("unused")
//	private void createRow(Sheet sheet, List<String> cellList, int rowNum) {
//    	int size = cellList.size();
//    	Row row = sheet.createRow(rowNum);
//        // 스타일 생성
//    	CellStyle style = workbook.createCellStyle();
//        // 테두리 설정
//        style.setBorderTop(BorderStyle.THIN);
//        style.setBorderBottom(BorderStyle.THIN);
//        style.setBorderLeft(BorderStyle.THIN);
//        style.setBorderRight(BorderStyle.THIN);
//
//        // 첫 번째 행 가져오기
//    	Row firstRow = sheet.getRow(0);
//    	for (int i = 0; i < size; i++) {
//    		Cell cell = row.createCell(i);
//        	cell.setCellValue(cellList.get(i));
//        	// 셀에 스타일 적용
//        	cell.setCellStyle(style);
//        	// 첫 번째 행의 셀 가져오기
//        	Cell cellOfFirstRow = firstRow.getCell(i);
//        	// 첫 번째 행의 셀의 너비 가져오기 (단위: 1/256)
//        	int cellWidthOfFirstRow = sheet.getColumnWidth(cellOfFirstRow.getColumnIndex());
//
//        	// 모든 열에 대해 셀의 너비 자동 조정
//        	sheet.autoSizeColumn(i);
//        	// 모든 열에 대해 추가 너비 설정
//        	sheet.setColumnWidth(i, sheet.getColumnWidth(i));
//
//        	// 셀의 너비 가져오기 (단위: 1/256)
//        	int cellWidth = sheet.getColumnWidth(cell.getColumnIndex());
//        	if(cellWidth < cellWidthOfFirstRow) {
//        		// 열에 대해 추가 너비 설정
//        		sheet.setColumnWidth(i, cellWidthOfFirstRow);
//        	}
//
//    	}
//    }

    /* 행 생성(Body : 넓이 자동 조절없음, 생성시간 짧음) */
//    private void createBodyRow(Sheet sheet, List<String> cellList, int rowNum) {
//        int size = cellList.size();
//        Row row = sheet.createRow(rowNum);
//        // 스타일 생성
//        CellStyle style = getOrCreateStyle("defaultStyle", false); // 스타일 재사용
//
//        for (int i = 0; i < size; i++) {
//            Cell cell = row.createCell(i);
//            cell.setCellValue(cellList.get(i));
//            cell.setCellStyle(style);
//        }
//    }

    private void createBodyRow(Sheet sheet, List<String> cellList, int rowNum) {
        int size = cellList.size();
        Row row = sheet.createRow(rowNum);
        CellStyle defaultStyle = getOrCreateStyle("defaultStyle", false); // 기본 스타일 재사용
        CellStyle numberStyle = getOrCreateNumberStyle("numberStyle"); // 숫자 스타일 생성
        CellStyle currencyStyle = getOrCreateCurrencyStyle("currencyStyle"); // 통화 스타일 생성

        for (int i = 0; i < size; i++) {
            Cell cell = row.createCell(i);
            if (i == 9 || i == 10) { // 10번째와 11번째 열 (0부터 시작하는 인덱스)
                cell.setCellValue(Double.parseDouble(cellList.get(i)));
                cell.setCellStyle(numberStyle);
            } else if (i == 11) { // 12번째 열
                cell.setCellValue(Double.parseDouble(cellList.get(i)));
                cell.setCellStyle(currencyStyle);
            } else {
                cell.setCellValue(cellList.get(i));
                cell.setCellStyle(defaultStyle);
            }
        }
    }

    // 숫자 스타일 생성 (테두리 포함)
    private CellStyle getOrCreateNumberStyle(String styleKey) {
        if (!styleCache.containsKey(styleKey)) {
            CellStyle style = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            style.setDataFormat(format.getFormat("0")); // 숫자 형식
            // 테두리 설정
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            styleCache.put(styleKey, style);
        }
        return styleCache.get(styleKey);
    }

    // 통화 스타일 생성 (테두리 포함)
    private CellStyle getOrCreateCurrencyStyle(String styleKey) {
        if (!styleCache.containsKey(styleKey)) {
            CellStyle style = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            style.setDataFormat(format.getFormat("#,###")); // 통화 형식
            // 테두리 설정
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            styleCache.put(styleKey, style);
        }
        return styleCache.get(styleKey);
    }




    /* 행 생성(Head) */
    private void createHeadRow(Sheet sheet, List<String> cellList, int rowNum) {
        int size = cellList.size();
        Row row = sheet.createRow(rowNum);
        // 스타일 생성
        CellStyle style = getOrCreateStyle("headerStyle", true); // 헤더 스타일 재사용

        for (int i = 0; i < size; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(cellList.get(i));
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 3000);
            cell.setCellStyle(style);
        }
    }

    /* 모델 객체에 담을 형태로 엑셀 데이터 생성 */
    public static Map<String, Object> createExcelData(List<String> head, List<List<String>> body, String fileName) {
        Map<String, Object> excelData = new HashMap<>();
        try {
            excelData.put("filename", java.net.URLEncoder.encode(fileName, "UTF-8"));
            excelData.put("head", head);
            excelData.put("body", body);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return excelData;
    }

}
