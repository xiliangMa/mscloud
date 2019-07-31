# mscloud provider 简介
   服务提供者，实现api接口

# config 配置中心添加provider-service-dev.yml

项目地址：https://github.com/xiliangMa/mscloud-config

```java
spring:
  application:
    name: provider-service
server:
  port: 8000
eureka:
  instance:
    hostname: mscloud-provider-service
  client:
    service-url:
      defaultZone: http://mscloud-eureka-service:7000/eureka/
```

*注意: 修改完后提交，否则eureka 启动找不到配置*

------------

# 创建Module provider

** 基于mscloud project 创建 module provider**

------------


# 配置Module provider pom.xml

**添加 eureka、 config 依赖**

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
        <!-- config -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
```

------------


# 配置bootstrap.yml
通过cloud--> config的配置读取配置中心config的信息

```yaml
spring:
  application:
    name: provider-service
  cloud:
    config:
      uri: http://mscloud-config-service:8888
      name: provider-service
      label: master
      profile: dev
```


------------


# Config服务端启动入口 EurekaApplication.java

**添加@EnableEurekaClient 注解**

```java

@SpringBootApplication
@EnableEurekaClient
public class ProviderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProviderServiceApplication.class, args);
    }
}
```

------------

# 编写接口
**创建ProviderServiceController.java**

```java
@RestController
public class ProviderServiceController {

    @Value("${server.port}")
    private String port;

    @RequestMapping(value = "/provider/port", method = RequestMethod.GET)
    public String getPort() {
        return String.format("Your provider Service port is: %s ", port);
    }
}

```

------------


# 启动测试
**1. 首先要启动config 配置中心**
**2. 启动eureka 服务端**
**3. 启动provider服务**

provider 接口访问：

![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/provider-test.png)

provider 注册到eureka 服务中心：

![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/eureka-server-test-provider.png)