package com.app.jungdreamweb.controller;

import com.app.jungdreamweb.dto.OrderDTO;
import com.app.jungdreamweb.dto.ProductInfoDTO;
import com.app.jungdreamweb.service.JungDreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class JungDreamRestController {

    private final JungDreamService jungDreamService;

    @Value("${is.eunjin}")
    private boolean isEunjin;

    @Value("${order.form.path}")
    private String orderFormPath;

    @PostMapping("/product-info")
    public List<ProductInfoDTO> productInfo(Integer isFirst, String productKind, String productWeight) {
        return jungDreamService.getProductInfo(isFirst, productKind, productWeight, null);
    }

    @PostMapping("/get-order")
    public OrderDTO getOrder(Integer orderId) {
        return jungDreamService.getOrder(orderId);
    }

    @PostMapping("/order")
    public Integer order(@RequestBody List<OrderDTO> orderDTOS) {
        String orderEunjun = isEunjin ? "Y" : "N";

        orderDTOS.forEach(orderDTO -> orderDTO.setOrderEunjin(orderEunjun));

        return jungDreamService.insertOrder(orderDTOS);
    }

    @PostMapping("/order-update")
    public void orderUpdate(@RequestBody OrderDTO orderDTO) {
        jungDreamService.updateOrder(orderDTO);
    }

    @PostMapping("/order-delete")
    public void orderDelete(Integer orderId) {
        jungDreamService.deleteOrder(orderId);
    }

    /**
     * 주문 양식 엑셀 다운로드
     */
    @GetMapping("/excel-order-form-download")
    public ResponseEntity<Resource> excelOrderFormDownload() {
        try {
            orderFormPath = new String(orderFormPath.getBytes("ISO-8859-1"), StandardCharsets.UTF_8);

            // 주문 양식 파일 경로 지정
            File file = new File(orderFormPath);

            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            // 파일 리소스 준비
            Resource resource = new FileSystemResource(file);

            // 파일 이름 UTF-8 인코딩 (브라우저 호환성 고려)
            String encodedFileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(file.length())
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/read-excel-order")
    public ResponseEntity<?> largeOrderProc(@RequestParam("file") MultipartFile file) {
        try {
            List<OrderDTO> dataList = jungDreamService.readExcelData(file);

            return ResponseEntity.ok(dataList); // 여기서 DB 저장 로직을 추가하면 됨
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("엑셀 파일 처리 실패: " + e.getMessage());
        }
    }
}
