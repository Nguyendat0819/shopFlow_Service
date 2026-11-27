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
@Table(name = "auth_permission", schema = "ShopFlow")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthPermission extends BaseEntity {

    // Mã quyền duy nhất theo dạng nhóm:tài nguyên:hành động, ví dụ admin:role:search.
    @Column(name = "code", nullable = false, length = 50, updatable = false)
    String code;

    // Tên hiển thị của quyền.
    @Column(name = "permission_name", nullable = false, length = 150)
    String permissionName;

    // Mô tả tùy chọn về API hoặc chức năng được quyền này bảo vệ.
    @Column(name = "description", length = 500)
    String description;
}
