package com.atstar.sell.VO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Dawn
 * @Date: 2022/4/12 17:40
 */
@Data
public class ProductVO implements Serializable {

    private static final long serialVersionUID = 1004624567554477064L;

    @JsonProperty("name")
    private String categoryName;

    @JsonProperty("type")
    private Integer categoryType;

    @JsonProperty("foods")
    private List<ProductInfoVO> productInfoVOList;
}
