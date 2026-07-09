#!/usr/bin/env bash
set -euo pipefail

# ============================================================================
# ttd-xpro-inf 应用脚手架 (api + srv 双项目)
#
# 基于 Nacos 服务注册发现的 RPC 框架，生成两个独立的 Maven 项目:
#   - api 项目: 定义 @RpcService 服务接口 (供 srv 实现和其他服务消费)
#   - srv 项目: 依赖 api，实现服务逻辑，启动 Spring Boot + RPC 服务端
#
# 用法:
#   ./create-app.sh <应用名称> <端口>
#
# 参数:
#   应用名称   不完整的应用名 (如 pay-dem)，脚本自动补 biz- 前缀
#              生成工作空间 biz-pay-dem，下挂 biz-pay-dem-api 和 biz-pay-dem-srv
#   端口       Spring Boot Web 端口 (RPC 端口自动设为 端口 + 10000)
#
# 包路径规则:
#   默认前缀 com.ly.ttd + 工作空间名按 . 拼接
#   示例: 传入 pay-dem -> 工作空间 biz-pay-dem
#     -> 基础路径 biz.pay.dem
#     -> api 包: com.ly.ttd.biz.pay.dem.api
#     -> srv 包: com.ly.ttd.biz.pay.dem.srv
#
# 示例:
#   ./create-app.sh pay-dem 8080
#
# 前置条件:
#   1. 已安装 Maven 3.6+ 和 JDK 17+
#   2. 已在 ttd-xpro-inf 目录执行过 mvn install (安装 rpc-api / rpc-spring-boot-starter)
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
ttd-xpro-inf 应用脚手架 (api + srv 双项目)

用法:
  ./create-app.sh <应用名称> <端口>

参数:
  应用名称   不完整的应用名 (如 pay-dem)，脚本自动补 biz- 前缀
             生成工作空间 biz-pay-dem，下挂 biz-pay-dem-api 和 biz-pay-dem-srv
  端口       Spring Boot Web 端口 (RPC 端口 = 端口 + 10000)

包路径规则:
  com.ly.ttd + 工作空间名按 . 拼接
  示例: 传入 pay-dem -> 工作空间 biz-pay-dem
    api 包: com.ly.ttd.biz.pay.dem.api
    srv 包: com.ly.ttd.biz.pay.dem.srv

生成的目录结构:
  biz-pay-dem/                              # 工作空间 (Maven 聚合工程)
  ├── pom.xml                              # 聚合 pom (parent: ttd-parent)
  ├── biz-pay-dem-api/                     # API 子模块
  │   ├── pom.xml
  │   └── src/main/java/.../api/
  │       └── DemoRpcService.java          # @RpcService 服务接口
  └── biz-pay-dem-srv/                     # SRV 子模块
      ├── pom.xml
      └── src/main/java/.../srv/
          ├── ServerMain.java              # Spring Boot 启动类
          ├── service/impl/
          │   └── DemoRpcServiceImpl.java  # 服务实现
          └── controller/
              └── DemoController.java      # REST 接口 + @Rpcwired 示例
          (resources/application.properties, META-INF/app.properties)

示例:
  ./create-app.sh pay-dem 8080
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

# 注册到 Nacos 的服务名 = 工作空间名 + -srv
SERVICE_NAME="${WORKSPACE_NAME}-srv"     # biz-pay-dem-srv

# ---- 路径与常量 ----
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
WORKSPACE_DIR="${SCRIPT_DIR}/${WORKSPACE_NAME}"
FRAMEWORK_VERSION="1.0.0-SNAPSHOT"
SPRING_BOOT_VERSION="3.5.0"
JAVA_VERSION="17"

# ---- 包路径转换 (遵循旧逻辑) ----
# biz-pay-dem -> biz.pay.dem  (横杠转点号)
BASE_NAME="${WORKSPACE_NAME}"            # biz-pay-dem
PKG_BASE="${BASE_NAME//-/.}"            # biz-pay-dem -> biz.pay.dem
PKG_PREFIX="com.ly.ttd"
API_PKG="${PKG_PREFIX}.${PKG_BASE}.api" # com.ly.ttd.biz.pay.dem.api
SRV_PKG="${PKG_PREFIX}.${PKG_BASE}.srv" # com.ly.ttd.biz.pay.dem.srv
API_PKG_DIR="${API_PKG//.//}"           # com/ly/ttd/biz/pay/dem/api
SRV_PKG_DIR="${SRV_PKG//.//}"           # com/ly/ttd/biz/pay/dem/srv
API_ARTIFACT="${WORKSPACE_NAME}-api"    # biz-pay-dem-api
SRV_ARTIFACT="${SERVICE_NAME}"          # biz-pay-dem-srv

echo ""
echo -e "${CYAN}============================================${NC}"
echo -e "${CYAN} ttd-xpro-inf 应用脚手架 (api + srv 双项目)${NC}"
echo -e "${CYAN}============================================${NC}"
echo ""
info "传入名称:      ${INPUT_NAME}"
info "工作空间名:    ${WORKSPACE_NAME}"
info "服务名称:      ${SERVICE_NAME}"
info "Web 端口:      ${SERVER_PORT}"
info "RPC 端口:      ${RPC_PORT}"
info "基础名:        ${BASE_NAME}"
info "API 包路径:    ${API_PKG}"
info "SRV 包路径:    ${SRV_PKG}"
info "API artifact:  ${API_ARTIFACT}"
info "SRV artifact:  ${SRV_ARTIFACT}"
info "工作空间:      ${WORKSPACE_DIR}"
echo ""

# ---- 检查目录 ----
if [ -d "${WORKSPACE_DIR}" ]; then
    error "目录已存在: ${WORKSPACE_DIR}"
    exit 1
fi

# ============================================================================
# 创建目录结构
# ============================================================================
info "创建目录结构..."

# --- API 项目目录 ---
API_SRC_DIR="${WORKSPACE_DIR}/${API_ARTIFACT}/src/main/java/${API_PKG_DIR}"
mkdir -p "${API_SRC_DIR}"

# --- SRV 项目目录 ---
SRV_SRC_DIR="${WORKSPACE_DIR}/${SRV_ARTIFACT}/src/main/java/${SRV_PKG_DIR}"
SRV_TEST_DIR="${WORKSPACE_DIR}/${SRV_ARTIFACT}/src/test/java/${SRV_PKG_DIR}"
SRV_RES_DIR="${WORKSPACE_DIR}/${SRV_ARTIFACT}/src/main/resources"
mkdir -p "${SRV_SRC_DIR}/service/impl"
mkdir -p "${SRV_SRC_DIR}/controller"
mkdir -p "${SRV_TEST_DIR}"
mkdir -p "${SRV_RES_DIR}/META-INF"

# ============================================================================
# 生成工作空间聚合 pom.xml
# ============================================================================
info "生成工作空间聚合 pom.xml..."
cat > "${WORKSPACE_DIR}/pom.xml" <<POM_EOF
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
    <artifactId>${WORKSPACE_NAME}</artifactId>
    <version>${FRAMEWORK_VERSION}</version>
    <packaging>pom</packaging>
    <name>${WORKSPACE_NAME}</name>
    <description>${SERVICE_NAME} 聚合工程</description>

    <modules>
        <module>${API_ARTIFACT}</module>
        <module>${SRV_ARTIFACT}</module>
    </modules>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>${PKG_PREFIX}</groupId>
                <artifactId>${API_ARTIFACT}</artifactId>
                <version>\${project.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
POM_EOF

# ============================================================================
# 生成 API 项目 pom.xml
# ============================================================================
info "生成 API 项目 pom.xml..."
cat > "${WORKSPACE_DIR}/${API_ARTIFACT}/pom.xml" <<POM_EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.ly.ttd</groupId>
        <artifactId>${WORKSPACE_NAME}</artifactId>
        <version>${FRAMEWORK_VERSION}</version>
        <relativePath/>
    </parent>

    <groupId>${PKG_PREFIX}</groupId>
    <artifactId>${API_ARTIFACT}</artifactId>
    <version>${FRAMEWORK_VERSION}</version>
    <packaging>jar</packaging>
    <name>${API_ARTIFACT}</name>
    <description>${SERVICE_NAME} RPC 服务接口定义</description>

    <properties>
        <maven.compiler.source>${JAVA_VERSION}</maven.compiler.source>
        <maven.compiler.target>${JAVA_VERSION}</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <rpc-framework.version>${FRAMEWORK_VERSION}</rpc-framework.version>
    </properties>

    <dependencies>
        <!-- ttd-xpro-inf RPC 注解 (仅依赖 rpc-api) -->
        <dependency>
            <groupId>${PKG_PREFIX}</groupId>
            <artifactId>rpc-api</artifactId>
       <!--      <version>\${rpc-framework.version}</version> -->
        </dependency>
    </dependencies>
</project>
POM_EOF

# ============================================================================
# 生成 API 服务接口 DemoRpcService.java
# ============================================================================
info "生成 DemoRpcService.java (@RpcService 服务接口)..."
cat > "${API_SRC_DIR}/DemoRpcService.java" <<JAVA_EOF
package ${API_PKG};

import com.ly.ttd.inf.rpc.api.annotation.RpcService;

/**
 * 示例 RPC 服务接口。
 * <p>
 * 标注 {@code @RpcService} 后:
 * <ol>
 *   <li>srv 模块实现该接口并注册为 Spring Bean，框架自动暴露 RPC 服务</li>
 *   <li>服务实例注册到 Nacos (serviceName = "${SERVICE_NAME}")</li>
 *   <li>其他微服务依赖本 api 包后，可通过 {@code @Rpcwired} 远程调用</li>
 * </ol>
 */
@RpcService(serviceName = "${SERVICE_NAME}")
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
# 生成 SRV 项目 pom.xml
# ============================================================================
info "生成 SRV 项目 pom.xml..."
cat > "${WORKSPACE_DIR}/${SRV_ARTIFACT}/pom.xml" <<POM_EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.ly.ttd</groupId>
        <artifactId>${WORKSPACE_NAME}</artifactId>
        <version>${FRAMEWORK_VERSION}</version>
        <relativePath/>
    </parent>

    <groupId>${PKG_PREFIX}</groupId>
    <artifactId>${SRV_ARTIFACT}</artifactId>
    <version>${FRAMEWORK_VERSION}</version>
    <packaging>jar</packaging>
    <name>${SRV_ARTIFACT}</name>
    <description>${SERVICE_NAME} 服务实现</description>

    <properties>
        <java.version>${JAVA_VERSION}</java.version>
        <maven.compiler.source>${JAVA_VERSION}</maven.compiler.source>
        <maven.compiler.target>${JAVA_VERSION}</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <rpc-framework.version>${FRAMEWORK_VERSION}</rpc-framework.version>
    </properties>

    <dependencies>

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

        <!-- 本项目 API 接口定义 -->
        <dependency>
            <groupId>${PKG_PREFIX}</groupId>
            <artifactId>${API_ARTIFACT}</artifactId>
            <version>\${project.version}</version>
        </dependency>

        <!-- ttd-xpro-inf Nacos RPC 框架 -->
        <dependency>
            <groupId>${PKG_PREFIX}</groupId>
            <artifactId>rpc-spring-boot-starter</artifactId>
            <version>\${rpc-framework.version}</version>
        </dependency>

        <!-- Spring Boot Web (REST 接口) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
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
# 生成 SRV ServerMain.java (启动类)
# ============================================================================
info "生成 ServerMain.java (启动类)..."
cat > "${SRV_SRC_DIR}/ServerMain.java" <<JAVA_EOF
package ${SRV_PKG};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
/**
 * ${SERVICE_NAME} 服务启动类。
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
         log.info("${SERVICE_NAME} start success, cost={}ms", (System.currentTimeMillis()-start));

    }
}
JAVA_EOF

# ============================================================================
# 生成 SRV DemoRpcServiceImpl.java (服务实现)
# ============================================================================
info "生成 DemoRpcServiceImpl.java (服务实现)..."
cat > "${SRV_SRC_DIR}/service/impl/DemoRpcServiceImpl.java" <<JAVA_EOF
package ${SRV_PKG}.service.impl;

import ${API_PKG}.DemoRpcService;
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
        return "Hello, " + name + "! From ${SERVICE_NAME}";
    }
}
JAVA_EOF

# ============================================================================
# 生成 SRV DemoController.java (REST 接口 + @Rpcwired 示例)
# ============================================================================
info "生成 DemoController.java (REST 接口 + @Rpcwired 示例)..."
cat > "${SRV_SRC_DIR}/controller/DemoController.java" <<JAVA_EOF
package ${SRV_PKG}.controller;

import ${API_PKG}.DemoRpcService;
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
    public String hello(@PathVariable String name) {
        log.info("Local call: hello({})", name);
        return demoRpcService.sayHello(name);
    }

    /*
     * ======================== 远程调用示例 ========================
     *
     * 如需调用其他微服务，在 api 包中定义远程服务接口并标注 @RpcService，
     * 然后在 srv 中使用 @Rpcwired 注入代理:
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
# 生成 SRV application.properties
# ============================================================================
info "生成 application.properties..."
cat > "${SRV_RES_DIR}/application.properties" <<PROPS_EOF
# ====================================================================
# ${SERVICE_NAME} 服务配置
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
nacos.rpc.app-name=${SERVICE_NAME}
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

# ---- 日志级别 ----
logging.level.com.ly.ttd.inf.rpc=INFO
logging.level.root=INFO
PROPS_EOF

# ============================================================================
# 生成 SRV META-INF/app.properties
# ============================================================================
info "生成 META-INF/app.properties..."
cat > "${SRV_RES_DIR}/META-INF/app.properties" <<APP_EOF
# 应用元数据 - 由 AppUtils 加载，用于服务注册时填充默认信息
app.name=${SERVICE_NAME}
app.version=1.0.0
APP_EOF

# ============================================================================
# 生成 SRV ServerMainTest.java
# ============================================================================
info "生成 ServerMainTest.java..."
cat > "${SRV_TEST_DIR}/ServerMainTest.java" <<TEST_EOF
package ${SRV_PKG};

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
echo -e "${GREEN} 应用骨架生成成功! (api + srv 双项目)${NC}"
echo -e "${GREEN}============================================${NC}"
echo ""
echo -e "${CYAN}目录结构:${NC}"
echo ""
if command -v tree &> /dev/null; then
    tree "${WORKSPACE_DIR}" --dirsfirst -I 'target'
else
    find "${WORKSPACE_DIR}" -type f | sort | sed "s|${WORKSPACE_DIR}/|  |"
fi
echo ""
echo -e "${CYAN}项目说明:${NC}"
echo ""
echo -e "  ${YELLOW}聚合工程${NC} (${WORKSPACE_NAME})"
echo -e "    类型:   Maven 聚合项目 (packaging=pom, parent=ttd-parent)"
echo -e "    子模块: ${API_ARTIFACT}, ${SRV_ARTIFACT}"
echo -e ""
echo -e "  ${YELLOW}api 子模块${NC} (${API_ARTIFACT})"
echo -e "    包路径: ${API_PKG}"
echo -e "    职责:   定义 @RpcService 服务接口，供 srv 实现和其他服务消费"
echo -e ""
echo -e "  ${YELLOW}srv 子模块${NC} (${SRV_ARTIFACT})"
echo -e "    包路径: ${SRV_PKG}"
echo -e "    职责:   依赖 api，实现服务逻辑，启动 Spring Boot + RPC 服务端"
echo -e ""
echo -e "${CYAN}下一步操作:${NC}"
echo ""
echo -e "  1. 安装 rpc-api / rpc-spring-boot-starter 到本地 Maven 仓库:"
echo -e "     ${YELLOW}cd ${SCRIPT_DIR} && mvn clean install -DskipTests${NC}"
echo ""
echo -e "  2. 在工作空间根目录一键构建全部子模块:"
echo -e "     ${YELLOW}cd ${WORKSPACE_DIR} && mvn clean install -DskipTests${NC}"
echo ""
echo -e "  3. 启动 Nacos 服务 (默认 127.0.0.1:8848)"
echo ""
echo -e "  4. 启动 srv 服务:"
echo -e "     ${YELLOW}cd ${WORKSPACE_DIR}/${SRV_ARTIFACT} && mvn spring-boot:run${NC}"
echo ""
echo -e "  5. 测试本地调用:"
echo -e "     ${YELLOW}curl http://localhost:${SERVER_PORT}/api/demo/hello/world${NC}"
echo ""
echo -e "  6. 其他服务远程调用本服务:"
echo -e "     依赖 ${API_ARTIFACT} 包后使用 @Rpcwired:"
echo -e "     ${YELLOW}@Rpcwired(serviceName = \"${SERVICE_NAME}\")${NC}"
echo -e "     ${YELLOW}private DemoRpcService demoRpcService;${NC}"
echo ""
echo -e "${CYAN}配置说明:${NC}"
echo -e "  Web 端口 (server.port):          ${SERVER_PORT}"
echo -e "  RPC 端口 (nacos.rpc.server-port): ${RPC_PORT}"
echo -e "  Nacos 地址:                       127.0.0.1:8848"
echo -e "  修改配置: ${WORKSPACE_DIR}/${SRV_ARTIFACT}/src/main/resources/application.properties"
echo ""