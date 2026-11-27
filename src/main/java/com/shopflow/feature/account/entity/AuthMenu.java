package com.shopflow.feature.account.entity;

import com.shopflow.common.persistence.dto.BaseEntity;
import com.shopflow.feature.account.constant.MenuStatus;
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
@Table(name = "auth_menu", schema = "ShopFlow")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthMenu extends BaseEntity {

    // Mã menu duy nhất và bất biến, được các quan hệ role-menu tham chiếu.
    @Column(name = "code", nullable = false, length = 50, updatable = false)
    String code;

    // Mã menu cha; để trống nếu đây là menu cấp cao nhất.
    @Column(name = "parent_code", length = 50)
    String parentCode;

    // Tên hiển thị của mục menu.
    @Column(name = "menu_name", nullable = false, length = 150)
    String menuName;

    // Đường dẫn Angular tùy chọn được mở khi chọn menu.
    @Column(name = "route_path", length = 255)
    String routePath;

    // Mã biểu tượng tùy chọn do giao diện quy ước.
    @Column(name = "icon", length = 100)
    String icon;

    // Thứ tự hiển thị không âm trong cùng cấp menu.
    @Column(name = "sort_order", nullable = false)
    int sortOrder;

    // Trạng thái menu được lưu theo tên enum ACTIVE hoặc DISABLED.
    @Enumerated(EnumType.STRING)
    @Column(name = "menu_status", nullable = false, length = 20)
    MenuStatus menuStatus = MenuStatus.ACTIVE;
}
