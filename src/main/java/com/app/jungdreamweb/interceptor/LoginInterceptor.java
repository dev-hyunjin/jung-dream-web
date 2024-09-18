package com.app.jungdreamweb.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        String requestURI = request.getRequestURI();

        if(session == null || (session.getAttribute("admin") != "y" && session.getAttribute("ordererName") == null)) {
            log.info("로그인 세션 체크 Exception {}", requestURI);

            response.setContentType("text/html;charset=UTF-8");
            PrintWriter writer = response.getWriter();

            writer.print("<script>alert('이름 및 전화번호를 입력 후 이용해주세요'); location.href = '/login';</script>");
            writer.flush();
            writer.close();

            return false;
        }

        return true;
    }
}
