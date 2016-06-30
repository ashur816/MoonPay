package com.martin.utils;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * @ClassName: ServiceManager
 * @Description: 实现类获取
 * @author ZXY
 * @date 2016/5/26 14:14
 */
public class ServiceContainer<T> {

    public static class Holder {
        private static WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
    }

    private static WebApplicationContext getInstance() {
        return Holder.context;
    }

    @SuppressWarnings({"unchecked", "resource"})
    public T get(String beanId) {
        return (T) getInstance().getBean(beanId);
    }

}
