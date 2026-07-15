package com.ly.ttd.inf.daf;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;
import java.io.StringReader;
import java.util.Properties;

/**
 * DruidDataSource 从库数据源构建 Bean。
 * <p>
 * 通过构造器传入 Nacos 配置中心的 {@code namespace}、{@code dataId}、{@code group}，
 * 从 Nacos 远程拉取配置，解析 {@code ttd.db.slave.*} 前缀的属性，构建 DruidDataSource。
 * <p>
 * 支持两种使用方式:
 * <ul>
 *   <li>XML 配置:
 *     <pre>{@code
 *     <bean id="slaveDataSource" class="com.ly.ttd.inf.daf.DataSourceFactoryBean">
 *         <constructor-arg name="namespace" value="${ttd.nacos.server-addr}"/>
 *         <constructor-arg name="namespace" value="${ttd.nacos.namespace}"/>
 *         <constructor-arg name="dataId" value="db-slave-config"/>
 *         <constructor-arg name="group" value="DEFAULT_GROUP"/>
 *     </bean>
 *     }</pre>
 *   </li>
 *   <li>Java Config:
 *     <pre>{@code
 *     @Bean
 *     public DataSourceFactoryBean slaveDataSource() {
 *         return new DataSourceFactoryBean(serverAddr,namespace, "db-slave-config", "DEFAULT_GROUP");
 *     }
 *     }</pre>
 *   </li>
 * </ul>
 * <p>
 * Nacos 配置内容示例 (properties 格式):
 * <pre>
 * ttd.db.slave.url=jdbc:mysql://127.0.0.1:3306/pay_slave?useSSL=false&characterEncoding=utf-8
 * ttd.db.slave.username=root
 * ttd.db.slave.password=123456
 * ttd.db.slave.driver-class-name=com.mysql.cj.jdbc.Driver
 * ttd.db.slave.initial-size=5
 * ttd.db.slave.min-idle=5
 * ttd.db.slave.max-active=20
 * ttd.db.slave.max-wait=60000
 * </pre>
 *
 * @author yong.li
 * @since 2026/7/10 09:43
 */
public class DataSourceFactoryBean implements FactoryBean<DataSource>, InitializingBean, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(DataSourceFactoryBean.class);

    /** Nacos 配置中从库属性的前缀 */
    public static final String prefix = "ttd.db.slave.";

    private static final String DEFAULT_GROUP = "DEFAULT_GROUP";
    private static final long TIMEOUT_MS = 5000;

    /** Nacos 配置参数 */
    private final String namespace;
    private final String dataId;
    private final String group;
    private final String serverAddr;

    /** 构建的数据源实例 */
    private DruidDataSource dataSource;

    public DataSourceFactoryBean(String serverAddr,String namespace, String dataId, String group) {
        this.namespace = namespace;
        this.dataId = dataId;
        this.group = group;
        this.serverAddr = serverAddr;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.dataSource = build();
    }

    /**
     * 从 Nacos 读取配置并构建 DruidDataSource。
     */
    private DruidDataSource build() throws Exception {
        log.info("DataSourceFactoryBean building — namespace={}, dataId={}, group={}, serverAddr={}",
                namespace, dataId, group, serverAddr);

        // 1. 创建 Nacos ConfigService
        ConfigService configService = createConfigService();

        // 2. 从 Nacos 拉取配置内容
        String content = configService.getConfig(dataId, group, TIMEOUT_MS);
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalStateException(
                    String.format("Nacos config not found — namespace=%s, dataId=%s, group=%s",
                            namespace, dataId, group));
        }
        log.info("Nacos config loaded — dataId={}, contentLength={}", dataId, content.length());

        // 3. 解析配置为 Properties
        Properties allProps = new Properties();
        allProps.load(new StringReader(content));

        // 4. 提取 ttd.db.slave.* 前缀属性，去掉前缀后传入 Druid
        Properties dbProps = new Properties();
        for (String key : allProps.stringPropertyNames()) {
            if (key.startsWith(prefix)) {
                String dbKey = key.substring(prefix.length());
                dbProps.setProperty(dbKey, allProps.getProperty(key));
            }
        }

        if (dbProps.isEmpty()) {
            throw new IllegalStateException(
                    String.format("No '%s' properties found in Nacos config — dataId=%s, group=%s",
                            prefix, dataId, group));
        }
        log.info("DataSource properties extracted — {} keys: {}", dbProps.size(), dbProps.stringPropertyNames());

        // 5. 构建 DruidDataSource
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl(dbProps.getProperty("url"));
        ds.setUsername(dbProps.getProperty("username"));
        ds.setPassword(dbProps.getProperty("password"));

        String driverClassName = dbProps.getProperty("driver-class-name");
        if (driverClassName != null && !driverClassName.isEmpty()) {
            ds.setDriverClassName(driverClassName);
        }

        // 连接池参数
        setIntProperty(ds, dbProps, "initial-size");
        setIntProperty(ds, dbProps, "min-idle");
        setIntProperty(ds, dbProps, "max-active");
        setLongProperty(ds, dbProps, "max-wait");
        setLongProperty(ds, dbProps, "time-between-eviction-runs-millis");
        setLongProperty(ds, dbProps, "min-evictable-idle-time-millis");
        setStringProperty(ds, dbProps, "validation-query");
        setBoolProperty(ds, dbProps, "test-while-idle");
        setBoolProperty(ds, dbProps, "test-on-borrow");
        setBoolProperty(ds, dbProps, "test-on-return");
        setBoolProperty(ds, dbProps, "pool-prepared-statements");
        setIntProperty(ds, dbProps, "max-pool-prepared-statement-per-connection-size");
        setStringProperty(ds, dbProps, "filters");

        ds.init();
        log.info("DruidDataSource build success — url={}, initialSize={}, maxActive={}",
                ds.getUrl(), ds.getInitialSize(), ds.getMaxActive());

        return ds;
    }

    /**
     * 创建 Nacos ConfigService 连接。
     */
    private ConfigService createConfigService() throws NacosException {
        Properties nacosProps = new Properties();
        nacosProps.setProperty("serverAddr", serverAddr);
        if (namespace != null && !namespace.isEmpty()) {
            nacosProps.setProperty("namespace", namespace);
        }
        return com.alibaba.nacos.api.NacosFactory.createConfigService(nacosProps);
    }

    @Override
    public DataSource getObject() {
        return this.dataSource;
    }

    @Override
    public Class<?> getObjectType() {
        return DruidDataSource.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() throws Exception {
        if (this.dataSource != null) {
            log.info("DruidDataSource closing — url={}", this.dataSource.getUrl());
            this.dataSource.close();
        }
    }

    // ---- 属性设置工具方法 ----

    private void setIntProperty(DruidDataSource ds, Properties props, String key) {
        String value = props.getProperty(key);
        if (value != null && !value.isEmpty()) {
            try {
                switch (key) {
                    case "initial-size":
                        ds.setInitialSize(Integer.parseInt(value));
                        break;
                    case "min-idle":
                        ds.setMinIdle(Integer.parseInt(value));
                        break;
                    case "max-active":
                        ds.setMaxActive(Integer.parseInt(value));
                        break;
                    case "max-pool-prepared-statement-per-connection-size":
                        ds.setMaxPoolPreparedStatementPerConnectionSize(Integer.parseInt(value));
                        break;
                }
            } catch (NumberFormatException e) {
                log.warn("Invalid int value for key '{}': {}", key, value);
            }
        }
    }

    private void setLongProperty(DruidDataSource ds, Properties props, String key) {
        String value = props.getProperty(key);
        if (value != null && !value.isEmpty()) {
            try {
                switch (key) {
                    case "max-wait":
                        ds.setMaxWait(Long.parseLong(value));
                        break;
                    case "time-between-eviction-runs-millis":
                        ds.setTimeBetweenEvictionRunsMillis(Long.parseLong(value));
                        break;
                    case "min-evictable-idle-time-millis":
                        ds.setMinEvictableIdleTimeMillis(Long.parseLong(value));
                        break;
                }
            } catch (NumberFormatException e) {
                log.warn("Invalid long value for key '{}': {}", key, value);
            }
        }
    }

    private void setStringProperty(DruidDataSource ds, Properties props, String key) {
        String value = props.getProperty(key);
        if (value != null && !value.isEmpty()) {
            switch (key) {
                case "validation-query":
                    ds.setValidationQuery(value);
                    break;
                case "filters":
                    try {
                        ds.setFilters(value);
                    } catch (Exception e) {
                        log.warn("Failed to set Druid filters: {}", value, e);
                    }
                    break;
            }
        }
    }

    private void setBoolProperty(DruidDataSource ds, Properties props, String key) {
        String value = props.getProperty(key);
        if (value != null && !value.isEmpty()) {
            boolean boolVal = Boolean.parseBoolean(value);
            switch (key) {
                case "test-while-idle":
                    ds.setTestWhileIdle(boolVal);
                    break;
                case "test-on-borrow":
                    ds.setTestOnBorrow(boolVal);
                    break;
                case "test-on-return":
                    ds.setTestOnReturn(boolVal);
                    break;
                case "pool-prepared-statements":
                    ds.setPoolPreparedStatements(boolVal);
                    break;
            }
        }
    }
}
