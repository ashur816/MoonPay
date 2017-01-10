package com.martin.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * @ClassName: ObjectUtils
 * @Description: 对象使用工具类
 * @author ZXY
 * @date 2016/6/30 16:42
 */
public class ObjectUtils {

    /**
     * 判断对象为空
     * @param obj
     * @return true 对象等于空 ， false 对象不等于空
     * @throws Exception
     */
    public	static	boolean	isEmpty(Object obj) {
        if(obj == null){
            return true;
        }
        //不同类型进行不同处理
        if(obj instanceof String){
            String	str	= (String) obj;
            return ("null".equals(str.trim()) || "".equals(str.trim()));
        }else if(obj instanceof Long){
            return obj == null || (Long)obj == 0L;
        }else if(obj instanceof Collection<?>){
            return ((Collection<?>) obj).isEmpty();
        }else if(obj instanceof Collection<?>){
            return ((Collection<?>) obj).isEmpty();
        }else if(obj instanceof Map<?,?>){
            return ((Map<?,?>) obj).isEmpty();
        }else if(obj.getClass().isArray()){
            return ((Array.getLength(obj)==0));
        }else{
            //没有找到类型抛出异常
            return false;
        }
    }

    /**
     * 判断对象不为空
     * @param obj
     * @return true 不为空, false 为空
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }
}
