package com.ly.ttd.inf.rpc.core.util;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Properties;

/**
 * 应用工具类——读取 META-INF/app.properties 中的应用信息。
 * <p>
 */
@Slf4j
public class AppUtils {
    private static String APP_NAME;
    private static String APP_VERSION;

    static {
        try {
            Properties props = new Properties();
            try (InputStream is = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("META-INF/app.properties")) {
                if (is != null) {
                    props.load(is);
                    APP_NAME = props.getProperty("app.name");
                    APP_VERSION = props.getProperty("app.version");
                    log.info("Loaded app info: name={}, version={}", APP_NAME, APP_VERSION);
                } else {
                    log.warn("META-INF/app.properties not found, use default app info");
                }
            }
        } catch (Exception e) {
            log.warn("Failed to load app.properties", e);
        }

        if (APP_NAME == null) {
            APP_NAME = "unknown";
        }
        if (APP_VERSION == null) {
            APP_VERSION = "1.0.0";
        }
    }

    public static String getAppName() {
        return APP_NAME;
    }

    public static String getAppVersion() {
        return APP_VERSION;
    }
}
