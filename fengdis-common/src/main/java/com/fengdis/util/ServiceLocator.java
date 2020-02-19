package com.fengdis.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * @version 1.0
 * @Descrittion: 服务实例化工具类
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public class ServiceLocator implements BeanFactoryAware {
	
    private static BeanFactory beanFactory = null;
    
    private static ServiceLocator servlocator = null;
 
	public void setBeanFactory(BeanFactory factory) throws BeansException {
        ServiceLocator.beanFactory = factory;
    }
 
    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public static ServiceLocator getInstance() {
        if (servlocator == null)
              servlocator = (ServiceLocator) beanFactory.getBean("serviceLocator");
        return servlocator;
    }
 
    /**
    * 根据提供的bean名称得到相应的服务类    
    * @param serviceName bean名称
    */
    public static Object getService(String serviceName) {
        return beanFactory.getBean(serviceName);
    }
 
    /**
    * 根据提供的bean名称得到对应于指定类型的服务类
    * @param serviceName bean名称
    * @param clazz 返回的bean类型,若类型不匹配,将抛出异常
    */
    public static <T> T getService(String serviceName, Class<T> clazz) {
        return (T)beanFactory.getBean(serviceName, clazz);
    }
	
}
