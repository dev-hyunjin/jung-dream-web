package com.app.jungdreamweb.controller;

import com.app.jungdreamweb.dto.ProductInfoDTO;
import com.app.jungdreamweb.service.JungDreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class JungDreamController {

    private final JungDreamService jungDreamService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/order")
    public String order(Model model) {
        List<ProductInfoDTO> productInfoKinds = jungDreamService.getProductInfo(1, null, null, null);

        model.addAttribute("productInfoKinds", productInfoKinds);

        return "order/order";
    }

    @GetMapping("/order-complete")
    public String orderComplete() {
        return "order/order-complete";
    }

    @GetMapping("/order-history")
    public String orderHistory() {
        return "order/order-history";
    }

    @GetMapping("/login")
    public String login() {
        return "login/login";
    }
}
