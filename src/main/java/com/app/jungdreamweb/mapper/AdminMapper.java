package com.app.jungdreamweb.mapper;

import com.app.jungdreamweb.dto.FileDTO;
import com.app.jungdreamweb.dto.ProductInfoDTO;
import com.app.jungdreamweb.dto.SellerDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper {

    public List<ProductInfoDTO> selectProductInfoList();

    public ProductInfoDTO selectProductInfo(String productKind, String productWeight, String productSize);

    public FileDTO selectFile();

    public SellerDTO selectSeller();

    public void insertProductInfo(ProductInfoDTO productInfoDTO);

    public void insertFile(FileDTO fileDTO);

    public void updateProductInfo(ProductInfoDTO productInfoDTO);

    public void updateSeller(SellerDTO sellerDTO);

    public void deleteProductInfo(String productKind, String productWeight, String productSize);

    public void deleteFile();

}
