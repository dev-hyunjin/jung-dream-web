package com.app.jungdreamweb.controller;

import com.app.jungdreamweb.dto.FileDTO;
import com.app.jungdreamweb.dto.ProductInfoDTO;
import com.app.jungdreamweb.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admins/*")
@RequiredArgsConstructor
@Slf4j
public class AdminRestController {

    private final AdminService adminService;

    @PostMapping("get-product-info")
    public ProductInfoDTO getProductInfo(String productKind, String productWeight, String productSize) {
        return adminService.getProductInfo(productKind, productWeight, productSize);
    }

    @PostMapping("registration-product-info")
    public void registrationProductInfo(@RequestBody ProductInfoDTO productInfoDTO) {
        adminService.insertProductInfo(productInfoDTO);
    }

    @PostMapping("modify-product-info")
    public void modifyProductInfo(@RequestBody ProductInfoDTO productInfoDTO) {
        adminService.updateProductInfo(productInfoDTO);
    }

    @PostMapping("remove-product-info")
    public void removeProductInfo(String productKind, String productWeight, String productSize) {
        adminService.deleteProductInfo(productKind, productWeight, productSize);
    }

    @PostMapping("registration-file")
    public void registrationFile(String fileUuid, String filePath) {
        FileDTO fileDTO = new FileDTO();

        fileDTO.setFileUuid(fileUuid);
        fileDTO.setFilePath(filePath);
        fileDTO.setFileName(filePath.split("_")[1]);

        adminService.insertFile(fileDTO);
    }
}
