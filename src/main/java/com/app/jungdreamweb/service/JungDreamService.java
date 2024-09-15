package com.app.jungdreamweb.service;

import com.app.jungdreamweb.dto.ProductInfoDTO;
import com.app.jungdreamweb.mapper.JungDreamMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JungDreamService {

    private final JungDreamMapper jungDreamMapper;

    public List<ProductInfoDTO> getProductInfo(Integer isFirst, String productKind, String productWeight, String productSize) {
        return jungDreamMapper.selectProduct(isFirst, productKind, productWeight, productSize);
    }
}
