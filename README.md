# mscloud
1. 基于 spring-boot、 spring-cloud 的微服务demo， 带你快速的熟悉spring-cloud 微服务模式，熟悉互联网常见微服务架构。
2. 通过Docker实现 spring-cloud微服务容器化，实现传统应用到容器化的过度。
3. 通过k8s编排如何实现sping-cloud服务到 kubernets平台部署、自动化编排调度。

注：
本项目意在如何快速的了解、搭建spring-cloud微服务，并从快速的实现容器化部署、以及kubernets平台的自动化编排部署的过程， 不涉及细节代码的开发。
如果有任何问题，请联系指正。


# 服务组件
 1. 服务注册中心 eureka
 2. 文件配置中心 config
 3. 服务提供者
 4. 服务消费者 （ribbon、feign 负载均衡客户端模式）
 5. 服务断路器 hystrix
 6. 服务网关 zuul
 7. 服务监控 admin
 8. 服务链路监控 zipkin


# build image （例：config）
    1. cd config
    2. docker build -t xiliangma/mscloud-config .
     
# run by docker (例： config)
     docker run -p 8888:8888 --restart=unless-stopped -d -v /tmp/mscloud/config:/mscloud/config mxiliangma/mscloud-config:latest
    
    
# run all by docker-compose 
    docker-compose up -d

# 删除mscloud 所有容器
    1. by docker
        docker rm -f $(docker ps  -a --format "table {{.Names}}" | grep mscloud)
    2. by docker-compose
        docker-compose down
    
    
# 容器化
 1. docker
 2. docker-compose
 3. kubernetes
 

 # TODO
    1. gitlab cicd 小问题处理
    2. 实现 spring cloud 微服务 + istio的结合
 
 
