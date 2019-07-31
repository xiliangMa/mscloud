# Spring-Cloud Config 简介
Spring Cloud Config是Spring创建的一个全新的服务端/客户端项目，为应用提供分布式的配置存储，提供集中化的外部配置支持。它除了适用于Spring构建的应用程序外，也可以在其他语言运行的应用程序中使用。

Spring Cloud Config分为服务端和客户端两部分。其中服务端称为分布式配置中心，用来连接配置仓库并为客户端提供获取配置信息、加密/解密信息的功能；客户端则是微服务架构中的各个微服务应用，它们通过指定的配置中心来管理与业务相关的配置内容，并在启动的时候从配置中心获取和加载配置信息。

Spring Cloud Config实现的配置中心默认采用Git来存储配置信息，但也提供了对其他存储方式的支持，比如SVN仓库、本地文件系统。在本例中构建了一个基于Git存储的分布式配置中心，并在客户端中演示了如何制定应用所属的配置中心，并能够从配置中心获取配置信息的整个过程。

# 准备config 仓库
很简单创建一个普通项目，git管理即可；
项目地址：https://github.com/xiliangMa/mscloud-config

# 创建项目
 1. 基于ide 或者 eclipse 创建project mscloud。 [mscloud github仓库地址](https://github.com/xiliangMa/mscloud "mscloud地址")
 2. 创建Module config。


# 配置 mscloud 父类project pom.xml
**引入依赖：**
>  **Spring-Cloud Version**： Greenwich.RELEASE
>  **Spring-Boot Version:** 2.1.6.RELEASE
>  **JAVA Version:** 1.8

```xml
        <!-- spring-cloud -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <!-- spring-boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-parent</artifactId>
            <version>${spring-boot.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
```
# 配置Module config pom.xml
**引入config 依赖**

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
        <!-- config -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
        </dependency>
```
# 配置bootstrap.yml
```yaml
spring:
  application:
    name: config-service
  zipkin:
    base-url: http://mscloud-zipkin-service:8080
  boot:
    admin:
      client:
        url: http://mscloud-admin-service:2000
        instance:
          management-url: http://mscloud-config-service:8888/actuator
          health-url: http://mscloud-config-service:8888/actuator/health
          service-url: http://mscloud-config-service:8888
  cloud:
    config:
      label: master
      server:
        git:
		  # fork后修改成自己的仓库地址即可
          uri: https://github.com/xiliangMa/mscloud-config.git
          search-paths: mscloud
          username:
          password:
```

# Config服务端启动入口 ConfigApplication.java
配置@EnableConfigServer 注解

```java
@SpringBootApplication
@EnableConfigServer
public class ConfigApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigApplication.class, args);
    }
}

```
# 启动测试
出现下面的结果表示成功

![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/config-test.png)

