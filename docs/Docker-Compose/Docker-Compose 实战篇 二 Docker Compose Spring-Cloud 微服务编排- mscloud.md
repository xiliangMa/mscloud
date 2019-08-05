# 概述
上章节链接 [Docker-Compose 基础篇 一 初识docker-compose](https://blog.csdn.net/weixin_41806245/article/details/98071815 "Docker-Compose 基础篇 一 初识docker-compose")

基于Docker-compose 实现 Spring-Cloud 的微服务编排。
mcloud地址：https://github.com/xiliangMa/mscloud
配置中心源码地址：https://github.com/xiliangMa/mscloud-config


------------


# 操作步骤
#### mscloud 结构- mscloud 结构
- 服务注册中心 eureka
- 文件配置中心 config
- 服务提供者
- 服务消费者 （ribbon、feign 负载均衡客户端模式）
- 服务断路器 hystrix
- 服务网关 zuul
- 服务监控 admin
- 服务链路监控 zipkin

#### Dockerfile

**配置中心** 
> 其他的服务都是类似只需要修改jar包名即可。

```
FROM java:8-jre
MAINTAINER xiliangMa <xiliangMa@outlook.com>

ADD ./target/admin-0.0.1-SNAPSHOT.jar /app/

CMD ["java", "-Xmx200m", "-jar", "/app/admin-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=dev"]

EXPOSE 2000
```

#### Build 镜像
> 以配置中心为例其他的一致
	
	cd config
	docker build -t xiliangma/mscloud-config .

#### Docker-Compose.yml
**服务启动顺序**
> provider 服务后顺序就无所谓了，前三个需要保证，需要通过 docker-compose depends_on 和 [wait-for-it.sh](https://github.com/vishnubob/wait-for-it "wait-for-it.sh") 控制。

1. config 配置中心服务
2. eureka 服务注册中心服务
3. provider
4. consumer-feign 负载均衡客户端服务
5. zuul 网关服务
6. zipkin全链路监控服务
7. admin 服务监控

```yaml
version: '3'
services:
  # -------- config --------
  config:
    restart: always
    container_name: mscloud-config
    image: xiliangma/mscloud-config:latest
    ports:
      - "8888:8888"
    volumes:
      - /tmp/mscloud/config/:/maslcoud/config


  # -------- eureka cluster --------
  eureka-1:
    restart : always
    container_name: mscloud-eureka-1
    image: xiliangma/mscloud-eureka:latest
    ports:
      - "7000:7000"
    volumes:
      - /tmp/mscloud/eureka-1/:/maslcoud/eureka-1
      - ./wait-for-it.sh:/mscloud/wait-for-it.sh
      - ./eureka/entrypoint.sh:/mscloud/eureka/entrypoint.sh
    depends_on:
      - config
    entrypoint: ["/mscloud/wait-for-it.sh", "-t", "0", "config:8888", "--strict", "--", "/mscloud/eureka/entrypoint.sh"]

#  eureka-2:
#    restart : always
#    container_name: mscloud-eureka-2
#    image: xiliangma/mscloud-eureka:latest
#    ports:
#      - "7001:7000"
#    volumes:
#      - /tmp/mscloud/eureka-2/:/maslcoud/eureka-2
#      - ./wait-for-it.sh:/mscloud/wait-for-it.sh
#      - ./eureka/entrypoint.sh:/mscloud/eureka/entrypoint.sh
#    depends_on:
#      - config
#    entrypoint: ["/mscloud/wait-for-it.sh", "-t", "0", "config:8888", "--strict", "--", "/mscloud/eureka/entrypoint.sh"]
#
#
#  eureka-3:
#    restart : always
#    container_name: mscloud-eureka-3
#    image: xiliangma/mscloud-eureka:latest
#    ports:
#      - "7002:7000"
#    volumes:
#      - /tmp/mscloud/eureka-3/:/maslcoud/eureka-3
#      - ./wait-for-it.sh:/mscloud/wait-for-it.sh
#      - ./eureka/entrypoint.sh:/mscloud/eureka/entrypoint.sh
#    depends_on:
#      - config
#    entrypoint: ["/mscloud/wait-for-it.sh", "-t", "0", "config:8888", "--strict", "--", "/mscloud/eureka/entrypoint.sh"]


  # -------- provider --------
  provider:
    restart : always
    container_name: mscloud_provider_service
    image: xiliangma/mscloud-provider:latest
    ports:
      - "8000:8000"
    volumes:
      - /tmp/mscloud/provider/:/maslcoud/provider
      - ./wait-for-it.sh:/mscloud/wait-for-it.sh
      - ./provider-service/entrypoint.sh:/mscloud/provider-service/entrypoint.sh
    depends_on:
      - config
      - eureka-1
#      - eureka-2
#      - eureka-3
    entrypoint: ["/mscloud/wait-for-it.sh", "-t", "0", "eureka-1:7000", "--strict", "--", "/mscloud/provider-service/entrypoint.sh"]


  # -------- consumer ribbon --------
  consumer-ribbon:
    restart : always
    container_name: mscloud-consumer-ribbon
    image: xiliangma/mscloud-consumer-ribbon:latest
    ports:
      - "9000:9000"
    volumes:
      - /tmp/mscloud/consumer-ribbon/:/maslcoud/consumer-ribbon
      - ./wait-for-it.sh:/mscloud/wait-for-it.sh
      - ./consumer-ribbon/entrypoint.sh:/mscloud/consumer-ribbon/entrypoint.sh
    depends_on:
      - config
      - eureka-1
#      - eureka-2
#      - eureka-3
      - provider
    entrypoint: ["/mscloud/wait-for-it.sh", "-t", "0", "eureka-1:7000", "--strict", "--", "/mscloud/consumer-ribbon/entrypoint.sh"]


  # -------- consumer feign --------
  consumer-feign:
    restart : always
    container_name: mscloud-consumer-feign
    image: xiliangma/mscloud-consumer-feign:latest
    ports:
      - "9001:9001"
    volumes:
      - /tmp/mscloud/consumer-feign/:/maslcoud/consumer-feign
      - ./wait-for-it.sh:/mscloud/wait-for-it.sh
      - ./consumer-feign/entrypoint.sh:/mscloud/consumer-feign/entrypoint.sh
    depends_on:
      - config
      - eureka-1
#      - eureka-2
#      - eureka-3
    entrypoint: ["/mscloud/wait-for-it.sh", "-t", "0", "eureka-1:7000", "--strict", "--", "/mscloud/consumer-feign/entrypoint.sh"]


  # -------- zuul --------
  zuul:
    container_name: mscloud-zuul
    image: xiliangma/mscloud-zuul:latest
    ports:
      - "5000:5000"
    volumes:
      - /tmp/mscloud/zuul/:/maslcoud/zuul
      - ./wait-for-it.sh:/mscloud/wait-for-it.sh
      - ./zuul/entrypoint.sh:/mscloud/zuul/entrypoint.sh
    depends_on:
      - config
      - eureka-1
#      - eureka-2
#      - eureka-3
    entrypoint: ["/mscloud/wait-for-it.sh", "-t", "0", "eureka-1:7000", "--strict", "--", "/mscloud/zuul/entrypoint.sh"]


  # -------- admin --------
  admin:
    container_name: mscloud-admin
    image: xiliangma/mscloud-admin:latest
    ports:
      - "2000:2000"
    volumes:
      - /tmp/mscloud/admin/:/maslcoud/admin
      - ./wait-for-it.sh:/mscloud/wait-for-it.sh
      - ./admin/entrypoint.sh:/mscloud/admin/entrypoint.sh
    depends_on:
      - config
      - eureka-1
#      - eureka-2
#      - eureka-3
    entrypoint: ["/mscloud/wait-for-it.sh", "-t", "0", "eureka-1:7000", "--strict", "--", "/mscloud/admin/entrypoint.sh"]



  # -------- zipkin --------
  zipkin:
    container_name: mscloud-zipkin
    image: xiliangma/mscloud-zipkin:latest
    ports:
      - "3000:3000"
      - "8080:8080"
    volumes:
      - /tmp/mscloud/zipkin/:/maslcoud/zipkin
      - ./wait-for-it.sh:/mscloud/wait-for-it.sh
      - ./zipkin/entrypoint.sh:/mscloud/zipkin/entrypoint.sh
    depends_on:
      - config
      - eureka-1
#      - eureka-2
#      - eureka-3
      - consumer-feign
    entrypoint: ["/mscloud/wait-for-it.sh", "-t", "0", "eureka-1:7000", "--strict", "--", "/mscloud/zipkin/entrypoint.sh"]

```
# 测试
#### 启动
	docker-compose up -d
	
![](https://github.com/xiliangMa/mscloud/blob/master/images/docker-compose-ps.png)

#### 关闭
	docker-compose down -v




