package com.app.jungdreamweb.service;

import com.app.jungdreamweb.dto.OrderDTO;
import com.app.jungdreamweb.dto.ProductInfoDTO;
import com.app.jungdreamweb.excel.ExcelWriter;
import com.app.jungdreamweb.mapper.JungDreamMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class JungDreamService {

    private final JungDreamMapper jungDreamMapper;

    public List<ProductInfoDTO> getProductInfo(Integer isFirst, String productKind, String productWeight, String productSize) {
        return jungDreamMapper.selectProduct(isFirst, productKind, productWeight, productSize);
    }

    public boolean getOrderCount(String startDate, String endDate, String ordererName, String ordererPhone) {
        return jungDreamMapper.selectOrderCount(startDate, endDate, ordererName, ordererPhone) > 0;
    }

    public List<OrderDTO> getOrderList(String startDate, String endDate, String ordererName, String ordererPhone) {
        return jungDreamMapper.selectOrderList(startDate, endDate, ordererName, ordererPhone);
    }

    public Integer insertOrder(List<OrderDTO> orderDTOS) {
        Integer totalPrice = 0;

        // 시퀀스 값을 설정하기 위해 nextval 호출 (한 번만 호출)
        jungDreamMapper.getNextSequenceValue();

        for (OrderDTO orderDTO : orderDTOS) {
            List<ProductInfoDTO> productInfoDTOS = jungDreamMapper.selectProduct(null, orderDTO.getOrderKind(), orderDTO.orderWeight, orderDTO.getOrderSize());
            orderDTO.setOrderPrice(productInfoDTOS.get(0).productPrice * orderDTO.orderCount);
            totalPrice += productInfoDTOS.get(0).productPrice * orderDTO.orderCount;

            jungDreamMapper.insertOrder(orderDTO);
        }
        return totalPrice;
    }

    public Map<String, Object> excelDownload(String startDate, String endDate, String ordererName, String ordererPhone) throws Exception {

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
            headList.add("수신자");
            headList.add("수신자 전화번호");
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

            list = jungDreamMapper.selectOrderList(startDate, endDate, ordererName, ordererPhone);

            if (list.size() > 0) {
                int i = 1;
                for (OrderDTO item : list) {
                    List<String> tmpBodyList = new ArrayList<String>();
                    tmpBodyList.add(String.valueOf(i));
                    tmpBodyList.add(String.valueOf(item.getOrderGroupId()));
                    tmpBodyList.add(item.getOrderOrdererName());
                    tmpBodyList.add(item.getOrderOrdererPhone());
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
}
