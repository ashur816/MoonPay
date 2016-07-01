package com.martin.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

    /**
     * @Description: 从文件读取json数据
     * @param filePath 文件路径
     * @return
     * @throws
     */
    public static String readJsonByFile(String filePath) throws Exception {
        File file = new File(filePath);
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        int length;
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[1024];
        while ((length = reader.read(buffer, 0, 1024)) != -1) {
            sb.append(buffer, 0, length);
        }
        return sb.toString();
    }
}
