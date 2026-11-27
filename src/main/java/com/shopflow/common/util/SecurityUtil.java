package com.shopflow.common.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.shopflow.common.util.Const.ANONYMOUS;
import static com.shopflow.common.util.Const.PREFERRED_USERNAME;
import static com.shopflow.common.util.Const.ROLES;

// Đọc JWT, username, vai trò và quyền của request hiện tại từ SecurityContextHolder.
@UtilityClass
public final class SecurityUtil {

    // Trả JWT đã được Spring Security xác minh hoặc empty nếu request chưa xác thực bằng JWT.
    public static Optional<Jwt> getJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return Optional.of(jwtAuth.getToken());
        }
        return Optional.empty();
    }
    // Lấy username từ claim preferred_username; trả anonymous nếu request không có JWT.
    public static String getCurrentUsername() {
        return getJwt().map(jwt -> jwt.getClaimAsString(PREFERRED_USERNAME)).orElse(ANONYMOUS);
    }

    // Lấy tập vai trò trong JWT hiện tại; trả tập rỗng nếu request chưa xác thực.
    public static Set<String> getRoles() {
        Set<String> roles = new HashSet<>();
        if (getJwt().isPresent()) {
            roles = getRolesFromJwt(getJwt().get());
        }
        return roles;
    }

    // Đọc claim roles từ JWT và chuẩn hóa kết quả thành tập hợp không trùng lặp.
    public static Set<String> getRolesFromJwt(Jwt jwt) {
        if (jwt == null) {
            return Set.of();
        }
        List<String> roles = jwt.getClaimAsStringList(ROLES);
        return roles == null ? Set.of() : new HashSet<>(roles);
    }

    // Lấy permission authority theo quy ước feature:action đã được gán trong SecurityContext.
    public static Set<String> getPermissions() {
        Set<String> permissions = new HashSet<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            permissions = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(Objects::nonNull)
                    .filter(auth -> auth.contains(":"))
                    .collect(Collectors.toSet());
        }
        return permissions;
    }

    // Lấy chuỗi access token đã được xác minh để chuyển tiếp khi gọi service nội bộ.
    public static String getTokenValue() {
        return getJwt().map(Jwt::getTokenValue).orElse(null);
    }

    // Cho biết request hiện tại có JWT đã được xác minh hay không.
    public static boolean isAuthenticated() {
        return getJwt().isPresent();
    }
}
