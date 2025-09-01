package com.app.jungdreamweb.service;

import com.app.jungdreamweb.dto.OrderDTO;
import com.app.jungdreamweb.dto.ProductInfoDTO;
import com.app.jungdreamweb.excel.ExcelWriter;
import com.app.jungdreamweb.mapper.JungDreamMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class JungDreamService {

    private final JungDreamMapper jungDreamMapper;

    public static String hash(String input) {
        try {
            // MessageDigest 인스턴스를 SHA-256으로 초기화
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // 입력 문자열을 바이트 배열로 변환하여 해시 처리
            byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));

            // 바이트 배열을 16진수 문자열로 변환
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            // 해시 결과 반환
            return hexString.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<ProductInfoDTO> getProductInfo(Integer isFirst, String productKind, String productWeight, String productSize) {
        return jungDreamMapper.selectProduct(isFirst, productKind, productWeight, productSize);
    }

    public int getOrderCount(String startDate, String endDate, String orderPassword, String ordererName, String ordererPhone, String orderEunjin) {
        return jungDreamMapper.selectOrderCount(startDate, endDate, orderPassword.equals("") ? null : hash(orderPassword), ordererName, ordererPhone, orderEunjin);
    }

    public List<OrderDTO> getOrderList(String startDate, String endDate, String orderPassword, String ordererName, String ordererPhone, String orderEunjin) {
        return jungDreamMapper.selectOrderList(startDate, endDate, orderPassword.equals("") ? null : hash(orderPassword), ordererName, ordererPhone, orderEunjin);
    }

    public OrderDTO getOrder(Integer orderId) {
        return jungDreamMapper.selectOrder(orderId);
    }

    public Integer insertOrder(List<OrderDTO> orderDTOS) {
        Integer totalPrice = 0;

        // 시퀀스 값을 설정하기 위해 nextval 호출 (한 번만 호출)
        jungDreamMapper.getNextSequenceValue();

        for (OrderDTO orderDTO : orderDTOS) {
            List<ProductInfoDTO> productInfoDTOS = jungDreamMapper.selectProduct(null, orderDTO.getOrderKind(), orderDTO.orderWeight, orderDTO.getOrderSize());
            orderDTO.setOrderPrice(productInfoDTOS.get(0).productPrice * orderDTO.orderCount);
            orderDTO.setOrderPassword(hash(orderDTO.getOrderPassword()));
            totalPrice += productInfoDTOS.get(0).productPrice * orderDTO.orderCount;

            jungDreamMapper.insertOrder(orderDTO);
        }
        return totalPrice;
    }

    public void updateOrder(OrderDTO orderDTO) {
        List<ProductInfoDTO> productInfoDTOS = jungDreamMapper.selectProduct(null, orderDTO.getOrderKind(), orderDTO.orderWeight, orderDTO.getOrderSize());
        orderDTO.setOrderPrice(productInfoDTOS.get(0).productPrice * orderDTO.orderCount);

        jungDreamMapper.updateOrder(orderDTO);
    }

    public void deleteOrder(Integer orderId) {
        jungDreamMapper.deleteOrder(orderId);
    }

    public Map<String, Object> excelDownload(String startDate, String endDate, String orderPassword, String ordererName, String ordererPhone, String orderEunjin) throws Exception {

        List<String> headList = null;				// 엑셀 해더
        List<List<String>> bodyList = null;			// 엑셀 바디
        String fileName = "";						// 엑셀 파일명
        headList = new ArrayList<String>();			// 헤더 리스트
        bodyList = new ArrayList<List<String>>();	// 바디 리스트

        try {

            /* 파일명(녹취이력) */
            fileName = "직거래 주문 현황-" + startDate + "_" + endDate;

            /* 엑셀 헤더 */
            headList.add("No");
            headList.add("주문번호");
            headList.add("주문자");
            headList.add("주문자 전화번호");
            headList.add("보내는사람");
            headList.add("보내는사람 전화번호");
            headList.add("받는사람");
            headList.add("받는사람 전화번호");
            headList.add("주소");
            headList.add("품종");
            headList.add("규격");
            headList.add("수량(5kg)");
            headList.add("수량(10kg)");
            headList.add("가격");
            headList.add("입금상황");
            headList.add("주문일");
            headList.add("배송여부");
            headList.add("배송일");
            headList.add("비고");

            Map<String, Object> argMap = new HashMap<String, Object>();
            List<OrderDTO> list = null;

            list = jungDreamMapper.selectOrderList(startDate, endDate, orderPassword, ordererName, ordererPhone, orderEunjin);

            if (list.size() > 0) {
                int i = 1;
                for (OrderDTO item : list) {
                    List<String> tmpBodyList = new ArrayList<String>();
                    tmpBodyList.add(String.valueOf(i));
                    tmpBodyList.add(String.valueOf(item.getOrderGroupId()));
                    tmpBodyList.add(item.getOrderOrdererName());
                    tmpBodyList.add(item.getOrderOrdererPhone());
                    tmpBodyList.add(item.getOrderDeliveryName());
                    tmpBodyList.add(item.getOrderDeliveryPhone());
                    tmpBodyList.add(item.getOrderReceiverName());
                    tmpBodyList.add(item.getOrderReceiverPhone());
                    tmpBodyList.add(item.getOrderAddress() + ", " + item.getOrderAddressDetail());
                    tmpBodyList.add(item.getOrderKind());
                    tmpBodyList.add(item.getOrderSize());

                    if(item.getOrderWeight().equals("5kg")) {
                        tmpBodyList.add(String.valueOf(item.getOrderCount())); // 수량 5kg
                        tmpBodyList.add("0"); // 수량 10kg
                    } else {
                        tmpBodyList.add("0"); // 수량 5kg
                        tmpBodyList.add(String.valueOf(item.getOrderCount())); // 수량 10kg
                    }

                    tmpBodyList.add(String.valueOf(item.getOrderPrice()));
                    tmpBodyList.add("");
                    tmpBodyList.add(item.getOrderDate());
                    tmpBodyList.add("미배송");
                    tmpBodyList.add("준비중");
                    tmpBodyList.add("");
                    /* 엑셀 바디 */
                    bodyList.add(tmpBodyList);
                    i++;
                }
            }

            return ExcelWriter.createExcelData(headList, bodyList, fileName);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ExcelWriter.createExcelData(headList, bodyList, fileName);
    }

    public List<OrderDTO> readExcelData(MultipartFile file) throws IOException {
        List<OrderDTO> dataList = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트
            Iterator<Row> rowIterator = sheet.iterator();

            // 첫 번째 행(헤더) 건너뛰기
            if (rowIterator.hasNext()) rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                String deliveryName = getCellValue(row, 0);
                String deliverPhone = getCellValue(row, 1);
                String receiverName = getCellValue(row, 2);
                String receiverPhone = getCellValue(row, 3);
                String postCode = getCellValue(row, 4);
                String address = getCellValue(row, 5);
                String addressDetail = getCellValue(row, 6);
                String kind = getCellValue(row, 7);
                String size = getCellValue(row, 8);
                String weight = getCellValue(row, 9);
                String count = getCellValue(row, 10);

                OrderDTO orderDTO = new OrderDTO();

                orderDTO.setOrderDeliveryName(deliveryName);
                orderDTO.setOrderDeliveryPhone(deliverPhone);
                orderDTO.setOrderReceiverName(receiverName);
                orderDTO.setOrderReceiverPhone(receiverPhone);
                orderDTO.setOrderPostcode(postCode);
                orderDTO.setOrderAddress(address);
                orderDTO.setOrderAddressDetail(addressDetail);
                orderDTO.setOrderKind(kind);
                orderDTO.setOrderSize(size);
                orderDTO.setOrderWeight(weight + "kg");
                orderDTO.setOrderCount(Integer.parseInt(count));

                dataList.add(orderDTO);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return ""; // 셀이 아예 없는 경우

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                // 정수/소수 구분 필요 시 여기서 조정 가능
                double numericValue = cell.getNumericCellValue();
                if (numericValue == Math.floor(numericValue)) {
                    return String.valueOf((long) numericValue); // 정수로 변환
                } else {
                    return String.valueOf(numericValue); // 소수점 그대로
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    private int parseIntValue(Cell cell) {
        if (cell == null) return 0;
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        } else {
            try {
                return Integer.parseInt(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }
}
