package com.shopflow.common.security;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;

import static com.shopflow.common.util.Const.ADMIN;
import static com.shopflow.common.util.SecurityUtil.getPermissions;
import static com.shopflow.common.util.SecurityUtil.getRoles;

// Kiểm tra authorities đã được dựng sau khi Spring xác minh JWT.
@Component("permissionChecker")
public class PermissionChecker {

    // Kiểm tra người dùng có quyền cụ thể từ authorities đã dựng sau khi xác minh JWT hay không.
    public boolean has(String permissionCode) {
        if (getRoles().contains(ADMIN)) {
            return true;
        }
        return getPermissions().contains(permissionCode);
    }

    // Cho phép thao tác khi người dùng có ít nhất một quyền được yêu cầu.
    public boolean hasAny(String... codes) {
        if (getRoles().contains(ADMIN)) {
            return true;
        }
        Set<String> permissions = getPermissions();
        for (String code : codes) {
            if (permissions.contains(code)) {
                return true;
            }
        }
        return false;
    }

    // Cho phép thao tác khi người dùng có đủ tất cả quyền được yêu cầu.
    public boolean hasAll(String... codes) {
        if (getRoles().contains(ADMIN)) {
            return true;
        }
        Set<String> permissions = getPermissions();
        for (String code : codes) {
            if (!permissions.contains(code)) {
                return false;
            }
        }
        return true;
    }
}
