package com.atstar.sell.form;

import lombok.Data;

/**
 * @Author: Dawn
 * @Date: 2022/4/21 19:39
 */
@Data
public class CategoryForm {

    private Integer categoryId;

    /**
     * 类目名字
     */
    private String categoryName;

    /**
     * 类目编号
     */
    private Integer categoryType;
}
