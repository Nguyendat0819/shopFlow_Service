package com.shopflow.feature.account.entity;

import com.shopflow.common.persistence.dto.BaseEntity;
import com.shopflow.feature.account.constant.AccountStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@Table(name = "auth_user_account", schema = "ShopFlow")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthUserAccount extends BaseEntity {

    // Mã nghiệp vụ bất biến, do server sinh và được các bảng khác dùng làm khóa tham chiếu.
    @Column(name = "code", nullable = false, length = 50, updatable = false)
    String code;

    // Tên đăng nhập đã chuẩn hóa và duy nhất trên toàn bảng, kể cả tài khoản xóa mềm.
    @Column(name = "username", nullable = false, length = 100)
    String username;

    // Mật khẩu đã băm bằng PasswordEncoder; không trả trường này ra API hoặc log.
    @Column(name = "password_hash", nullable = false, length = 255)
    String passwordHash;

    // Email tùy chọn; schema hiện tại không đặt ràng buộc duy nhất.
    @Column(name = "email", length = 254)
    String email;

    // Trạng thái vòng đời tài khoản được lưu theo tên enum trong SQL Server.
    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false, length = 20)
    AccountStatus accountStatus = AccountStatus.PENDING;

    // Số lần xác thực sai liên tiếp, dùng để áp dụng chính sách khóa tài khoản.
    @Column(name = "failed_login_count", nullable = false)
    int failedLoginCount;

    // Thời điểm tự hết khóa; null biểu thị khóa thủ công hoặc tài khoản chưa bị khóa theo thời hạn.
    @Column(name = "locked_until")
    LocalDateTime lockedUntil;

    // Thời điểm đăng nhập thành công gần nhất.
    @Column(name = "last_login_at")
    LocalDateTime lastLoginAt;

    // Thời điểm đổi mật khẩu gần nhất, dùng để vô hiệu hóa phiên cũ theo yêu cầu nghiệp vụ.
    @Column(name = "password_changed_at")
    LocalDateTime passwordChangedAt;
}
