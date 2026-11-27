package com.shopflow.common.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Meta-annotation yêu cầu người gọi có một permission authority lấy từ JWT.
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@permissionChecker.has('{value}')")
public @interface RequiresPermission {

    // Mã quyền phải khớp với authority được nạp cho vai trò trong JWT.
    String value();
}
