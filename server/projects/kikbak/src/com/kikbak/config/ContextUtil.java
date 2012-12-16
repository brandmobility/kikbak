package com.kikbak.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ContextUtil implements ApplicationContextAware {
    private static ApplicationContext context;
    private static String realPath;
    
    @Override
    public synchronized void setApplicationContext(ApplicationContext context) throws BeansException {
        ContextUtil.context = context;
    }
    
    public static <T> T getBean(String beanName, Class<T> daoClass) {
        return context.getBean(beanName, daoClass);
    }
    
    public static String getRealPath() {
        return realPath;
    }
    
    public static void setRealPath(String path) {
        realPath = path;
    }
    
}
