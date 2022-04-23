package com.atstar.sell.exception;

import com.atstar.sell.enums.ResultEnum;
import lombok.Getter;

/**
 * @Author: Dawn
 * @Date: 2022/4/14 01:43
 */
@Getter
public class SellException extends RuntimeException {

    private final ResultEnum resultEnum;

    private String msg;

    public SellException(ResultEnum resultEnum) {

        super(resultEnum.getMsg());

        this.resultEnum = resultEnum;

        this.msg = resultEnum.getMsg();
    }

    public SellException(ResultEnum resultEnum, String msg) {

        super(msg);

        this.resultEnum = resultEnum;

        this.msg = msg;
    }
}
