# Spring-Cloud Eureka 简介
云端服务发现，一个基于 REST 的服务，用于定位服务，以实现云端中间层服务发现和故障转移。

------------

# config 配置中心添加eureka-service-dev.yml

项目地址：https://github.com/xiliangMa/mscloud-config
```xml
spring:
  application:
    name: eureka-server

server:
  port: 7000

eureka:
  instance:
    # k8s
    hostname: mscloud-eureka-service
  client:
    service-url:
      defaultZone: http://mscloud-eureka-service:7000/eureka/
    fetch-registry: false
    register-with-eureka: false
  server:
    peer-node-read-timeout-ms: 10000
    registry-sync-retries: 20
```
注意: 修改完后提交，否则eureka 启动找不到配置

------------

# 创建Module eureka
 基于mscloud project 创建 module eureka

------------

# 配置Module eureka pom.xml
添加eureka server 依赖

```xml
        <!-- cloud eureka -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>
```

------------

# 配置bootstrap.yml
通过cloud--> config的配置读取配置中心config的信息

```yaml
spring:
  application:
    name: eureka-server
  cloud:
    config:
      uri: http://mscloud-config-service:8888
      name: eureka-service #配置中心文件名称
      label: master
      profile: dev
```

------------

# Config服务端启动入口 EurekaApplication.java
添加@EnableEurekaServer 注解

```java
@EnableEurekaServer
@SpringBootApplication
public class EurekaApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication.class, args);
    }
}

```

------------

# 启动测试
**1. 首先要启动config 配置中心** \
**2. 启动eureka 服务端**

出现下面的结果表示成功
![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/eureka-server-test.png)


------------

# 注册config 服务到eureka 中心
**1. ConfigApplication.java 添加注解 @EnableEurekaClient**

```java
@SpringBootApplication
@EnableEurekaClient
@EnableConfigServer
public class ConfigApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigApplication.class, args);
    }
}
```

**2. 重新启动config、eureka 服务**

出现下面的结果表示成功

![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/eureka-server-test-config.png)
