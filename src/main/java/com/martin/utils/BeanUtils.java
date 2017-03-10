package com.martin.utils;

import com.martin.constant.TenPublicParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author ZXY
 * @ClassName: BeanUtils
 * @Description:
 * @date 2017/3/2 9:54
 */
public class BeanUtils {
    /**
     * 将一个 JavaBean 对象转化为一个  Map
     *
     * @param bean 要转化的JavaBean 对象
     * @return 转化出来的  Map 对象
     * @throws IntrospectionException    如果分析类属性失败
     * @throws IllegalAccessException    如果实例化 JavaBean 失败
     * @throws InvocationTargetException 如果调用属性的 setter 方法失败
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map convertBean(Object bean)
            throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        Class type = bean.getClass();
        Map returnMap = new HashMap();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);

        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class")) {
                Method readMethod = descriptor.getReadMethod();
                Object result = readMethod.invoke(bean, new Object[0]);
                if (result != null) {
                    returnMap.put(propertyName, result);
                } else {
                    returnMap.put(propertyName, "");
                }
            }
        }
        return returnMap;
    }


    /**
     * 将一个 Map 对象转化为一个 JavaBean
     *
     * @param type 要转化的类型
     * @param map  包含属性值的 map
     * @return 转化出来的 JavaBean 对象
     * @throws IntrospectionException    如果分析类属性失败
     * @throws IllegalAccessException    如果实例化 JavaBean 失败
     * @throws InstantiationException    如果实例化 JavaBean 失败
     * @throws InvocationTargetException 如果调用属性的 setter 方法失败
     */
    @SuppressWarnings("rawtypes")
    public static Object convertMap(Class type, Map map) throws IntrospectionException, IllegalAccessException, InstantiationException, InvocationTargetException {
        BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
        Object obj = type.newInstance(); // 创建 JavaBean 对象

        // 给 JavaBean 对象的属性赋值
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];

            String propertyName = descriptor.getName();
            String capFirstName = propertyName.substring(0, 1).toUpperCase() + propertyName.replaceFirst("\\w", "");

            if (map.containsKey(propertyName) || map.containsKey(capFirstName)) {
                // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
                Object propertyValue = map.get(propertyName);
                Object capFirstValue = map.get(capFirstName);

                Object value = ObjectUtils.isNotEmpty(propertyValue) ? propertyValue : capFirstValue;

                Object[] args = new Object[1];
                args[0] = value;

                descriptor.getWriteMethod().invoke(obj, args);
            }
        }
        return obj;
    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 将bean转成xml
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String convertXml(Map<String, Object> beanMap) throws Exception {
        String retMsg = TenPublicParam.retMsg;
        if (!CollectionUtils.isEmpty(beanMap)) {
            StringBuilder builder = new StringBuilder("<xml>");
            Set<Map.Entry<String, Object>> entrySet = beanMap.entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                String key = entry.getKey();
                Object bean = entry.getValue();
                Field[] fields = bean.getClass().getDeclaredFields();
                if (fields.length > 0) {
                    if (!"root".equals(key)) {//非根节点需要加入子节点名称
                        builder.append("<").append(key).append(">");
                    }
                    if("Articles".equals(key)){
                        builder.append("<item>");
                    }
                    for (Field field : fields) {
                        String name = field.getName();
                        if (!"serialVersionUID".equalsIgnoreCase(name)) {
                            Method m = bean.getClass().getMethod("get" + name);
                            String value = (String) m.invoke(bean);
                            if (StringUtils.isNotBlank(value)) {
                                builder.append("<").append(name).append(">").append(value).append("</").append(name).append(">");
                            }
                        }
                    }
                    if("Articles".equals(key)){
                        builder.append("</item>");
                    }
                    if (!"root".equals(key)) {//非根节点需要加入子节点名称
                        builder.append("</").append(key).append(">");
                    }
                }
            }
            builder.append("</xml>");
            retMsg = builder.toString();
        }
        return retMsg;
    }

}
