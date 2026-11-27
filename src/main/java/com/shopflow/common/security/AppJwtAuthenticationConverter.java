package com.shopflow.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import static com.shopflow.common.util.Const.ROLE_PREFIX;
import static com.shopflow.common.util.SecurityUtil.getRolesFromJwt;

@Component
@RequiredArgsConstructor
public class AppJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final PermissionResolver permissionResolver;

    // Chuyển claim roles trong JWT và quyền đã phân giải thành authorities của Spring Security.
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        Set<String> roles = getRolesFromJwt(jwt);

        // Tiền tố ROLE_ giúp các quy tắc hasRole của Spring nhận diện vai trò.
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role)));
        // Quyền nghiệp vụ được lấy từ PermissionResolver, không tự có sẵn trong JWT.
        permissionResolver.permissionsOf(roles)
                .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission)));

        return new JwtAuthenticationToken(jwt, authorities);
    }
}
