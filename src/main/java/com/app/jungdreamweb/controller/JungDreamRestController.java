package com.app.jungdreamweb.controller;

import com.app.jungdreamweb.dto.OrderDTO;
import com.app.jungdreamweb.dto.ProductInfoDTO;
import com.app.jungdreamweb.service.JungDreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class JungDreamRestController {

    private final JungDreamService jungDreamService;

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
}
