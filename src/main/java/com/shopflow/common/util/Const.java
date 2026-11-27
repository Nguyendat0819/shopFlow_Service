package com.shopflow.common.util;

import java.time.format.DateTimeFormatter;

/**
 * Hằng số dùng chung toàn dự án — gom về 1 chỗ để tránh hard-code ở nhiều nơi.
 */
public final class Const {

    private Const() {
    }

    // Claim chứa các mã vai trò mà bộ chuyển đổi JWT đọc để tạo ROLE_* authority.
    public static final String ROLES = "roles";
    // Tiền tố authority theo quy ước Spring Security cho vai trò.
    public static final String ROLE_PREFIX = "ROLE_";
    // Claim chứa username để ứng dụng hiển thị và ghi nhận người thao tác.
    public static final String PREFERRED_USERNAME = "preferred_username";
    // Tên đại diện khi request không có JWT đã xác thực.
    public static final String ANONYMOUS = "anonymous";
    // Mã vai trò quản trị được các bộ kiểm tra quyền ưu tiên.
    public static final String ADMIN = "ADMIN";

    // ---- Logging / MDC ----
    public static final String REQUEST_ID = "requestId";
    public static final String USERNAME = "username";
    public static final String HEADER_REQUEST_ID = "X-Request-Id";

    // ---- Pagination ----
    public static final int PAGE_DEFAULT = 0;
    public static final int SIZE_DEFAULT = 15;
    public static final int SIZE_MAX = 200;

    // ---- File ----
    public static final DateTimeFormatter FILE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");


    // Cờ xoá mềm is_deleted
    public static final int NOT_DELETED = 0;
    public static final int DELETED = 1;
}
