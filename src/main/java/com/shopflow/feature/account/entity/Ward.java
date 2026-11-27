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
@Table(name = "ward", schema = "ShopFlow")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Ward extends BaseEntity {

    // ID tỉnh quản lý phường này.
    @Column(name = "province_id", nullable = false)
    Long provinceId;

    // Tên phường dùng để chọn địa chỉ; script hiện không đặt UNIQUE cho trường này.
    @Column(name = "ward_name", nullable = false, length = 100)
    String wardName;
}
