package com.ly.ttd.daf.starter;

/**
 * mysql 支付从库数据源自动配置 (支持 MyBatis + JdbcTemplate)
 *
 * @author yong.li
 * @since 2025/7/3 15:42
 */

import javax.sql.DataSource;

import com.ly.ttd.inf.daf.DataSourceFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
public class MysqlRcsSlaveDataSourceAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MysqlRcsSlaveDataSourceAutoConfiguration.class);

    @Value("${ttd.nacos.server-addr:127.0.0.1:8848}")
    private String serverAddr;

    @Value("${ttd.nacos.namespace:}")
    private String namespace;

    /**
     * 从库 DataSource — 通过 DataSourceFactoryBean 从 Nacos 拉取配置构建 DruidDataSource。
     * <p>
     * 返回 FactoryBean，由 Spring 容器管理 afterPropertiesSet() / getObject() / destroy() 生命周期。
     */
    @ConditionalOnProperty(name = "ttd.load.slave.rcs.enable", havingValue = "true")
    @Bean("RCS.Slave.DataSource")
    public DataSourceFactoryBean slaveDataSource() {
        logger.info("====== load RCS Slave DataSource ======");
        return new DataSourceFactoryBean(serverAddr, namespace, "db-slave-rcs.properties", "DEFAULT_GROUP");
    }

    /**
     * MyBatis 配置 — SqlSessionFactory + MapperScanner。
     * <p>
     * 依赖 RCS.Slave.DataSource，仅当 DataSource bean 存在时才激活。
     */
    @ConditionalOnProperty(name = "ttd.load.slave.rcs.mybatis.enable", havingValue = "true")
    @ConditionalOnBean(name = "RCS.Slave.DataSource")
    @Configuration
    static class MybatisAutoConfiguration {

        public MybatisAutoConfiguration() {
            logger.info("====== load RCS Slave Mybatis  ======");
        }

        @Bean("RCS.Slave.SqlSessionFactory")
        @ConditionalOnClass(SqlSessionFactoryBean.class)
        public SqlSessionFactory initSqlSessionFactoryBean(@Qualifier("RCS.Slave.DataSource") DataSource dataSource) throws Exception {
            ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
            SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
            factory.setDataSource(dataSource);
            factory.setConfigLocation(resourceResolver.getResource("classpath:mybatis-config.xml"));
            factory.setMapperLocations(resourceResolver.getResources("classpath*:db/mybatis/rcs/slave/mapper/*Mapper.xml"));
            return factory.getObject();
        }

        @Bean("RCS.Slave.MapperScanner")
        @ConditionalOnBean(name = "RCS.Slave.SqlSessionFactory")
        public MapperScannerConfigurer mapperScannerConfigurer() {
            MapperScannerConfigurer scannerConfigurer = new MapperScannerConfigurer();
            scannerConfigurer.setSqlSessionFactoryBeanName("RCS.Slave.SqlSessionFactory");
            scannerConfigurer.setBasePackage("com.ly.ttd.biz.**.rcs.slave.mapper");
            return scannerConfigurer;
        }
    }

    /**
     * JdbcTemplate — 基于从库 DataSource 的 NamedParameterJdbcTemplate。
     */
    @Bean("RCS.Slave.JdbcTemplate")
    @ConditionalOnProperty(name = "ttd.load.slave.rcs.jdbc.enable", havingValue = "true")
    @ConditionalOnBean(name = "RCS.Slave.DataSource")
    public NamedParameterJdbcTemplate initNamedParameterJdbcTemplate(@Qualifier("RCS.Slave.DataSource") DataSource dataSource) {
        logger.info("====== load RCS Slave NamedParameterJdbcTemplate ======");
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
