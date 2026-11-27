package com.shopflow.feature.account.entity;

import com.shopflow.common.persistence.dto.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Table(name = "auth_role", schema = "ShopFlow")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthRole extends BaseEntity {

    // Mã vai trò duy nhất, được các quan hệ user-role và role-permission tham chiếu.
    @Column(name = "code", nullable = false, length = 50, updatable = false)
    String code;

    // Tên hiển thị của vai trò.
    @Column(name = "role_name", nullable = false, length = 100)
    String roleName;

    // Mô tả tùy chọn về phạm vi nghiệp vụ của vai trò.
    @Column(name = "description", length = 500)
    String description;
}
