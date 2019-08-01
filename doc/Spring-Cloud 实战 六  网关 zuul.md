# Spring-Cloud zuul 简介

为什么需要网关呢？
我们知道我们要进入一个服务本身，很明显我们没有特别好的办法，直接输入IP地址+端口号，我们知道这样的做法很糟糕的，这样的做法大有问题，首先暴露了我们实体机器的IP地址，别人一看你的IP地址就知道服务部署在哪里，让别人很方便的进行攻击操作。

第二，我们这么多服务，我们是不是要挨个调用它呀，我们这里假设做了个权限认证，我们每一个客户访问的都是跑在不同机器上的不同的JVM上的服务程序，我们每一个服务都需要一个服务认证，这样做烦不烦呀，明显是很烦的。

那么我们这时候面临着这两个极其重要的问题，这时我们就需要一个办法解决它们。首先，我们看IP地址的暴露和IP地址写死后带来的单点问题，我是不是对这么服务本身我也要动态的维护它服务的列表呀，我需要调用这服务本身，是不是也要一个负载均衡一样的玩意，

还有关于IP地址暴露的玩意，我是不是需要做一个代理呀，像Nginx的反向代理一样的东西，还有这玩意上部署公共的模块，比如所有入口的权限校验的东西。因此我们现在需要Zuul API网关。它就解决了上面的问题，你想调用某个服务，它会给你映射，把你服务的IP地址映射成


------------

# zuul 的强大功能
    验证与安全保障: 识别面向各类资源的验证要求并拒绝那些与要求不符的请求。
	审查与监控: 在边缘位置追踪有意义数据及统计结果，从而为我们带来准确的生产状态结论。
	动态路由: 以动态方式根据需要将请求路由至不同后端集群处。
	压力测试: 逐渐增加指向集群的负载流量，从而计算性能水平。
	负载分配: 为每一种负载类型分配对应容量，并弃用超出限定值的请求。
	静态响应处理: 在边缘位置直接建立部分响应，从而避免其流入内部集群。
	多区域弹性: 跨越AWS区域进行请求路由，旨在实现ELB使用多样化并保证边缘位置与使用者尽可能接近。
------------

# config 配置中心添加zuul-service-dev.yml

项目地址：https://github.com/xiliangMa/mscloud-config

```xml
spring:
  application:
    name: zuul
server:
  port: 5000

eureka:
  instance:
    hostname: mscloud-zuul-service
  client:
    service-url:
      defaultZone: http://mscloud-eureka-service:7000/eureka/

zuul:
  routes:
    api-feign:
      path: /api/feign/**
      serviceId: consumer-service-feign
```
注意: 修改完后提交。

------------

# 创建Module zuul
 **基于mscloud project 创建 module zuul**

------------

# 配置Module zuul pom.xml
**添加eureka 、config、zuul 依赖**

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
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
        <!-- cloud eureka -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>
        <!-- zuul -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
        </dependency>
```

------------

# 配置bootstrap.yml
**通过cloud--> config的配置读取配置中心config的信息**

```yaml
spring:
  application:
    name: zuul-service
  cloud:
    config:
      uri: http://mscloud-config-service:8888
      name: zuul-service
      label: master
      profile: dev
```

------------

# zuul服务端启动入口 ZuulApplication.java

**添加@EnableZuulProxy 注解**

```java
@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
public class ZuulApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZuulApplication.class, args);
    }
}
```

------------

# 添加 FeignFallBackProvider.java
**实现feign 消费端的接口到zuul的注册**

```java
@Component
public class FeignFallBackProvider implements FallbackProvider {

    public String getRoute() {
        return "consumer-service-feign";
    }

    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        return new ClientHttpResponse() {
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.OK;
            }

            public int getRawStatusCode() throws IOException {
                return HttpStatus.OK.value();
            }

            public String getStatusText() throws IOException {
                return HttpStatus.OK.getReasonPhrase();
            }

            public void close() {

            }

            public InputStream getBody() throws IOException {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("status", HttpStatus.OK);
                map.put("message", "无法连接，请检查您的网络!!!!");
                return new ByteArrayInputStream(objectMapper.writeValueAsString(map).getBytes("UTF-8"));
            }

            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                return headers;
            }
        };
    }
}
```
------------

# 添加 LoginFilter.java
**实现接口访问的登入检测**
```java
@Component
public class LoginFilter extends ZuulFilter {
    public String filterType() {
        return "pre";
    }

    public int filterOrder() {
        return 0;
    }

    public boolean shouldFilter() {
        return true;
    }

    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String token = request.getParameter("token");
        if (token == null) {
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(401);
            try {
                context.getResponse().getWriter().write("Token is enpty");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
```

------------

# 启动测试
**1. 首先要启动config 配置中心** \
**2. 启动eureka 服务端** \
**3. 启动provider 服务** \
**4. 启动consumer-feign** \
**5. 启动 zuul**

**eureka 服务注册：**
![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/eureka-server-test-zuul.png)

**zuul 接口测试：**

![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/zuul-test.png)

