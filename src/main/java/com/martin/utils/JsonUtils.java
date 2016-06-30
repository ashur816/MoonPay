package com.martin.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * @ClassName: JsonUtils
 * @Description: json 对象转换工具类
 * @author ZXY
 * @date 2016/6/17 13:26
 */
public class JsonUtils {

    private static ObjectMapper objectMapper = new JsonMapper();

    /**
     * @Description: json 转换成对象
     * @param json
     * @param clazz  对象的 class
     * @return 对象
     * @throws IOException
     */
    public static <T> T readValue(String json, Class<T> clazz) throws IOException {
        return objectMapper.readValue(json, clazz);
    }

    /**
     * @Description: 获取json 的属性值
     * @param json
     * @param name   属性名称
     * @return 属性值
     * @throws IOException
     */
    public static String readValueByName(String json, String name) throws IOException {
        Map<?, ?> map = objectMapper.readValue(json, Map.class);
        return map.get(name).toString();
    }

    /**
     * @Description: 获取json 的属性 map
     * @param json
     * @return map
     * @throws IOException
     */
    public static Map<?, ?> readMap(String json) throws IOException {
        Map<?, ?> map = objectMapper.readValue(json, Map.class);
        return map;
    }

    /**
     * @Description: 对象转换成json
     * @param obj  对象
     * @return json
     * @throws IOException
     */
    public static String translateToJson(Object obj) throws IOException {
        return objectMapper.writeValueAsString(obj);
    }
}
