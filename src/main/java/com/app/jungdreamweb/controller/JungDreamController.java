package com.app.jungdreamweb.controller;

import com.app.jungdreamweb.dto.*;
import com.app.jungdreamweb.excel.ExcelXlsxView;
import com.app.jungdreamweb.service.AdminService;
import com.app.jungdreamweb.service.JungDreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class JungDreamController {

    private final JungDreamService jungDreamService;
    private final AdminService adminService;

    @Value("${admin.id}")
    private String adminId;

    @Value("${admin.phone}")
    private String adminPhone;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${is.eunjin}")
    private boolean isEunjin;

    @Value("${order.form.path}")
    private String orderFormPath;

    @GetMapping("/")
    public String index(Model model) {
        FileDTO fileInfo = adminService.getFile();
        NoticeDTO notice = adminService.getNotice(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        model.addAttribute("fileInfo", fileInfo);
        model.addAttribute("noticeDTO", notice);

        return "index";
    }

    @GetMapping("/order")
    public String order(Model model) {
        List<ProductInfoDTO> productInfoKinds = jungDreamService.getProductInfo(1, null, null, null);
        FileDTO fileInfo = adminService.getFile();

        model.addAttribute("productInfoKinds", productInfoKinds);
        model.addAttribute("fileInfo", fileInfo);

        return "order/order";
    }

    @GetMapping("/large-order")
    public String largeOrder(Model model) {
        List<ProductInfoDTO> productInfoKinds = jungDreamService.getProductInfo(1, null, null, null);
        FileDTO fileInfo = adminService.getFile();
        SellerDTO sellerInfo = adminService.getSeller();

        model.addAttribute("productInfoKinds", productInfoKinds);
        model.addAttribute("fileInfo", fileInfo);
        model.addAttribute("sellerInfo", sellerInfo);

        return "order/large-order";
    }

    @PostMapping("/order-complete")
    public String orderComplete(Integer totalPrice, Model model) {
        String formatPrice = NumberFormat.getNumberInstance(Locale.US).format(totalPrice);
        SellerDTO sellerInfo = adminService.getSeller();

        model.addAttribute("totalPrice", formatPrice);
        model.addAttribute("sellerInfo", sellerInfo);

        return "order/order-complete";
    }

    @GetMapping("/login")
    public String login() {
        return "login/login";
    }

    @PostMapping("/login")
    public String login(String ordererName, String ordererPhone, String orderPassword, HttpSession session, Model model) {
        if(ordererName.equals(adminId) && ordererPhone.equals(adminPhone) && orderPassword.equals(adminPassword)) {
            session.setAttribute("admin", "y");

            model.addAttribute("msg", "adminSuccess");

            return "jsonView";
        }

        // startDate는 1달 전으로 세팅
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        String startDate = String.valueOf(oneMonthAgo);

        //endDate는 오늘 날짜로 세팅
        String endDate = String.valueOf(LocalDate.now());


        if(jungDreamService.getOrderCount(startDate, endDate, orderPassword, ordererName, ordererPhone, isEunjin ? "Y" : "N") == 0) {
            model.addAttribute("msg", "입력하신 정보가 맞는지 다시 한번 확인해주세요");

            return "jsonView";
        }

        session.setAttribute("ordererName", ordererName);
        session.setAttribute("ordererPhone", ordererPhone);
        session.setAttribute("orderPassword", orderPassword);

        model.addAttribute("msg", "success");

        return "jsonView";
    }

    @GetMapping("/order-history")
    public String orderHistory(String startDate, String endDate, String ordererName, HttpSession session, Model model) {
        if(startDate == null) {
            LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
            startDate = String.valueOf(oneMonthAgo);
        }

        if(endDate == null) {
            endDate = String.valueOf(LocalDate.now());
        }

        String orderEunjin = isEunjin ? "Y" : "N";

        String ordererPhone = String.valueOf(session.getAttribute("ordererPhone"));
        String orderPassword = String.valueOf(session.getAttribute("orderPassword"));
        if(String.valueOf(session.getAttribute("admin")).equals("y")) {
            ordererPhone = "";
            orderPassword = "";
            orderEunjin = orderEunjin.equals("Y") ? "Y" : "";
        } else {
            ordererName = String.valueOf(session.getAttribute("ordererName"));
        }

        List<OrderDTO> orderList = jungDreamService.getOrderList(startDate, endDate, orderPassword, ordererName, ordererPhone, orderEunjin);
        int orderCount = jungDreamService.getOrderCount(startDate, endDate, orderPassword, ordererName, ordererPhone, orderEunjin);
        List<ProductInfoDTO> productInfoKinds = jungDreamService.getProductInfo(1, null, null, null);
        SellerDTO sellerInfo = adminService.getSeller();

        model.addAttribute("orderList", orderList);
        model.addAttribute("orderCount", orderCount);
        model.addAttribute("productInfoKinds", productInfoKinds);
        model.addAttribute("sellerInfo", sellerInfo);

        return "history/order-history";
    }

    @GetMapping("/logout")
    public RedirectView logout(HttpSession session) {
        session.invalidate();

        return new RedirectView("/");
    }

    @GetMapping("/excel")
    public ModelAndView excelDownload(String startDate, String endDate, HttpSession session)  {
        Map<String, Object> excelData = new HashMap<>();
        try {
            if(startDate.equals("")) {
                startDate = String.valueOf(LocalDate.now());
            }

            if(endDate.equals("")) {
                endDate = String.valueOf(LocalDate.now());
            }

            String orderEunjin = isEunjin ? "Y" : "N";

            String ordererName = "";
            String ordererPhone = String.valueOf(session.getAttribute("ordererPhone"));
            String orderPassword = String.valueOf(session.getAttribute("orderPassword"));
            if(String.valueOf(session.getAttribute("admin")).equals("y")) {
                ordererPhone = "";
                orderPassword = "";
                orderEunjin = orderEunjin.equals("Y") ? "Y" : "";
            } else {
                ordererName = String.valueOf(session.getAttribute("ordererName"));
            }
            excelData = jungDreamService.excelDownload(startDate, endDate, orderPassword, ordererName, ordererPhone, orderEunjin);
            return new ModelAndView(new ExcelXlsxView(), excelData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView(new ExcelXlsxView(), excelData);
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

            if (!file.exists() || !file.isFile()) {
                return ResponseEntity.notFound().build();
            }

            // 파일명 인코딩
            String encoded = URLEncoder.encode("주문_양식.xlsx", StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded);
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");

            // 큰 파일/스트림 안전하게
            Resource resource = new org.springframework.core.io.InputStreamResource(
                    new java.io.FileInputStream(file)
            );

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
