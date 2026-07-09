package com.ly.ttd.dynamic.tp.event;

import org.dromara.dynamictp.common.properties.DtpProperties;
import org.springframework.context.ApplicationEvent;

/**
 * @author yong.li
 * @since 2026/3/10 21:34
 */
public class NacosConfigUpdateEvent extends ApplicationEvent {
    private DtpProperties properties;

    public NacosConfigUpdateEvent(Object source, DtpProperties properties) {
        super(source);
        this.properties = properties;
    }

    public DtpProperties getProperties() {
        return properties;
    }

    public void setProperties(DtpProperties properties) {
        this.properties = properties;
    }
}
