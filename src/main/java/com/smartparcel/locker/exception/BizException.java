package com.smartparcel.locker.exception;

import com.smartparcel.locker.vo.ResultCode;
import lombok.Getter;

@Getter
public class BizException extends RuntimeException {
    private final ResultCode resultCode;

    public BizException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.resultCode = resultCode;
    }

    public BizException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }
}
