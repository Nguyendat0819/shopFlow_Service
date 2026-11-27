package com.shopflow.common.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.shopflow.common.util.SecurityUtil;

import java.io.IOException;
import java.util.UUID;

import static com.shopflow.common.util.Const.HEADER_REQUEST_ID;
import static com.shopflow.common.util.Const.REQUEST_ID;
import static com.shopflow.common.util.Const.USERNAME;

// Gắn requestId và username hiện tại vào MDC để truy vết log của từng request.
@Component
public class MdcLoggingFilter extends OncePerRequestFilter {

    // Gắn requestId và username lấy từ JWT vào MDC trong suốt vòng đời của HTTP request.
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String requestId = request.getHeader(HEADER_REQUEST_ID);
            if (requestId == null || requestId.isBlank()) {
                requestId = UUID.randomUUID().toString();
            }
            MDC.put(REQUEST_ID, requestId);
            MDC.put(USERNAME, SecurityUtil.getCurrentUsername());

            response.setHeader(HEADER_REQUEST_ID, requestId);

            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
