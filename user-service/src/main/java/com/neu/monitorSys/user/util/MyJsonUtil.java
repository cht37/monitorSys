package com.neu.monitorSys.user.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class MyJsonUtil {
    // 创建一个线程安全的ObjectMapper实例
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 私有构造函数，防止实例化
    private MyJsonUtil() {
    }

    /**
     * 将对象转换为JSON字符串
     *
     * @param object 要转换的对象
     * @return JSON字符串
     * @throws Exception 转换异常
     */
    public static String toJson(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }

    /**
     * 将JSON字符串转换为对象
     *
     * @param json  JSON字符串
     * @param clazz 对象类型
     * @param <T>   对象泛型
     * @return 转换后的对象
     * @throws Exception 转换异常
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }

    /**
     * 将JSON字符串转换为对象列表
     *
     * @param json JSON字符串
     * @param <T>  对象泛型
     * @return 转换后的对象列表
     * @throws Exception 转换异常
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
    }

    /**
     * 将对象列表转换为JSON字符串
     *
     * @param list 要转换的对象列表
     * @return JSON字符串
     * @throws Exception 转换异常
     */
    public static String toJson(List<?> list) throws Exception {
        return objectMapper.writeValueAsString(list);
    }

    /**
     * 将JSON字符串转换为指定类型的对象
     *
     * @param json          JSON字符串
     * @param typeReference 对象类型引用
     * @param <T>           对象泛型
     * @return 转换后的对象
     * @throws Exception 转换异常
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) throws Exception {
        return objectMapper.readValue(json, typeReference);
    }
}
