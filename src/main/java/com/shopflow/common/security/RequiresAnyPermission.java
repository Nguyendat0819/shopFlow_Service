package com.shopflow.common.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Meta-annotation cho qua nếu JWT có ít nhất một permission authority được yêu cầu.
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@permissionChecker.hasAny('{value}'.split(','))")
public @interface RequiresAnyPermission {

    // Các mã quyền được phép thay thế cho nhau khi kiểm tra authorities của JWT.
    String[] value();
}
