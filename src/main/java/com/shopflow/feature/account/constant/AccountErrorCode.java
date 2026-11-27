package com.shopflow.feature.account.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// Mã và thông báo lỗi nghiệp vụ được trích từ danh mục AUTH trong tài liệu Account.
@Getter
@RequiredArgsConstructor
public enum AccountErrorCode {

    // Thông tin đăng nhập sai; không tiết lộ username có tồn tại hay không.
    INVALID_CREDENTIALS("AUTH-001", "Tên đăng nhập hoặc mật khẩu không đúng.", HttpStatus.UNAUTHORIZED),
    // Tài khoản đang chờ được kích hoạt.
    ACCOUNT_PENDING("AUTH-002", "Tài khoản chưa được kích hoạt.", HttpStatus.FORBIDDEN),
    // Tài khoản đang bị khóa hoặc vô hiệu hóa.
    ACCOUNT_UNAVAILABLE("AUTH-003", "Tài khoản hiện không thể đăng nhập.", HttpStatus.FORBIDDEN),
    // Người dùng hiện tại không có permission cần thiết.
    FORBIDDEN("AUTH-004", "Bạn không có quyền thực hiện chức năng này.", HttpStatus.FORBIDDEN),
    // Username hoặc mã duy nhất đã được sử dụng.
    DUPLICATE_DATA("AUTH-005", "Thông tin đã tồn tại.", HttpStatus.CONFLICT),
    // Refresh token hết hạn, bị thu hồi hoặc không hợp lệ.
    INVALID_SESSION("AUTH-006", "Phiên đăng nhập không còn hợp lệ.", HttpStatus.UNAUTHORIZED),
    // Request vi phạm quy tắc validation.
    INVALID_DATA("AUTH-007", "Dữ liệu nhập chưa hợp lệ.", HttpStatus.BAD_REQUEST),
    // Request vượt ngưỡng giới hạn tốc độ.
    RATE_LIMITED("AUTH-008", "Bạn thao tác quá nhanh, vui lòng thử lại sau.", HttpStatus.TOO_MANY_REQUESTS),
    // Trạng thái yêu cầu không nằm trong bảng chuyển đổi hợp lệ.
    INVALID_STATUS_TRANSITION("AUTH-009", "Không thể chuyển tài khoản sang trạng thái này.", HttpStatus.CONFLICT),
    // Dữ liệu đã bị cập nhật đồng thời bởi một thao tác khác.
    CONCURRENT_UPDATE("AUTH-010", "Dữ liệu đã bị thay đổi bởi thao tác khác, vui lòng tải lại và thử lại.", HttpStatus.CONFLICT),
    // Mật khẩu mới không đạt các điều kiện bảo mật.
    WEAK_NEW_PASSWORD("AUTH-011", "Mật khẩu mới chưa đáp ứng yêu cầu bảo mật.", HttpStatus.BAD_REQUEST);

    // Mã nghiệp vụ trả trong trường code của ApiResponse.
    private final String code;

    // Thông báo tiếng Việt an toàn để trả cho client.
    private final String message;

    // HTTP status dùng khi bộ xử lý lỗi Account ánh xạ mã nghiệp vụ thành response.
    private final HttpStatus httpStatus;
}
