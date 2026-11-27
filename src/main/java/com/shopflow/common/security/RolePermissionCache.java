package com.shopflow.common.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "app.security.cache.enabled", havingValue = "true", matchIfMissing = true)
public class RolePermissionCache implements PermissionResolver {

    private final AtomicReference<Map<String, Set<String>>> cache =
            new AtomicReference<>(Map.of());

    // Hợp các quyền đã cache của các vai trò trong JWT hiện tại.
    @Override
    public Set<String> permissionsOf(Collection<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of();
        }

        Set<String> permissions = new HashSet<>();
        Map<String, Set<String>> current = cache.get();
        for (String role : roles) {
            permissions.addAll(current.getOrDefault(role, Set.of()));
        }
        return Set.copyOf(permissions);
    }

    // Thay toàn bộ snapshot nguyên tử để request khác không đọc được cache đang nạp dở.
    public void replace(Map<String, Set<String>> rolePermissions) {
        if (rolePermissions == null || rolePermissions.isEmpty()) {
            cache.set(Map.of());
            return;
        }

        Map<String, Set<String>> snapshot = rolePermissions.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        entry -> Set.copyOf(entry.getValue())));
        cache.set(snapshot);
    }

    // Trả snapshot hiện tại để quản trị hoặc kiểm tra trạng thái cache.
    public Map<String, Set<String>> currentCache() {
        return cache.get();
    }
}
