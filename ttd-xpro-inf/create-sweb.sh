#!/usr/bin/env bash
set -euo pipefail

# 强制 UTF-8 编码，确保生成的文件中文不乱码
export LANG=en_US.UTF-8
export LC_ALL=en_US.UTF-8

# ============================================================================
# ttd-xpro-inf sweb 项目脚手架 (单体后端项目)
#
# 基于 Nacos 服务注册发现的 RPC 框架，生成单体 Spring Boot 后端项目。
# 不拆分 api/srv，服务接口与实现均在同一项目中。
#
# 用法:
#   ./create-sweb.sh <应用名称> <端口>
#
# 参数:
#   应用名称   不完整的应用名 (如 pay-dem)，脚本自动补 biz- 前缀
#              生成项目 biz-pay-dem-sweb
#   端口       Spring Boot Web 端口 (RPC 端口自动设为 端口 + 10000)
#
# 包路径规则:
#   默认前缀 com.ly.ttd + 工作空间名按 . 拼接 + .sweb
#   示例: 传入 pay-dem -> 工作空间 biz-pay-dem
#     -> 基础路径 biz.pay.dem
#     -> sweb 包: com.ly.ttd.biz.pay.dem.sweb
#
# 示例:
#   ./create-sweb.sh pay-dem 8080
#
# 前置条件:
#   1. 已安装 Maven 3.6+ 和 JDK 17+
#   2. 已在 ttd-xpro-inf 目录执行过 mvn install (安装 rpc-spring-boot-starter)
#   3. 本地或远程有可用的 Nacos 服务 (默认 127.0.0.1:8848)
# ============================================================================

# ---- 颜色输出 ----
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

info()  { echo -e "${GREEN}[INFO]${NC} $*"; }
warn()  { echo -e "${YELLOW}[WARN]${NC} $*"; }
error() { echo -e "${RED}[ERROR]${NC} $*" >&2; }

# ---- 帮助信息 ----
show_help() {
    cat <<'EOF'
ttd-xpro-inf sweb 项目脚手架 (单体后端项目)

用法:
  ./create-sweb.sh <应用名称> <端口>

参数:
  应用名称   不完整的应用名 (如 pay-dem)，脚本自动补 biz- 前缀
             生成项目 biz-pay-dem-sweb
  端口       Spring Boot Web 端口 (RPC 端口 = 端口 + 10000)

包路径规则:
  com.ly.ttd + 工作空间名按 . 拼接 + .sweb
  示例: 传入 pay-dem -> 工作空间 biz-pay-dem
    sweb 包: com.ly.ttd.biz.pay.dem.sweb

生成的目录结构:
  biz-pay-dem-sweb/                        # 项目目录 (parent: ttd-parent)
  ├── pom.xml
  └── src/main/java/.../sweb/
      ├── ServerMain.java                  # Spring Boot 启动类
      ├── config/
      │   ├── SwaggerConfig.java           # Swagger/OpenAPI 配置
      │   └── DataSourceConfig.java        # 多数据源配置 (ttd-daf-starter)
      ├── service/
      │   ├── DemoRpcService.java          # @RpcService 服务接口
      │   └── impl/
      │       └── DemoRpcServiceImpl.java  # 服务实现
      ├── controller/
      │   └── DemoController.java          # REST 接口 + @Rpcwired 示例
      ├── mapper/
      │   └── DemoMapper.java              # MyBatis Mapper 接口示例
      └── dal/
          └── DemoEntity.java              # 数据实体示例
      resources/
      ├── application.properties
      ├── META-INF/app.properties
      └── db/mybatis/mapper/               # MyBatis Mapper XML
          └── DemoMapper.xml

示例:
  ./create-sweb.sh pay-dem 8080
EOF
    exit 0
}

# ---- 参数解析 ----
if [[ "${1:-}" == "-h" || "${1:-}" == "--help" || $# -ne 2 ]]; then
    show_help
fi

INPUT_NAME="$1"
SERVER_PORT="$2"
RPC_PORT=$((SERVER_PORT + 10000))

# ---- 校验端口 ----
if ! [[ "$SERVER_PORT" =~ ^[0-9]+$ ]] || [ "$SERVER_PORT" -lt 1 ] || [ "$SERVER_PORT" -gt 65535 ]; then
    error "无效的端口号: ${SERVER_PORT} (应为 1-65535 的整数)"
    exit 1
fi

if [ "$RPC_PORT" -gt 65535 ]; then
    error "RPC 端口 ${RPC_PORT} 超出范围 (端口 + 10000 不能超过 65535)"
    exit 1
fi

# ---- 自动补 biz- 前缀，生成工作空间名 ----
# 传入 pay-dem -> 工作空间 biz-pay-dem
# 传入 biz-pay-dem -> 工作空间 biz-pay-dem (已有前缀不重复补)
if [[ "$INPUT_NAME" == biz-* ]]; then
    WORKSPACE_NAME="$INPUT_NAME"
else
    WORKSPACE_NAME="biz-${INPUT_NAME}"
fi

# 项目名 = 工作空间名 + -sweb
PROJECT_NAME="${WORKSPACE_NAME}-sweb"     # biz-pay-dem-sweb

# ---- 路径与常量 ----
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="${SCRIPT_DIR}/${PROJECT_NAME}"
FRAMEWORK_VERSION="1.0.0-SNAPSHOT"
SPRING_BOOT_VERSION="3.5.0"
JAVA_VERSION="17"

# ---- 包路径转换 ----
# biz-pay-dem -> biz.pay.dem  (横杠转点号)
BASE_NAME="${WORKSPACE_NAME}"               # biz-pay-dem
PKG_BASE="${BASE_NAME//-/.}"               # biz-pay-dem -> biz.pay.dem
PKG_PREFIX="com.ly.ttd"
SWEB_PKG="${PKG_PREFIX}.${PKG_BASE}.sweb"  # com.ly.ttd.biz.pay.dem.sweb
SWEB_PKG_DIR="${SWEB_PKG//.//}"            # com/ly/ttd/biz/pay/dem/sweb
SWEB_ARTIFACT="${PROJECT_NAME}"            # biz-pay-dem-sweb

echo ""
echo -e "${CYAN}============================================${NC}"
echo -e "${CYAN} ttd-xpro-inf sweb 项目脚手架 (单体后端)${NC}"
echo -e "${CYAN}============================================${NC}"
echo ""
info "传入名称:      ${INPUT_NAME}"
info "工作空间名:    ${WORKSPACE_NAME}"
info "项目名称:      ${PROJECT_NAME}"
info "Web 端口:      ${SERVER_PORT}"
info "RPC 端口:      ${RPC_PORT}"
info "基础名:        ${BASE_NAME}"
info "sweb 包路径:   ${SWEB_PKG}"
info "sweb artifact: ${SWEB_ARTIFACT}"
info "项目目录:      ${PROJECT_DIR}"
echo ""

# ---- 检查目录 ----
if [ -d "${PROJECT_DIR}" ]; then
    error "目录已存在: ${PROJECT_DIR}"
    exit 1
fi

# ============================================================================
# 创建目录结构
# ============================================================================
info "创建目录结构..."
SRC_DIR="${PROJECT_DIR}/src/main/java/${SWEB_PKG_DIR}"
TEST_DIR="${PROJECT_DIR}/src/test/java/${SWEB_PKG_DIR}"
RES_DIR="${PROJECT_DIR}/src/main/resources"
mkdir -p "${SRC_DIR}/config"
mkdir -p "${SRC_DIR}/service/impl"
mkdir -p "${SRC_DIR}/controller"
mkdir -p "${SRC_DIR}/mapper"
mkdir -p "${SRC_DIR}/dal"
mkdir -p "${TEST_DIR}"
mkdir -p "${RES_DIR}/META-INF"
mkdir -p "${RES_DIR}/db/mybatis/mapper"

# ============================================================================
# 生成 pom.xml
# ============================================================================
info "生成 pom.xml..."
cat > "${PROJECT_DIR}/pom.xml" <<POM_EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>${PKG_PREFIX}</groupId>
        <artifactId>ttd-parent</artifactId>
        <version>2026.0.0-SNAPSHOT</version>
        <relativePath/>
    </parent>

    <groupId>${PKG_PREFIX}</groupId>
    <artifactId>${SWEB_ARTIFACT}</artifactId>
    <version>${FRAMEWORK_VERSION}</version>
    <packaging>jar</packaging>
    <name>${SWEB_ARTIFACT}</name>
    <description>基于 ttd-xpro-inf Nacos RPC 框架的 sweb 后端服务</description>

    <properties>
        <java.version>${JAVA_VERSION}</java.version>
        <maven.compiler.source>${JAVA_VERSION}</maven.compiler.source>
        <maven.compiler.target>${JAVA_VERSION}</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <rpc-framework.version>${FRAMEWORK_VERSION}</rpc-framework.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Web (排除 Tomcat, 使用 Undertow) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-tomcat</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-undertow</artifactId>
        </dependency>

        <!-- ttd-xpro-inf Nacos RPC 框架 (含 rpc-api 注解 + rpc-core 实现) -->
        <dependency>
            <groupId>${PKG_PREFIX}</groupId>
            <artifactId>rpc-spring-boot-starter</artifactId>
            <version>\${rpc-framework.version}</version>
        </dependency>

        <!-- ttd-nacos-config 远程配置加载 (EnvironmentPostProcessor + NacosConfigLoader) -->
        <dependency>
            <groupId>${PKG_PREFIX}</groupId>
            <artifactId>ttd-nacos-config</artifactId>
            <version>2026.0.0-SNAPSHOT</version>
        </dependency>

        <!-- ttd-daf-starter 多数据源框架 (DataSourceFactoryBean + MyBatis + JdbcTemplate) -->
        <dependency>
            <groupId>${PKG_PREFIX}</groupId>
            <artifactId>ttd-daf-starter</artifactId>
            <version>2026.0.0-SNAPSHOT</version>
        </dependency>

        <!-- Springdoc OpenAPI (Swagger UI) -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.8.7</version>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- 测试 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
POM_EOF

# ============================================================================
# 生成 ServerMain.java (启动类)
# ============================================================================
info "生成 ServerMain.java (启动类)..."
cat > "${SRC_DIR}/ServerMain.java" <<JAVA_EOF
package ${SWEB_PKG};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
/**
 * ${PROJECT_NAME} 服务启动类。
 * <p>
 * 启动后自动:
 * <ul>
 *   <li>启动嵌入式 Undertow RPC 服务器 (端口 ${RPC_PORT})</li>
 *   <li>将标注 {@code @RpcService} 的服务实现注册到 Nacos</li>
 *   <li>为标注 {@code @Rpcwired} 的字段注入远程服务代理</li>
 * </ul>
 */
@SpringBootApplication
@Slf4j
public class ServerMain {

    public static void main(String[] args) {
         long start = System.currentTimeMillis();
         ConfigurableApplicationContext ctx = SpringApplication.run(ServerMain.class, args);
         log.info("${PROJECT_NAME} start success, cost={}ms", (System.currentTimeMillis()-start));

    }
}
JAVA_EOF

# ============================================================================
# 生成 SwaggerConfig.java (OpenAPI 配置)
# ============================================================================
info "生成 SwaggerConfig.java (Swagger/OpenAPI 配置)..."
cat > "${SRC_DIR}/config/SwaggerConfig.java" <<SWAGGER_EOF
package ${SWEB_PKG}.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 配置。
 * <p>
 * 启动后访问:
 * <ul>
 *   <li>Swagger UI: http://localhost:${SERVER_PORT}/swagger-ui.html</li>
 *   <li>OpenAPI JSON: http://localhost:${SERVER_PORT}/v3/api-docs</li>
 * </ul>
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("${PROJECT_NAME} API")
                        .version("1.0.0")
                        .description("${PROJECT_NAME} 后端服务接口文档")
                        .contact(new Contact().name("dev").email("dev@example.com")));
    }
}
SWAGGER_EOF

# ============================================================================
# 生成 DataSourceConfig.java (多数据源配置)
# ============================================================================
info "生成 DataSourceConfig.java (多数据源配置)..."
cat > "${SRC_DIR}/config/DataSourceConfig.java" <<DS_EOF
package ${SWEB_PKG}.config;

import com.ly.ttd.inf.daf.DataSourceFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

/**
 * 多数据源配置 — 基于 ttd-daf-starter 的 DataSourceFactoryBean。
 * <p>
 * 通过 Nacos 配置中心拉取从库连接信息，构建 DruidDataSource，
 * 并配置 MyBatis SqlSessionFactory、MapperScanner 和 JdbcTemplate。
 * <p>
 * 启用条件: {@code ttd.daf.datasource.enable=true}
 */
@Configuration
@ConditionalOnProperty(name = "ttd.daf.datasource.enable", havingValue = "true")
public class DataSourceConfig {

    @Value("\${ttd.nacos.server-addr:127.0.0.1:8848}")
    private String serverAddr;

    @Value("\${ttd.nacos.namespace:}")
    private String namespace;

    /**
     * 从库 DataSource — 通过 DataSourceFactoryBean 从 Nacos 拉取配置构建 DruidDataSource。
     */
    @Bean("RCS.Slave.DataSource")
    public DataSourceFactoryBean slaveDataSource() {
        return new DataSourceFactoryBean(serverAddr, namespace,
                "db-slave-rcs.properties", "DEFAULT_GROUP");
    }

    /**
     * MyBatis SqlSessionFactory — 绑定从库 DataSource。
     */
    @Bean("RCS.Slave.SqlSessionFactory")
    @ConditionalOnBean(name = "RCS.Slave.DataSource")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("RCS.Slave.DataSource") DataSource dataSource) throws Exception {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(resolver.getResources(
                "classpath*:db/mybatis/mapper/*Mapper.xml"));
        return factory.getObject();
    }

    /**
     * Mapper 扫描器 — 扫描 ${SWEB_PKG}.mapper 包下的 Mapper 接口。
     */
    @Bean("RCS.Slave.MapperScanner")
    @ConditionalOnBean(name = "RCS.Slave.SqlSessionFactory")
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer scanner = new MapperScannerConfigurer();
        scanner.setSqlSessionFactoryBeanName("RCS.Slave.SqlSessionFactory");
        scanner.setBasePackage("${SWEB_PKG}.mapper");
        return scanner;
    }

    /**
     * JdbcTemplate — 基于从库 DataSource 的 NamedParameterJdbcTemplate。
     */
    @Bean("RCS.Slave.JdbcTemplate")
    @ConditionalOnBean(name = "RCS.Slave.DataSource")
    public NamedParameterJdbcTemplate jdbcTemplate(@Qualifier("RCS.Slave.DataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
DS_EOF

# ============================================================================
# 生成 DemoMapper.java (MyBatis Mapper 接口示例)
# ============================================================================
info "生成 DemoMapper.java (MyBatis Mapper 接口)..."
cat > "${SRC_DIR}/mapper/DemoMapper.java" <<MAPPER_EOF
package ${SWEB_PKG}.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * 示例 Mapper 接口。
 * <p>
 * 由 DataSourceConfig 中的 MapperScannerConfigurer 自动扫描注册。
 * 对应的 XML 映射文件: {@code resources/db/mybatis/mapper/DemoMapper.xml}
 */
public interface DemoMapper {

    /**
     * 根据 ID 查询名称。
     *
     * @param id 主键 ID
     * @return 名称
     */
    String selectNameById(@Param("id") Long id);
}
MAPPER_EOF

# ============================================================================
# 生成 DemoMapper.xml (MyBatis XML 映射示例)
# ============================================================================
info "生成 DemoMapper.xml (MyBatis XML 映射)..."
cat > "${RES_DIR}/db/mybatis/mapper/DemoMapper.xml" <<XML_EOF
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${SWEB_PKG}.mapper.DemoMapper">

    <select id="selectNameById" resultType="java.lang.String">
        SELECT name FROM demo_table WHERE id = #{id}
    </select>

</mapper>
XML_EOF

# ============================================================================
# 生成 DemoRpcService.java (@RpcService 服务接口)
# ============================================================================
info "生成 DemoRpcService.java (@RpcService 服务接口)..."
cat > "${SRC_DIR}/service/DemoRpcService.java" <<JAVA_EOF
package ${SWEB_PKG}.service;

import com.ly.ttd.inf.rpc.api.annotation.RpcService;

/**
 * 示例 RPC 服务接口。
 * <p>
 * 标注 {@code @RpcService} 后，框架会:
 * <ol>
 *   <li>将该接口的实现类暴露为 RPC 服务</li>
 *   <li>将服务实例注册到 Nacos (serviceName = "${PROJECT_NAME}")</li>
 *   <li>其他微服务可通过 {@code @Rpcwired} 远程调用</li>
 * </ol>
 */
@RpcService(serviceName = "${PROJECT_NAME}")
public interface DemoRpcService {

    /**
     * 示例 RPC 方法: 拼接问候语。
     *
     * @param name 名称
     * @return 问候信息
     */
    String sayHello(String name);
}
JAVA_EOF

# ============================================================================
# 生成 DemoRpcServiceImpl.java (服务实现)
# ============================================================================
info "生成 DemoRpcServiceImpl.java (服务实现)..."
cat > "${SRC_DIR}/service/impl/DemoRpcServiceImpl.java" <<JAVA_EOF
package ${SWEB_PKG}.service.impl;

import ${SWEB_PKG}.service.DemoRpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * {@link DemoRpcService} 的实现类。
 * <p>
 * 注册为 Spring Bean 后，框架的 BeanPostProcessor 会自动收集
 * 并暴露给 RPC 服务端，注册到 Nacos。
 */
@Slf4j
@Service
public class DemoRpcServiceImpl implements DemoRpcService {

    @Override
    public String sayHello(String name) {
        log.info("RPC call received: sayHello({})", name);
        return "Hello, " + name + "! From ${PROJECT_NAME}";
    }
}
JAVA_EOF

# ============================================================================
# 生成 DemoController.java (REST 接口 + @Rpcwired 示例)
# ============================================================================
info "生成 DemoController.java (REST 接口 + @Rpcwired 示例)..."
cat > "${SRC_DIR}/controller/DemoController.java" <<JAVA_EOF
package ${SWEB_PKG}.controller;

import ${SWEB_PKG}.service.DemoRpcService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 示例 REST 控制器。
 * <p>
 * 演示两种调用方式:
 * <ul>
 *   <li>本地调用: 直接注入 {@link DemoRpcService} 实现类</li>
 *   <li>远程调用: 使用 {@code @Rpcwired} 注入远程 RPC 代理
 *       (调用其他微服务的接口)</li>
 * </ul>
 */
@Slf4j
@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
@Tag(name = "Demo", description = "示例 REST 接口")
public class DemoController {

    private final DemoRpcService demoRpcService;

    /**
     * 本地调用示例。
     * <p>
     * GET /api/demo/hello/\{name\}
     *
     * @param name 名称
     * @return 问候信息
     */
    @GetMapping("/hello/{name}")
    @Operation(summary = "问候接口", description = "本地调用 DemoRpcService.sayHello")
    public String hello(@Parameter(description = "名称") @PathVariable String name) {
        log.info("Local call: hello({})", name);
        return demoRpcService.sayHello(name);
    }

    /*
     * ======================== 远程调用示例 ========================
     *
     * 如需调用其他微服务，定义远程服务接口并标注 @RpcService，
     * 然后使用 @Rpcwired 注入代理:
     *
     * @Rpcwired(serviceName = "other-service-name", timeout = 3000)
     * private OtherRpcService otherRpcService;
     *
     * @GetMapping("/remote/{name}")
     * public String remote(@PathVariable String name) {
     *     return otherRpcService.someMethod(name);
     * }
     *
     * 注意: @Rpcwired 注入的是远程代理，每次调用都会通过 Nacos
     * 发现目标实例并发起 HTTP RPC 请求。
     * ============================================================
     */
}
JAVA_EOF

# ============================================================================
# 生成 application.properties
# ============================================================================
info "生成 application.properties..."
cat > "${RES_DIR}/application.properties" <<PROPS_EOF
# ====================================================================
# ${PROJECT_NAME} 服务配置
# ====================================================================

# ---- Spring Boot Web 端口 ----
server.port=${SERVER_PORT}

# ---- Nacos RPC 框架配置 (nacos.rpc.*) ----
# 是否启用 Nacos RPC 框架
nacos.rpc.enabled=true

# Nacos 注册中心地址
nacos.rpc.server-addr=127.0.0.1:8848
# Nacos 命名空间 (留空为公共命名空间)
nacos.rpc.namespace=
# Nacos 分组
nacos.rpc.group=DEFAULT_GROUP
# Nacos 用户名/密码 (如需认证)
nacos.rpc.username=
nacos.rpc.password=

# ---- RPC 服务端配置 ----
# RPC 服务器监听主机
nacos.rpc.server-host=0.0.0.0
# RPC 服务器端口 (Undertow, 与 Web 端口不同)
nacos.rpc.server-port=${RPC_PORT}
# RPC 请求路径
nacos.rpc.rpc-path=/nacos-rpc/invoke

# ---- 应用信息 ----
nacos.rpc.app-name=${PROJECT_NAME}
nacos.rpc.app-version=1.0.0
# 环境: dev / inte / prod / rc / stable
nacos.rpc.env=dev

# ---- Undertow 线程配置 ----
nacos.rpc.io-threads=2
nacos.rpc.worker-threads=16
nacos.rpc.direct-buffers=true

# ---- OkHttp 客户端超时 (毫秒) ----
nacos.rpc.connect-timeout=5000
nacos.rpc.read-timeout=10000
nacos.rpc.write-timeout=10000

# ---- Nacos 配置中心 (ttd-nacos-config NacosConfigLoader) ----
ttd.nacos.server-addr=127.0.0.1:8848
ttd.nacos.namespace=

# Nacos 远程配置加载 (NacosConfigLoader EnvironmentPostProcessor)
# 支持配置多条, 索引从 0 开始, 启动时自动拉取并注入 Environment
ttd.nacos[0].namespace=
ttd.nacos[0].group=DEFAULT_GROUP
ttd.nacos[0].dataId=${PROJECT_NAME}.properties

# ---- 多数据源配置 (ttd-daf-starter) ----
# 启用从库数据源 (DataSourceConfig)
ttd.daf.datasource.enable=true

# ---- Swagger/OpenAPI 配置 ----
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true

# ---- 日志级别 ----
logging.level.com.ly.ttd.inf.rpc=INFO
logging.level.root=INFO
PROPS_EOF

# ============================================================================
# 生成 META-INF/app.properties
# ============================================================================
info "生成 META-INF/app.properties..."
cat > "${RES_DIR}/META-INF/app.properties" <<APP_EOF
# 应用元数据 - 由 AppUtils 加载，用于服务注册时填充默认信息
app.name=${PROJECT_NAME}
app.version=1.0.0
APP_EOF

# ============================================================================
# 生成 ServerMainTest.java
# ============================================================================
info "生成 ServerMainTest.java..."
cat > "${TEST_DIR}/ServerMainTest.java" <<TEST_EOF
package ${SWEB_PKG};

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 应用上下文加载测试。
 */
@SpringBootTest
class ServerMainTest {

    @Test
    void contextLoads() {
        // 验证 Spring 上下文能正常加载
    }
}
TEST_EOF

# ============================================================================
# 完成
# ============================================================================
echo ""
echo -e "${GREEN}============================================${NC}"
echo -e "${GREEN} sweb 项目生成成功!${NC}"
echo -e "${GREEN}============================================${NC}"
echo ""
echo -e "${CYAN}目录结构:${NC}"
echo ""
if command -v tree &> /dev/null; then
    tree "${PROJECT_DIR}" --dirsfirst -I 'target'
else
    find "${PROJECT_DIR}" -type f | sort | sed "s|${PROJECT_DIR}/|  |"
fi
echo ""
echo -e "${CYAN}项目说明:${NC}"
echo ""
echo -e "  ${YELLOW}${SWEB_ARTIFACT}${NC} (单体后端项目)"
echo -e "    包路径: ${SWEB_PKG}"
echo -e "    职责:   集成 RPC + 多数据源 (ttd-daf-starter) + Swagger UI"
echo -e "    打包:   mvn clean package"
echo ""
echo -e "${CYAN}下一步操作:${NC}"
echo ""
echo -e "  1. 安装 rpc-spring-boot-starter 到本地 Maven 仓库:"
echo -e "     ${YELLOW}cd ${SCRIPT_DIR} && mvn clean install -DskipTests${NC}"
echo ""
echo -e "  2. 启动 Nacos 服务 (默认 127.0.0.1:8848)"
echo ""
echo -e "  3. 启动服务:"
echo -e "     ${YELLOW}cd ${PROJECT_DIR} && mvn spring-boot:run${NC}"
echo ""
echo -e "  4. 测试本地调用:"
echo -e "     ${YELLOW}curl http://localhost:${SERVER_PORT}/api/demo/hello/world${NC}"
echo ""
echo -e "  5. 访问 Swagger UI:"
echo -e "     ${YELLOW}http://localhost:${SERVER_PORT}/swagger-ui.html${NC}"
echo ""
echo -e "  6. 其他服务远程调用本服务:"
echo -e "     定义相同接口标注 @RpcService 后使用 @Rpcwired:"
echo -e "     ${YELLOW}@RpcService(serviceName = \"${PROJECT_NAME}\")${NC}"
echo -e "     ${YELLOW}public interface DemoRpcService { ... }${NC}"
echo -e "     ${YELLOW}@Rpcwired(serviceName = \"${PROJECT_NAME}\")${NC}"
echo -e "     ${YELLOW}private DemoRpcService demoRpcService;${NC}"
echo ""
echo -e "${CYAN}配置说明:${NC}"
echo -e "  Web 端口 (server.port):          ${SERVER_PORT}"
echo -e "  RPC 端口 (nacos.rpc.server-port): ${RPC_PORT}"
echo -e "  Nacos 地址:                       127.0.0.1:8848"
echo -e "  修改配置: ${PROJECT_DIR}/src/main/resources/application.properties"
echo ""
