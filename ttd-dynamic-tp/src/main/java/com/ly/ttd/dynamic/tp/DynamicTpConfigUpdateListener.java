package com.ly.ttd.dynamic.tp;

import com.ly.ttd.dynamic.tp.event.NacosConfigUpdateEvent;
import org.dromara.dynamictp.common.properties.DtpProperties;
import org.dromara.dynamictp.core.DtpRegistry;
import org.springframework.context.ApplicationListener;

/**
 * @author yong.li
 * @since 2026/3/10 21:40
 */
public class DynamicTpConfigUpdateListener implements ApplicationListener<NacosConfigUpdateEvent> {
    @Override
    public void onApplicationEvent(NacosConfigUpdateEvent event) {
        DtpProperties dtpProperties = event.getProperties();
        DtpRegistry.refresh(dtpProperties);

    }
}
