package com.victor.iatms.quartz;

import lombok.RequiredArgsConstructor;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

/**
 * Spring Job工厂
 * 支持在Quartz Job中注入Spring Bean
 */
@Component
@RequiredArgsConstructor
public class SpringJobFactory extends SpringBeanJobFactory {

    private final AutowireCapableBeanFactory beanFactory;

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        // 调用父类方法创建Job实例
        Object job = super.createJobInstance(bundle);
        // 将Job实例交由Spring容器管理，实现依赖注入
        beanFactory.autowireBean(job);
        return job;
    }
}

