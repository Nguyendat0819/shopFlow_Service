package com.shopflow.common.security;

import java.util.Collection;
import java.util.Set;

public interface PermissionResolver {

    // Trả các quyền nghiệp vụ tương ứng với danh sách vai trò đã xác thực trong JWT.
    Set<String> permissionsOf(Collection<String> roles);
}
