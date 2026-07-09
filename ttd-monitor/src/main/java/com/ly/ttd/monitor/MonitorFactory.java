package com.ly.ttd.monitor;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yong.li
 * @since 2026/3/24 13:00
 */
@Component
public class MonitorFactory {
    private static Map<String, AbstractMonitorService> serviceMap = new HashMap<>();
    @Resource
    private ApplicationContext context;


    @PostConstruct
    public void init(){
        context.getBeansOfType(AbstractMonitorService.class)
                .values().forEach(t -> {
                    serviceMap.put(t.getType(), t);
                });
    }

    public static AbstractMonitorService getInstance(String type){
        return serviceMap.get(type);
    }


}
