package com.shopflow.common.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class JwtConfig {
    private static final Logger log = LoggerFactory.getLogger(JwtConfig.class);
    // Dùng chung một khóa HS256 để ký access token và xác minh chữ ký trong ứng dụng hiện tại.
    JwtProperties jwtProperties;

    // Giải mã secret Base64 và từ chối khóa HS256 ngắn hơn 256 bit.
    @Bean
    public SecretKey jwtSecretKey() {
        byte[] secret = Base64.getDecoder().decode(jwtProperties.getSecretBase64());
        // Chỉ ghi độ dài để kiểm tra cấu hình, không ghi nội dung secret vào log.
        log.info("Độ dài chuỗi JWT secret: {} bytes", secret.length);
        if (secret.length < 32) {
            throw new IllegalArgumentException("JWT Ký bí mật không được dưới 32 Byte");
        }
        return new SecretKeySpec(secret, "HmacSHA256");
    }

    // Cung cấp bộ ký HS256 để luồng đăng nhập có thể phát hành access token.
    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
        return NimbusJwtEncoder.withSecretKey(jwtSecretKey)
                .algorithm(MacAlgorithm.HS256)
                .build();
    }

    // Cung cấp bộ xác minh chữ ký, issuer và thời hạn chuẩn cho access token.
    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtSecretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(jwtProperties.getIssuer()));
        return decoder;
    }
}
