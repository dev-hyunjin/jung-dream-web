package com.app.jungdreamweb.controller;

import com.app.jungdreamweb.dto.OrderDTO;
import com.app.jungdreamweb.dto.ProductInfoDTO;
import com.app.jungdreamweb.service.JungDreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class JungDreamRestController {

    private final JungDreamService jungDreamService;

    @Value("${is.eunjin}")
    private boolean isEunjin;

    @PostMapping("/product-info")
    public List<ProductInfoDTO> productInfo(Integer isFirst, String productKind, String productWeight) {
        return jungDreamService.getProductInfo(isFirst, productKind, productWeight, null);
    }

    @PostMapping("/get-order")
    public OrderDTO getOrder(Integer orderId) {
        return jungDreamService.getOrder(orderId);
    }

    @PostMapping("/order")
    public Integer order(@RequestBody List<OrderDTO> orderDTOS) {
        String orderEunjun = isEunjin ? "Y" : "N";

        orderDTOS.forEach(orderDTO -> orderDTO.setOrderEunjin(orderEunjun));

        return jungDreamService.insertOrder(orderDTOS);
    }

    @PostMapping("/order-update")
    public void orderUpdate(@RequestBody OrderDTO orderDTO) {
        jungDreamService.updateOrder(orderDTO);
    }

    @PostMapping("/order-delete")
    public void orderDelete(Integer orderId) {
        jungDreamService.deleteOrder(orderId);
    }

    @PostMapping("/read-excel-order")
    public ResponseEntity<?> largeOrderProc(@RequestParam("file") MultipartFile file) {
        try {
            List<OrderDTO> dataList = jungDreamService.readExcelData(file);

            return ResponseEntity.ok(dataList); // 여기서 DB 저장 로직을 추가하면 됨
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("엑셀 파일 처리 실패: " + e.getMessage());
        }
    }
}
