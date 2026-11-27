package com.shopflow.common.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

/**
 * Mã phản hồi và thông điệp mặc định dùng thống nhất trong ShopFlow.
 *
 * <p>Prefix {@code SFL}; nhóm {@code 0xx} là thành công,
 * {@code 4xx} là lỗi yêu cầu và {@code 5xx} là lỗi hệ thống.</p>
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum DomainCode {

    // Thành công
    SUCCESS("SFL-000", HttpStatus.OK, "Thành công"),

    // Lỗi yêu cầu (4xx)
    BAD_REQUEST("SFL-400", HttpStatus.BAD_REQUEST, "Yêu cầu không hợp lệ"),
    UNAUTHORIZED("SFL-401", HttpStatus.UNAUTHORIZED, "Chưa xác thực"),
    INVALID_PARAMETER("SFL-402", HttpStatus.BAD_REQUEST, "Tham số không hợp lệ: {}"),
    FORBIDDEN("SFL-403", HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện thao tác này"),
    NOT_FOUND("SFL-404", HttpStatus.NOT_FOUND, "Không tìm thấy: {}"),
    VALIDATION_ERROR("SFL-405", HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ: {}"),
    TOKEN_EXPIRED("SFL-406", HttpStatus.UNAUTHORIZED, "Phiên đăng nhập đã hết hạn"),
    CONFLICT("SFL-407", HttpStatus.CONFLICT, "Dữ liệu đã tồn tại hoặc đang xung đột: {}"),

    // Mã lỗi liên quan tới đăng nhập và đăng ký
    INVALID_CREDENTIALS("SFL-408", HttpStatus.UNAUTHORIZED, "Tên đăng nhập hoặc mật khẩu không đúng"),
    USERNAME_ALREADY_EXISTS("SFL-409", HttpStatus.CONFLICT, "Tên đăng nhập đã tồn tại"),
    EMAIL_ALREADY_EXISTS("SFL-410", HttpStatus.CONFLICT, "Email đã được đăng ký"),

    // Lỗi hệ thống hoặc dịch vụ ngoài (5xx)
    INTERNAL_ERROR("SFL-500", HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống, vui lòng thử lại sau"),
    EXTERNAL_SERVICE_ERROR("SFL-502", HttpStatus.BAD_GATEWAY, "Lỗi khi gọi dịch vụ ngoài: {}"),
    SERVICE_UNAVAILABLE("SFL-503", HttpStatus.SERVICE_UNAVAILABLE, "Dịch vụ tạm thời không khả dụng");


    String code;
    HttpStatus httpStatus;
    String message;
}
