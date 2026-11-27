package com.shopflow.common.exception;

import com.shopflow.common.response.DomainCode;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(Object... args) {
        super(DomainCode.NOT_FOUND, args);
    }
}
