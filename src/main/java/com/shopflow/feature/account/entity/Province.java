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
@Table(name = "province", schema = "ShopFlow")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Province extends BaseEntity {

    // Tên tỉnh dùng trong danh mục địa chỉ; script hiện không đặt UNIQUE cho trường này.
    @Column(name = "province_name", nullable = false, length = 100)
    String provinceName;
}
