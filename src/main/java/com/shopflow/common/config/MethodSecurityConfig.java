package com.shopflow.common.config;

import com.shopflow.common.security.CustomPermissionEvaluator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.core.annotation.AnnotationTemplateExpressionDefaults;

// Kết nối permission evaluator với authorities được dựng sau khi JWT được xác minh.
@Configuration
public class MethodSecurityConfig {
    // Gắn evaluator tùy chỉnh để hasPermission so khớp authority từ JWT.
    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(CustomPermissionEvaluator evaluator) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(evaluator);
        return handler;
    }

    // Cho phép meta-annotation thay {value} bằng mã quyền cần kiểm tra.
    @Bean
    static AnnotationTemplateExpressionDefaults annotationTemplateExpressionDefaults() {
        return new AnnotationTemplateExpressionDefaults();
    }
}
