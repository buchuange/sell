package com.atstar.sell.handler;

import com.atstar.sell.VO.ResultVO;
import com.atstar.sell.enums.ResultEnum;
import com.atstar.sell.exception.SellException;
import com.atstar.sell.exception.SellerAuthorizeException;
import com.atstar.sell.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Objects;

/**
 * @Author: Dawn
 * @Date: 2022/4/16 17:42
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResultVO handle(RuntimeException e) {

        return ResultVOUtil.error(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(SellException.class)
    public ResultVO handle(SellException s) {

        return ResultVOUtil.error(s.getResultEnum(), s.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(SellerAuthorizeException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResultVO handle(SellerAuthorizeException s) {

        return ResultVOUtil.error(ResultEnum.USER_NOT_LOGIN, s.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(BindException.class)
    public ResultVO notValidExceptionHandle(BindException e) {

        BindingResult bindingResult = e.getBindingResult();
        return ResultVOUtil.error(ResultEnum.PARAM_ERROR,
                Objects.requireNonNull(bindingResult.getFieldError()).getField() + ": " +
                        bindingResult.getFieldError().getDefaultMessage());
    }

    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResultVO notValidExceptionHandle(MissingServletRequestParameterException e) {

        log.error(e.getParameterName() + "==========参数为空");
        return ResultVOUtil.error(ResultEnum.PARAM_ERROR, e.getParameterName() + "参数不能为空");
    }
}
