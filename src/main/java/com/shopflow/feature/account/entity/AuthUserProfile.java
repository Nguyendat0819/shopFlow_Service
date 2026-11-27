package com.shopflow.feature.account.entity;

import com.shopflow.common.persistence.dto.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "auth_user_profile", schema = "ShopFlow")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthUserProfile extends BaseEntity {

    // Mã tài khoản sở hữu hồ sơ.
    @Column(name = "user_code", nullable = false, length = 50)
    String userCode;

    // Họ tên hiển thị, bắt buộc và tối đa 200 ký tự theo schema.
    @Column(name = "full_name", nullable = false, length = 200)
    String fullName;

    // Họ tên đã chuẩn hóa để tìm kiếm; không có ràng buộc duy nhất trong database.
    @Column(name = "name_search", length = 255)
    String nameSearch;

    // Ngày sinh tùy chọn của người dùng.
    @Column(name = "birth_date")
    LocalDate birthDate;

    // Số điện thoại tùy chọn trong hồ sơ cá nhân.
    @Column(name = "phone", length = 20)
    String phone;

    // Đường dẫn ảnh đại diện tùy chọn, tối đa 255 ký tự.
    @Column(name = "avatar_url", length = 255)
    String avatarUrl;
}
