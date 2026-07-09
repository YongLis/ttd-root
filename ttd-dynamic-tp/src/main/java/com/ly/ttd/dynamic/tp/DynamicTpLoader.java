package com.ly.ttd.dynamic.tp;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.client.config.listener.impl.PropertiesListener;
import com.ly.ttd.config.nacos.consts.DefaultNacosConfigEnum;
import com.ly.ttd.config.nacos.consts.NacosConsts;
import com.ly.ttd.config.nacos.utils.TextUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * 初始化加载远程配置
 * @author yong.li
 * @since 2026/3/10 10:33
 */
@Component
public class DynamicTpLoader implements EnvironmentPostProcessor {
    private Logger logger = LoggerFactory.getLogger(DynamicTpLoader.class);
    private static final String DYNAMIC_TP_CONFIG = "dynamicTpConfig";
    private static final String DYNAMIC_TP_CONFIG_APP = "dynamicTpConfig-app";


    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String appName = environment.getProperty(NacosConsts.APPLICATION_NAME);
        // 加载全局配置
        loadRemoteConfig(environment, DefaultNacosConfigEnum.DYNAMIC_TP.getNameSpaceId(),
                DefaultNacosConfigEnum.DYNAMIC_TP.getGroup(),
                DefaultNacosConfigEnum.DYNAMIC_TP.getDataId(), DYNAMIC_TP_CONFIG);


        // 加载应用配置
        loadRemoteConfig(environment, appName,
                DefaultNacosConfigEnum.APP.getGroup(),
                DefaultNacosConfigEnum.APP.getDataId(), DYNAMIC_TP_CONFIG_APP);
    }

    private void loadRemoteConfig(ConfigurableEnvironment environment, String nameSpaceId, String group, String dataId, String sourceKey) {
        String nacosAddress = environment.getProperty(NacosConsts.NACOS_ADDRESS);
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, nacosAddress);
        properties.put(PropertyKeyConst.NAMESPACE, nameSpaceId);
        try {
            ConfigService configService = NacosFactory.createConfigService(properties);
            String global = configService.getConfig(dataId,
                    group,
                    NacosConsts.TIMEOUT);

            if (StringUtils.isNotBlank(global)) {
                logger.info("load dynamic tp config from nacos, dataId={} : {}", dataId, JSON.toJSONString(properties));
                Properties props = TextUtils.stringToProperties(global);
                PropertiesPropertySource propertySource = new PropertiesPropertySource(sourceKey, props);
                environment.getPropertySources().addLast(propertySource);
            } else {
                logger.warn("load dynamic tp config from nacos failed, please check config, dataId={}", dataId);
            }

        } catch (Exception e) {
            logger.error("load config from nacos failed, please check config", e);
            throw new RuntimeException(e);
        }
    }
}
