package com.atstar.sell.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * 类目
 * @Author: Dawn
 * @Date: 2022/4/11 21:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity // 将表映射为实体
@DynamicUpdate
public class ProductCategory {

    /**
     * 类目Id
     *
     * JPA strategy提供的四种标准用法为TABLE,SEQUENCE,IDENTITY,AUTO.
     *
     * TABLE：使用一个特定的数据库表格来保存主键。
     * SEQUENCE：根据底层数据库的序列来生成主键，条件是数据库支持序列。
     * IDENTITY：主键由数据库自动生成（主要是自动增长型）
     * AUTO：主键由程序控制。
     */
    @Id  // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // @GeneratedValue注解存在的意义主要就是为一个实体生成一个唯一标识的主键
    private Integer categoryId;

    /**
     * 类目名字
     */
    private String categoryName;

    /**
     * 类目编号
     */
    private Integer categoryType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
