# Spring-Boot 服务监控 admin 简介

Spring Boot Admin 用于监控基于 Spring Boot 的应用，它是在 Spring Boot Actuator 的基础上提供简洁的可视化 WEB UI。Spring Boot Admin 提供了很多功能，如显示 name、id 和 version，显示在线状态，Loggers 的日志级别管理，Threads 线程管理，Environment 管理等。

------------

# config 配置中心添加 admin-service-dev.yml

项目地址：https://github.com/xiliangMa/mscloud-config
```xml
spring:
  application:
    name: admin-service
  zipkin:
    base-url: http://mscloud-zipkin-service:8080

server:
  port: 2000

eureka:
  instance:
    hostname: mscloud-admin-service
  client:
    service-url:
      defaultZone: http://mscloud-eureka-service:7000/eureka/
```
注意: 修改完后提交。


------------

# 修改父类project pom.xml
**添加 amdin 依赖**

```xml
 			<!-- spring-boot admin -->
            <dependency>
                <groupId>de.codecentric</groupId>
                <artifactId>spring-boot-admin-starter-client</artifactId>
                <version>${spring-boot-admin.version}</version>
            </dependency>
            <dependency>
                <groupId>de.codecentric</groupId>
                <artifactId>spring-boot-admin-server</artifactId>
                <version>${spring-boot-admin.version}</version>
            </dependency>
            <dependency>
                <groupId>de.codecentric</groupId>
                <artifactId>spring-boot-admin-server-ui</artifactId>
                <version>${spring-boot-admin.version}</version>
            </dependency>
```

**标识版本**

```xml
<properties>
        <java.version>1.8</java.version>
        <spring-cloud.version>Greenwich.RELEASE</spring-cloud.version>
        <spring-boot.version>2.1.6.RELEASE</spring-boot.version>
		<!-- admin 版本 -->
        <spring-boot-admin.version>2.1.6</spring-boot-admin.version>
		<!-- admin 版本 -->
        <spring-cloud-netflix-hystrix.version>2.0.0.RELEASE</spring-cloud-netflix-hystrix.version>
        <zipkin.version>2.12.8</zipkin.version>
    </properties>
```

------------

# 其他服务 config 配置中心添加添加 admin 客户端配置

**例如 feign**

```xml
spring:
  application:
    name: consumer-service-feign
  zipkin:
    base-url: http://mscloud-zipkin-service:8080
 # -------需要添加的配置
  boot:
    admin:
      client:
        url: http://mscloud-admin-service:2000
        instance:
          management-url: http://mscloud-consumer-feign-service:9001/actuator
          health-url: http://mscloud-consumer-feign-service:9001/actuator/health
          service-url: http://mscloud-consumer-feign-service:9001
		  
management:
  endpoints:
    web:
      exposure:
        include: '*'
  endpoint:
    health:
      show-details: ALWAYS
# ------- 需要添加的配置
```

------------

# 其他服务 修改pom.xml

**添加 admin 依赖**

```xml
 		<!-- admin -->
        <dependency>
            <groupId>de.codecentric</groupId>
            <artifactId>spring-boot-admin-starter-client</artifactId>
        </dependency>
```

# 创建Module admin
 **基于mscloud project 创建 module admin**

------------

# 配置Module admin pom.xml
**添加eureka 、config、zipkin 、 admin等依赖**

```xml
         <!-- spring boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-autoconfigure</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- cloud eureka -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>
        <!-- admin -->
        <dependency>
            <groupId>de.codecentric</groupId>
            <artifactId>spring-boot-admin-server</artifactId>
        </dependency>
        <dependency>
            <groupId>de.codecentric</groupId>
            <artifactId>spring-boot-admin-server-ui</artifactId>
        </dependency>
        <!-- config -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
```

------------

# 配置bootstrap.yml
**通过cloud--> config的配置读取配置中心config的信息**

```yaml
spring:
  application:
    name: admin-service
  cloud:
    config:
      uri: http://mscloud-config-service:8888
      name: admin-service
      label: master
      profile: dev
```

------------

# admin 服务端启动入口 AdminApplication.java

**添加@EnableAdminServer 注解**

```java
@EnableEurekaClient
@SpringBootApplication
@EnableAdminServer
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}

```

------------

# 启动测试
**1. 首先要启动config 配置中心** \
**2. 启动eureka 服务端** \
**3. 启动provider 服务** \
**4. 启动consumer-feign** \
**5. 启动 zuul** \
**6. 启动zipkin** \
**启动 admin**

**eureka 服务注册：**
![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/eureka-server-test-admin.png)

**admin 服务：**
![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/admin-test.png)

**admin 监控**
![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/admin-info-test.png)

