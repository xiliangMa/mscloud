# Spring-Cloud 全链路监控 zipkin 简介

Zipkin 是一个开放源代码分布式的跟踪系统，由Twitter公司开源，它致力于收集服务的定时数据，以解决微服务架构中的延迟问题，包括数据的收集、存储、查找和展现。

每个服务向zipkin报告计时数据，zipkin会根据调用关系通过Zipkin UI生成依赖关系图，显示了多少跟踪请求通过每个服务，该系统让开发者可通过一个 Web 前端轻松的收集和分析数据，例如用户每次请求服务的处理时间等，可方便的监测系统中存在的瓶颈。

Zipkin提供了可插拔数据存储方式：In-Memory、MySql、Cassandra以及Elasticsearch。接下来的测试为方便直接采用In-Memory方式进行存储，生产推荐Elasticsearch。

------------

# config 配置中心添加 zipkin-service-dev.yml

项目地址：https://github.com/xiliangMa/mscloud-config
```xml
spring:
  application:
    name: zipkin-service

server:
  port: 3000

eureka:
  instance:
    hostname: mscloud-zipkin-service
  client:
    service-url:
      defaultZone: http://mscloud-eureka-service:7000/eureka/
```


注意: 修改完后提交。


------------
# 其他服务 config 配置中心添加添加 zipkin url

**例如 feign**

```xml
spring:
  application:
    name: consumer-service-feign
  zipkin: #需要增加的内容
    base-url: http://mscloud-zipkin-service:8080
```

------------

# 其他服务 修改pom.xml

**添加zipkin 依赖**


# 创建Module zipkin
 **基于mscloud project 创建 module zipkin**

------------

# 配置Module zuul pom.xml
**添加eureka 、config、zipkin 等依赖**

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
		 <!-- config -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
        <!-- zipkin -->
        <dependency>
            <groupId>io.zipkin.java</groupId>
            <artifactId>zipkin-server</artifactId>
        </dependency>
        <dependency>
            <groupId>io.zipkin.java</groupId>
            <artifactId>zipkin-autoconfigure-ui</artifactId>
        </dependency>
```

------------

# 配置bootstrap.yml
**通过cloud--> config的配置读取配置中心config的信息**

```yaml
spring:
  application:
    name: zipkin-service
  cloud:
    config:
      uri: http://mscloud-config-service:8888
      name: zipkin-service
      label: master
      profile: dev
```

------------

# zipkin服务端启动入口 ZipkinApplication.java

**添加@EnableZipkinServer 注解**

```java
@EnableEurekaClient
@EnableZipkinServer
@SpringBootApplication
public class ZipkinApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZipkinApplication.class, args);
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
**6. 启动zipkin**

**eureka 服务注册：**
![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/eureka-server-test-zipkin.png)

**zipkin 服务：**

![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/zipkin-service-test.png)

**zipkin 请求监控：**
![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/zipkin-request-test.png)

**zipkin 请求链路结构：**
![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/zipkin-request-tree.png)

**zipkin 请求链路依赖：**

![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/zipkin-request-depen.png)





