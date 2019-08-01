# Spring-Cloud hystrix 简介

在微服务架构中，将系统拆分成了很多服务单元，各单元的应用间通过服务注册与订阅的方式互相依赖。由于每个单元都在不同的进程中运行，依赖通过远程调用的方式执行，这样就有可能因为网络原因或是依赖服务自身间题出现调用故障或延迟，而这些问题会直接导致调用方的对外服务也出现延迟，若此时调用方的请求不断增加，最后就会因等待出现故障的依赖方响应形成任务积压，最终导致自身服务的瘫痪。

在微服务架构中，存在着那么多的服务单元，若一个单元出现故障，就很容易因依赖关系而引发故障的蔓延，最终导致整个系统的瘫痪，这样的架构相较传统架构更加不稳定。为了解决这样的问题，产生了断路器等一系列的服务保护机制。

针对上述问题，Spring Cloud Hystrix实现了断路器、线程隔离等一系列服务保护功能。它也是基于Netflix的开源框架Hystrix实现的，该框架的目标在于通过控制那些访问远程系统、服务和第三方库的节点， 从而对延迟和故障提供更强大的容错能力。Hystrix具备服务降级、服务熔断、线程和信号隔离、请求缓存、请求合并以及服务监控等强大功能。

上章节讲到 [Spring-Cloud 实战 四 服务消费 feign](https://blog.csdn.net/weixin_41806245/article/details/97941744 "Spring-Cloud 实战 四 服务消费 feign")

本章基于feign + hystrix 实现客户端的负载均衡、熔断等

------------

# config 配置中心修改consumer-service-feign-dev.yml

项目地址：https://github.com/xiliangMa/mscloud-config

**开启feign熔断器，feign模式对hystrix默认是关闭的**

```xml
# 开启feign 熔断器
feign:
  hystrix:
    enabled: true
```
注意: 修改完后提交。

------------

# 修改父类project pom.xml
**添加 hystrix 依赖**

```xml
 			 <!-- hystrix dashboard -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-netflix-hystrix-dashboard</artifactId>
                <version>${spring-cloud-netflix-hystrix.version}</version>
            </dependency>
```

**标识版本**

```xml
	<properties>
        <java.version>1.8</java.version>
        <spring-cloud.version>Greenwich.RELEASE</spring-cloud.version>
        <spring-boot.version>2.1.6.RELEASE</spring-boot.version>
		<!-- zipkin -->
        <spring-cloud-netflix-hystrix.version>2.0.0.RELEASE</spring-cloud-netflix-hystrix.version>
		<!-- zipkin -->
    </properties>
```

------------
# 其他服务 修改 pom.xml

**添加 zipkin 依赖**
```xml
 		<!-- zipkin -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-zipkin</artifactId>
        </dependency>
```
------------
# 修改Module consumer-feign pom.xml
**添加 hystrix dashboard 依赖**

```xml
         <!-- hystrix dashboard -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-netflix-hystrix-dashboard</artifactId>
        </dependency>
```

------------

# 修改启动类 ConsumerFeignApplication.java
**添加 @EnableHystrixDashboard 注解**

```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableHystrixDashboard
public class ConsumerFeignApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerFeignApplication.class, args);
    }
}

```

------------

# 添加 HystrixDashboardConfiguration.java

```java
@Configuration
public class HystrixDashboardConfiguration {
    @Bean
    public ServletRegistrationBean getServlet() {
        HystrixMetricsStreamServlet streamServlet =new HystrixMetricsStreamServlet();
        ServletRegistrationBean registrationBean =new ServletRegistrationBean(streamServlet);
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/hystrix.stream");
        registrationBean.setName("HystrixMetricsStreamServlet");
        return registrationBean;
    }
}
```

------------

# 添加 ConsumerFeignServiceHystrix.java
实现对熔断的错误处理

```java
@Component
public class ConsumerFeignServiceHystrix implements ConsumerFeignService {
    public String port() {
        return "熔断测试返回结果";
    }
}
```
------------

# 修改 ConsumerFeignService.java

添加 fallback = ConsumerFeignServiceHystrix.class 指定熔断错误处理

```java
@FeignClient(value = "PROVIDER-SERVICE", fallback = ConsumerFeignServiceHystrix.class)
public interface ConsumerFeignService {

    // 这里的url为调用服务端的restapi地址
    @RequestMapping(value = "/provider/port", method = RequestMethod.GET)
    public String port();
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

**熔断dashboard 配置**

![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/hystrix-config-test.png)

**熔断测试**

浏览器打开链接 http://ip:port/consumer/port 多次刷新

![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/hystrix-test.png)



