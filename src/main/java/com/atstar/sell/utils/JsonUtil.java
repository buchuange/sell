package com.atstar.sell.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @Author: Dawn
 * @Date: 2022/4/21 22:18
 */
public class JsonUtil {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String print(Object src) {

        return gson.toJson(src);
    }
}

