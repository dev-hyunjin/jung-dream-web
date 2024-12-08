package com.app.jungdreamweb.controller;

import com.app.jungdreamweb.dto.FileDTO;
import com.app.jungdreamweb.dto.ProductInfoDTO;
import com.app.jungdreamweb.dto.SellerDTO;
import com.app.jungdreamweb.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
@RequestMapping("/admin/*")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;

    @GetMapping("product-mgmt")
    public String productMgmt(Model model) {
        List<ProductInfoDTO> productInfoList = adminService.getProductInfoList();

        model.addAttribute("productInfoList", productInfoList);

        return "admin/product-mgmt";
    }

    @GetMapping("image-mgmt")
    public String imageMgmt(Model model) {
        FileDTO fileInfo = adminService.getFile();

        model.addAttribute("fileInfo", fileInfo);

        return "admin/image-mgmt";
    }

    @GetMapping("seller-mgmt")
    public String sellerMgmt(Model model) {
        SellerDTO sellerInfo = adminService.getSeller();

//        model.addAttribute("sellerInfo", sellerInfo);
        model.addAttribute("sellerDTO", sellerInfo);

        return "admin/seller-mgmt";
    }

    @PostMapping("seller-update")
    public RedirectView sellerUpdate(SellerDTO sellerDTO) {
        adminService.updateSeller(sellerDTO);

        return new RedirectView("/admin/seller-mgmt");
    }
}
