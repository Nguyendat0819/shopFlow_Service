package com.shopflow.common.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import tools.jackson.databind.ObjectMapper;

import com.shopflow.common.response.ApiResponse;
import com.shopflow.common.response.ApiResponseFactory;
import com.shopflow.common.response.DomainCode;

import java.io.IOException;

// Chuyển lỗi xác thực và từ chối quyền ở security filter thành ApiResponse JSON thống nhất.
@Slf4j
@Component
@RequiredArgsConstructor
public class RestSecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ApiResponseFactory responseFactory;
    private final ObjectMapper objectMapper;

    // Trả lỗi 401 thống nhất khi thiếu, sai hoặc hết hạn access token.
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.warn("Chưa xác thực: {}", request.getRequestURI());
        write(response, DomainCode.UNAUTHORIZED);
    }

    // Trả lỗi 403 thống nhất khi JWT hợp lệ nhưng không đủ quyền truy cập.
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.warn("Từ chối truy cập: {}", request.getRequestURI());
        write(response, DomainCode.FORBIDDEN);
    }

    // Ghi body lỗi bảo mật theo định dạng JSON chung của API.
    private void write(HttpServletResponse response, DomainCode code) throws IOException {
        ApiResponse<?> body = responseFactory.error(code);
        response.setStatus(code.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
