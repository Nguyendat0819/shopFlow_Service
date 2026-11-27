package com.shopflow.feature.account.entity;

import com.shopflow.common.persistence.dto.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "auth_refresh_token", schema = "ShopFlow")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthRefreshToken extends BaseEntity {

    // Mã tài khoản sở hữu phiên.
    @Column(name = "user_code", nullable = false, length = 50)
    String userCode;

    // Hash của refresh token ngẫu nhiên; không lưu token gốc trong database.
    @Column(name = "token_hash", nullable = false, length = 500)
    String tokenHash;

    // Thời điểm refresh token hết hiệu lực.
    @Column(name = "expires_at", nullable = false)
    LocalDateTime expiresAt;

    // Thời điểm thu hồi; null nghĩa là chưa ghi nhận thu hồi.
    @Column(name = "revoked_at")
    LocalDateTime revokedAt;

    // Tên thiết bị tùy chọn dùng để hiển thị phiên đăng nhập.
    @Column(name = "device_name", length = 200)
    String deviceName;

    // Địa chỉ IP tùy chọn ghi nhận nguồn tạo phiên.
    @Column(name = "ip_address", length = 45)
    String ipAddress;
}
