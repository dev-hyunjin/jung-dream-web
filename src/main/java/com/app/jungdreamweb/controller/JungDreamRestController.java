package com.app.jungdreamweb.controller;

import com.app.jungdreamweb.dto.ProductInfoDTO;
import com.app.jungdreamweb.service.JungDreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/order")
    public String order() {

        return "주문이 완료되었습니다";
    }
}
