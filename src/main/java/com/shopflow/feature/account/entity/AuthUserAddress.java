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
@Table(name = "auth_user_address", schema = "ShopFlow")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthUserAddress extends BaseEntity {

    // Mã tài khoản sở hữu địa chỉ.
    @Column(name = "user_code", nullable = false, length = 50)
    String userCode;

    // Nhãn tùy chọn giúp người dùng phân biệt địa chỉ như Nhà riêng hoặc Văn phòng.
    @Column(name = "address_label", length = 100)
    String addressLabel;

    // Tên người nhận hàng, bắt buộc theo schema.
    @Column(name = "recipient_name", nullable = false, length = 200)
    String recipientName;

    // Số điện thoại người nhận hàng.
    @Column(name = "phone", nullable = false, length = 20)
    String phone;

    // ID phường được lưu dạng snapshot; schema không tạo khóa ngoại tới bảng ward.
    @Column(name = "ward_id", nullable = false)
    Long wardId;

    // Tên phường tại thời điểm lưu địa chỉ, không tự đổi khi danh mục thay đổi.
    @Column(name = "ward_name", nullable = false, length = 255)
    String wardName;

    // ID tỉnh được lưu dạng snapshot; schema không tạo khóa ngoại tới bảng province.
    @Column(name = "province_id", nullable = false)
    Long provinceId;

    // Tên tỉnh tại thời điểm lưu địa chỉ, không tự đổi khi danh mục thay đổi.
    @Column(name = "province_name", nullable = false, length = 255)
    String provinceName;

    // Phần địa chỉ chi tiết như số nhà, đường hoặc khu vực.
    @Column(name = "address_detail", nullable = false, length = 255)
    String addressDetail;

    // Ghi chú giao hàng tùy chọn.
    @Column(name = "note", length = 255)
    String note;

    // Đánh dấu địa chỉ mặc định; filtered unique index giới hạn một địa chỉ active trên mỗi tài khoản.
    @Column(name = "is_default", nullable = false)
    boolean isDefault;
}
