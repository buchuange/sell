package com.atstar.sell.utils;

import java.util.Random;

/**
 * @Author: Dawn
 * @Date: 2022/4/14 02:15
 */
public class KeyUtil {

    /**
     * 生成唯一主键
     * 格式：时间+随机数
     * @return
     */
    public static synchronized Long generateUniqueKey() {

        Random random = new Random();

        int num = random.nextInt(900000) + 100000;

        return System.currentTimeMillis() + num;
    }
}
