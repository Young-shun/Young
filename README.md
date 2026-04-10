# Sky Take Out 微服务项目说明

## 1. 项目简介

本项目是一个基于 Spring Cloud Alibaba 的外卖业务后端，采用多模块 Maven 聚合工程。

核心能力包括：

- 用户端：登录、地址管理、购物车、下单。
- 管理端：员工、分类、菜品、套餐、营业状态、工作台、报表。
- 网关统一入口：通过 Gateway 按路径转发到各微服务。

## 2. 技术栈

- Java 8+
- Spring Boot 2.7.3
- Spring Cloud 2021.0.3
- Spring Cloud Alibaba 2021.0.1.0
- Spring Cloud Gateway
- OpenFeign + Sentinel
- MyBatis-Plus
- MySQL 8
- Redis
- Nacos

## 3. 工程结构

### 3.1 公共模块

- sky-common：公共配置、工具类、拦截器、通用组件。
- sky-pojo：DTO、VO、Entity 等模型对象。
- sky-api：服务间调用的 API 定义。

### 3.2 业务服务模块

- sky-user-server：用户相关服务，默认端口 8081。
- category-service：分类服务，默认端口 8082。
- setmeal-service：套餐服务，默认端口 8084。
- order-service：订单服务，默认端口 8085。
- shop-service：店铺服务，默认端口 8086。
- sky-shoppingcart-server：购物车服务，默认端口 8087。
- workspace-service：工作台服务，默认端口 8088。
- address-service：地址服务，默认端口 8089。
- report-service：报表服务，默认端口 8090。
- employee-service：员工服务，默认端口 8091。
- dish-service：菜品服务，默认端口 8092。
- sky-gateway：网关服务，默认端口 8080。

### 3.3 网关路由到服务名（Nacos 注册名）

- /user/category/**、/admin/category/** -> sky-take-out-category
- /user/dish/**、/admin/dish/** -> sky-take-out-dish
- /user/setmeal/**、/admin/setmeal/** -> sky-take-out-setmeal
- /user/order/**、/admin/order/**、/ws/** -> sky-take-out-order
- /user/shop/**、/admin/shop/** -> sky-take-out-shop
- /admin/employee/** -> sky-take-out-employee
- /admin/workspace/** -> sky-take-out-workspace
- /user/address/**、/user/addressBook/** -> sky-take-out-address
- /admin/report/** -> sky-take-out-report
- /user/shoppingCart/**、/user/shoppingcart/** -> sky-take-out-shoppingcart
- /user/user/** -> sky-take-out-user

## 4. 运行前准备

请先准备以下基础设施：

- MySQL（默认使用 3306）
- Redis（默认使用 6379）
- Nacos（默认使用 8848）

当前配置文件中默认 Nacos 地址为：

- 192.168.100.128:8848

建议按本机环境修改各服务的 application-dev.yml：

- sky.datasource.host / port / database / username / password
- sky.redis.host / port / database
- sky.alioss 和 sky.wechat 相关密钥

注意：仓库中示例配置含有云服务和支付相关字段，仅用于本地调试示意，部署前请替换为你自己的真实配置，并避免将敏感信息提交到版本库。

## 5. 快速启动（后端 + Nginx 前端 + 微信小程序）

### 5.1 初始化 MySQL 数据

使用 `source/Ordering platform.sql` 初始化数据库（默认库名 `sky_take_out`）。

示例：

```sql
SOURCE source/Ordering platform.sql;
```

### 5.2 配置后端环境变量（建议）

各服务 `application-dev.yml` 中的敏感字段已替换为环境变量占位符，建议在本机环境中设置后再启动：

- `MYSQL_USERNAME`
- `MYSQL_PASSWORD`
- `ALIYUN_OSS_ACCESS_KEY_ID`
- `ALIYUN_OSS_ACCESS_KEY_SECRET`
- `WECHAT_APPID`
- `WECHAT_SECRET`
- `WECHAT_MCHID`
- `WECHAT_MCH_SERIAL_NO`
- `WECHAT_PRIVATE_KEY_FILE_PATH`
- `WECHAT_API_V3_KEY`
- `WECHAT_PAY_CERT_FILE_PATH`
- `BAIDU_MAP_AK`

### 5.3 安装公共模块到本地仓库

在根目录执行：

	mvn -pl sky-api,sky-common,sky-pojo -am install -DskipTests

### 5.4 启动后端服务

可在根目录按模块分别启动（建议先启动业务服务，最后启动网关）：

	mvn -pl sky-user-server spring-boot:run
	mvn -pl category-service spring-boot:run
	mvn -pl dish-service spring-boot:run
	mvn -pl setmeal-service spring-boot:run
	mvn -pl order-service spring-boot:run
	mvn -pl shop-service spring-boot:run
	mvn -pl sky-shoppingcart-server spring-boot:run
	mvn -pl workspace-service spring-boot:run
	mvn -pl address-service spring-boot:run
	mvn -pl report-service spring-boot:run
	mvn -pl employee-service spring-boot:run

最后启动网关：

	mvn -pl sky-gateway spring-boot:run

### 5.5 启动 Nginx 前端

前端静态资源在 `source/nginx/html/sky`，Nginx 配置在 `source/nginx/conf/nginx.conf`。

Windows 下可在 `source/nginx` 目录执行：

```powershell
./nginx.exe
```

停止：

```powershell
./nginx.exe -s stop
```

### 5.6 启动微信小程序

小程序源码压缩包位于 `source/mp-weixin.zip`：

1. 解压后使用微信开发者工具导入项目。
2. 将请求地址配置为你的网关地址（例如 `http://localhost:8080`）。
3. 使用你自己的小程序 AppID 与后端登录配置联调。

### 5.7 访问入口

- 网关地址：http://localhost:8080
- 管理端前端（Nginx）：按 `source/nginx/conf/nginx.conf` 中监听端口访问
- 管理端登录接口：POST /admin/employee/login
- 用户端登录接口：POST /user/user/login

## 6. 常见问题排查

1. 根工程构建时报 sky-server 模块不存在。

- 现状是工程已拆分为多个独立服务模块。
- 若本地没有 sky-server 目录，可移除根 pom.xml 中 sky-server 模块声明后再构建。

2. 修改共享接口后，服务启动仍引用旧 SNAPSHOT。

- 先重新安装公共模块：

	  mvn -pl sky-api,sky-common -am install -DskipTests

- 再重启对应服务。

3. Feign 报错 AbstractMethodError（RibbonLoadBalancerClient 相关）。

- 在对应服务中排除 spring-cloud-starter-netflix-ribbon。
- 添加 spring-cloud-starter-loadbalancer。

4. LocalDate 通过 Feign 传参导致 400。

- 接口若使用 yyyy-MM-dd，客户端参数需显式标注 ISO.DATE，避免日期格式被序列化为 yyyy/M/d。

5. 引入 Sentinel 后启动报 NoProviderFoundException。

- 需要补充 spring-boot-starter-validation 依赖。

6. 业务接口取不到当前用户上下文。

- 需确认公共模块中的 WebMvcConfiguration 已注册拦截器，否则 BaseContext 无法注入 userInfo/empInfo。

## 7. 开发建议

- 优先通过网关进行联调，减少直连服务带来的鉴权与路由差异。
- 跨服务调用时使用 Nacos 服务名，不要误用模块目录名。
- 新增公共能力时尽量放到 sky-common 或 sky-api，统一复用并降低重复实现。



