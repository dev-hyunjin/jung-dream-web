package com.app.jungdreamweb.service;

import com.app.jungdreamweb.dto.FileDTO;
import com.app.jungdreamweb.dto.NoticeDTO;
import com.app.jungdreamweb.dto.ProductInfoDTO;
import com.app.jungdreamweb.dto.SellerDTO;
import com.app.jungdreamweb.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final AdminMapper adminMapper;

    public List<ProductInfoDTO> getProductInfoList() {
        return adminMapper.selectProductInfoList();
    }

    public ProductInfoDTO getProductInfo(String productKind, String productWeight, String productSize) {
        return adminMapper.selectProductInfo(productKind, productWeight, productSize);
    }

    public FileDTO getFile() {
        return adminMapper.selectFile();
    }

    public SellerDTO getSeller() {
        return adminMapper.selectSeller();
    }

    public NoticeDTO getNotice(String today) {
        return adminMapper.selectNotice(today);
    }

    public void insertProductInfo(ProductInfoDTO productInfoDTO) {
        adminMapper.insertProductInfo(productInfoDTO);
    }

    public void insertFile(FileDTO fileDTO) {
        adminMapper.deleteFile();
        adminMapper.insertFile(fileDTO);
    }

    public void updateProductInfo(ProductInfoDTO productInfoDTO) {
        adminMapper.updateProductInfo(productInfoDTO);
    }

    public void updateSeller(SellerDTO sellerDTO) {
        adminMapper.updateSeller(sellerDTO);
    }

    public void updateNotice(NoticeDTO noticeDTO) {
        adminMapper.updateNotice(noticeDTO);
    }

    public void deleteProductInfo(String productKind, String productWeight, String productSize) {
        adminMapper.deleteProductInfo(productKind, productWeight, productSize);
    }
}
