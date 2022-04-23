package com.atstar.sell.utils;

import com.atstar.sell.VO.ResultVO;
import com.atstar.sell.enums.ResultEnum;

/**
 * @Author: Dawn
 * @Date: 2022/4/12 18:18
 */
public class ResultVOUtil {

    public static <T> ResultVO<T> success(T data) {

        return new ResultVO<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), data);
    }


    public static <T> ResultVO<T> success() {

        return success(null);
    }

    public static <T> ResultVO<T> error(ResultEnum resultEnum, T data) {
        return new ResultVO<>(resultEnum.getCode(), resultEnum.getMsg(), data);
    }

    public static <T> ResultVO<T> error(T data) {

        return new ResultVO<>(ResultEnum.ERROR.getCode(), ResultEnum.ERROR.getMsg(), data);
    }
}
