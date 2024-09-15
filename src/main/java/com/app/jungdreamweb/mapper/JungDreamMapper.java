package com.app.jungdreamweb.mapper;

import com.app.jungdreamweb.dto.ProductInfoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface JungDreamMapper {

    public List<ProductInfoDTO> selectProduct(Integer isFirst, String productKind, String productWeight, String productSize);
}
