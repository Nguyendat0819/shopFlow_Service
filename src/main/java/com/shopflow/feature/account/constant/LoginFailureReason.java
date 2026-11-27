package com.shopflow.feature.account.constant;

// Các nguyên nhân đăng nhập thất bại đã được chuẩn hóa trong tài liệu Account.
public enum LoginFailureReason {
    BAD_CREDENTIALS,
    ACCOUNT_PENDING,
    ACCOUNT_LOCKED,
    ACCOUNT_DISABLED,
    RATE_LIMITED
}
