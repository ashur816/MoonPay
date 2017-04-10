package com.martin;

import com.martin.filter.Datasource;

import java.lang.reflect.Method;

/**
 * @author ZXY
 * @ClassName: Test
 * @Description:
 * @date 2017/4/6 14:56
 */
public class Test {

    public void getName(String name) {
        System.out.println(name);
    }

    @Datasource
    public void getName1(String name) {
        System.out.println(name);
    }

    @Datasource(paramValue = "ddd")
    public void getName2(String name) {
        System.out.println(name);
    }

    public static void main(String[] args) throws Exception {

        Test test = new Test();
        Method[] methods = Test.class.getDeclaredMethods();
        for (Method method : methods) {
            Datasource annotationTmp = null;
            if (!method.getName().equals("main")) {
                if ((annotationTmp = method.getAnnotation(Datasource.class)) != null) {// 检测是否使用了我们的注解
                    method.invoke(test, annotationTmp.paramValue()); // 如果使用了我们的注解，我们就把注解里的"paramValue"参数值作为方法参数来调用方法
                } else {
                    method.invoke(test, "Rose"); // 如果没有使用我们的注解，我们就需要使用普通的方式来调用方法了
                }
            }
        }
    }
}
