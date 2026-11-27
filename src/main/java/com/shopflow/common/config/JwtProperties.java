package com.shopflow.common.config;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Component
@ConfigurationProperties(prefix = "app.jwt")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtProperties {
    // Giá trị issuer phải khớp claim iss mà luồng phát hành sẽ ghi vào JWT.
    @NotBlank(message = "JWT issuer không được để trống")
    private String issuer;

    // Thời gian sống access token tính bằng giây; bộ phát hành cần dùng giá trị này để tạo exp.
    @NotNull(message = "JWT access-token-ttl không được để trống")
    @Positive(message = "JWT access-token-ttl phải lớn hơn 0")
    private Long accessTokenTtl;

    // Khóa HS256 dạng Base64; sau khi giải mã phải có ít nhất 32 byte và cần được giữ bí mật.
    @NotBlank(message = "JWT secret-base64 không được để trống")
    private String secretBase64;

}
