package com.shopflow.common.exception;

import com.shopflow.common.response.DomainCode;

public class BusinessException extends BaseException {
    public BusinessException(DomainCode domainCode, Object ... args) {
        super(domainCode, args);
    }
}
