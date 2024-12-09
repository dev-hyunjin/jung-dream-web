package com.app.jungdreamweb.controller;

import com.app.jungdreamweb.dto.FileDTO;
import com.app.jungdreamweb.dto.OrderDTO;
import com.app.jungdreamweb.dto.ProductInfoDTO;
import com.app.jungdreamweb.dto.SellerDTO;
import com.app.jungdreamweb.excel.ExcelXlsxView;
import com.app.jungdreamweb.service.AdminService;
import com.app.jungdreamweb.service.JungDreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.NumberFormat;
import java.time.LocalDate;
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

    @GetMapping("/")
    public String index(Model model) {
        FileDTO fileInfo = adminService.getFile();

        model.addAttribute("fileInfo", fileInfo);

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
    public ModelAndView excelDownload(String startDate, String endDate, HttpSession session) throws Exception  {
        Map<String, Object> excelData = new HashMap<String, Object>();
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
}
