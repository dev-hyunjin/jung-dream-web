package com.app.jungdreamweb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductInfoDTO {
    public String productKind;
    public String productWeight;
    public String productSize;
    public Integer productPrice;
    public String productSoldOut;
}
