package com.app.jungdreamweb.mapper;

import com.app.jungdreamweb.dto.OrderDTO;
import com.app.jungdreamweb.dto.ProductInfoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface JungDreamMapper {

    public List<ProductInfoDTO> selectProduct(Integer isFirst, String productKind, String productWeight, String productSize);

    public int selectOrderCount(String startDate, String endDate, String ordererName, String ordererPhone);

    public List<OrderDTO> selectOrderList(String startDate, String endDate, String ordererName, String ordererPhone);

    public void getNextSequenceValue();

    public void insertOrder(OrderDTO orderDTO);
}
