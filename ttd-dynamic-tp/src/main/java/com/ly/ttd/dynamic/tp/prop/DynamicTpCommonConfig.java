package com.ly.ttd.dynamic.tp.prop;

import lombok.Data;
import org.dromara.dynamictp.common.entity.DtpExecutorProps;
import org.dromara.dynamictp.common.entity.NotifyPlatform;
import org.dromara.dynamictp.common.entity.TpExecutorProps;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yong.li
 * @since 2026/3/10 21:08
 */

@Data
@Component
@ConfigurationProperties(prefix = "dynamictp")
public class DynamicTpCommonConfig {
    private boolean enabled;
    private String env;
    private boolean enabledBanner;
    private boolean enabledCollect;
    private List<String> collectorTypes;
    private String logPath;
    private String configType;
    private int monitorInterval;
    private List<NotifyPlatform> platforms;
    private DtpExecutorProps globalExecutorProps;
    private List<DtpExecutorProps> executors;
    private TpExecutorProps tomcatTp;
    private TpExecutorProps jettyTp;
    private TpExecutorProps undertowTp;
    private List<TpExecutorProps> dubboTp;
    private List<TpExecutorProps> hystrixTp;
    private List<TpExecutorProps> rocketMqTp;
    private List<TpExecutorProps> grpcTp;
    private List<TpExecutorProps> motanTp;
    private List<TpExecutorProps> okhttp3Tp;
    private List<TpExecutorProps> brpcTp;
    private List<TpExecutorProps> tarsTp;
    private List<TpExecutorProps> sofaTp;
    private List<TpExecutorProps> rabbitmqTp;
    private List<TpExecutorProps> liteflowTp;
    private List<TpExecutorProps> thriftTp;

    public DynamicTpCommonConfig() {
    }
}
