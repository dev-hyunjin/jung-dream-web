package com.app.jungdreamweb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDTO {
    public Integer orderId;
    public String orderPassword;
    public String orderOrdererName;
    public String orderOrdererPhone;
    public String orderDate;
    public String orderDeliveryName;
    public String orderDeliveryPhone;
    public String orderReceiverName;
    public String orderReceiverPhone;
    public String orderAddress;
    public String orderAddressDetail;
    public String orderPostcode;
    public String orderKind;
    public String orderWeight;
    public String orderSize;
    public Integer orderCount;
    public Integer orderPrice;
    public Integer orderGroupId;
    public String orderEunjin;
}
