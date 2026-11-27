package com.shopflow.common.security;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private static final String ADMIN_AUTHORITY = "ROLE_ADMIN";

    // Kiểm tra quyền thao tác theo authorities đã gắn vào Authentication từ JWT.
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return hasAuthority(authentication, permission);
    }

    // Kiểm tra quyền thao tác theo authorities; quyền sở hữu bản ghi cần được xử lý riêng ở service.
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId,
                                 String targetType, Object permission) {
        return hasAuthority(authentication, permission);
    }

    // Quyền sở hữu từng bản ghi cần được kiểm tra tại service; evaluator này chỉ kiểm tra quyền thao tác.
    // So khớp quyền yêu cầu với authority của người dùng và xử lý quyền ADMIN.
    private boolean hasAuthority(Authentication authentication, Object permission) {
        if (authentication == null || !authentication.isAuthenticated() || permission == null) {
            return false;
        }

        String requiredAuthority = permission.toString();
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> ADMIN_AUTHORITY.equals(authority.getAuthority())
                        || requiredAuthority.equals(authority.getAuthority()));
    }
}
