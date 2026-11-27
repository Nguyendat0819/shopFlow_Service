package com.shopflow.common.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Meta-annotation chỉ cho qua khi JWT có đủ mọi permission authority được yêu cầu.
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@permissionChecker.hasAll('{value}'.split(','))")
public @interface RequiresAllPermission {

    // Các mã quyền mà authorities của JWT phải có đầy đủ.
    String[] value();
}
