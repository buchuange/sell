package com.atstar.sell.VO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * http请求 返回的最外层对象
 * @Author: Dawn
 * @Date: 2022/4/12 17:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultVO<T> implements Serializable {

    // 快捷键 shift+G
    private static final long serialVersionUID = 183257227019846160L;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String msg;

    /**
     * 具体内容
     */
    private T data;
}
