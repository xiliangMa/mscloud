# Spring-Cloud Feign 简介
Feign是一个声明式的Web服务客户端。这使得Web服务客户端的写入更加方便 要使用Feign创建一个界面并对其进行注释。它具有可插拔注释支持，包括Feign注释和JAX-RS注释。Feign还支持可插拔编码器和解码器。Spring Cloud添加了对Spring MVC注释的支持，并在Spring Web中使用默认使用的HttpMessageConverters。Spring Cloud集成Ribbon和Eureka以在使用Feign时提供负载均衡的http客户端。

------------

# config 配置中心添加consumer-service-feign-dev.yml

项目地址：https://github.com/xiliangMa/mscloud-config

```xml
spring:
  application:
    name: consumer-service-feign
server:
  port: 9001
eureka:
  instance:
    hostname: mscloud-consumer-feign-service
  client:
    service-url:
      defaultZone: http://mscloud-eureka-service:7000/eureka/
```
注意: 修改完后提交，否则eureka 启动找不到配置

------------

# 创建Module consumer-feign
 **基于mscloud project 创建 module consumer-feign**

------------

# 配置Module consumer-feign pom.xml
**添加eureka 、config、openfeign等 依赖**

```xml
        <!-- spring boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!-- cloud eureka -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-autoconfigure</artifactId>
        </dependency>
        <!-- feign -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
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
    name: consumer-service-feign
  cloud:
    config:
      uri: http://mscloud-config-service:8888
      name: consumer-service-feign
      label: master
      profile: dev
```

------------

# Config服务端启动入口 ConsumerFeignApplication.java

**添加@EnableDiscoveryClient(服务消费者) @EnableFeignClients 注解**

```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ConsumerFeignApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerFeignApplication.class, args);
    }
}

```

------------

# 实现ConsumerFeignService.java

> 添加@FeignClient 注解value为eureka中服务的名称，可以通过部署多个provider实现看到feign的负载均衡访问

```java
@FeignClient(value = "PROVIDER-SERVICE")
public interface ConsumerFeignService {

    // 这里的url为调用服务端的restapi地址
    @RequestMapping(value = "/provider/port", method = RequestMethod.GET)
    public String port();
}
```
------------

# 实现ConsumerFeignController.java

```java
@RestController
public class ConsumerFeingController {

    @Autowired
    private ConsumerFeignService service;

    @RequestMapping(value = "/consumer/port", method = RequestMethod.GET)
    public String port() {
        return service.port();
    }
}

```

------------

# 启动测试
**1. 首先要启动config 配置中心** \
**2. 启动eureka 服务端** \
**3. 启动provider 服务** \
**4. 启动consumer-feign**

**consumer 接口测试：**
![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/consumer-test-feign.png)

**eureka 服务注册：**
![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/eureka-server-test-feign.png)

