package com.martin.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ZXY
 * @ClassName: Datasource
 * @Description:
 * @date 2017/4/6 15:50
 */
// 在运行时执行
@Retention(RetentionPolicy.RUNTIME)
// 注解适用地方(字段和方法)
@Target({ElementType.METHOD})
public @interface Datasource {
    String paramValue() default "johness";// 表示我的注解需要一个参数 名为"paramValue" 默认值为"johness"
}
