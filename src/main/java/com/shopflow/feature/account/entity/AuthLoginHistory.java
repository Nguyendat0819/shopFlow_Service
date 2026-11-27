package com.shopflow.feature.account.entity;

import com.shopflow.common.persistence.dto.BaseEntity;
import com.shopflow.feature.account.constant.LoginFailureReason;
import com.shopflow.feature.account.constant.LoginResult;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Table(name = "auth_login_history", schema = "ShopFlow")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthLoginHistory extends BaseEntity {

    // Mã tài khoản tìm thấy trong lần đăng nhập; null nếu username không tồn tại.
    @Column(name = "user_code", length = 50)
    String userCode;

    // Username người dùng đã gửi, kể cả khi không tìm thấy tài khoản.
    @Column(name = "username_entered", nullable = false, length = 100)
    String usernameEntered;

    // Kết quả đăng nhập được giới hạn theo CHECK constraint SUCCESS hoặc FAILED.
    @Enumerated(EnumType.STRING)
    @Column(name = "login_result", nullable = false, length = 20)
    LoginResult loginResult;

    // Nguyên nhân lỗi chuẩn hóa; không ghi password, token hoặc nội dung nhạy cảm.
    @Enumerated(EnumType.STRING)
    @Column(name = "failure_reason", length = 255)
    LoginFailureReason failureReason;

    // Địa chỉ IP có thể null nếu request không cung cấp được thông tin này.
    @Column(name = "ip_address", length = 45)
    String ipAddress;

    // User-Agent của client để hỗ trợ tra soát thiết bị đăng nhập.
    @Column(name = "user_agent", length = 500)
    String userAgent;
}
