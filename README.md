# mscloud
1. 基于 spring-boot、 spring-cloud 的微服务demo， 带你快速的熟悉spring-cloud 微服务模式。
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


# Build 镜像
    以config 服务为例
    
    1. cd config
    2. docker build -t xiliangma/mscloud-config .

   
# Run by docker
    以config 服务为例
    
    docker run -p 8888:8888 --restart=unless-stopped -d -v /tmp/mscloud/config:/mscloud/config mxiliangma/mscloud-config:latest
    
    
# run all by docker-compose 
    docker-compose up -d
    
   注意：如果部署在公有云服务器，需要将配置中心的各个服务域名配置成ip，或者购买云服务的自定义域名解析，否则服务注册发现失败。
 

# 解析配置
    修改 hosts 文件，替换为自己的ip（如果以容器部署必须修改成本地ip）
    127.0.0.1 eureka-server-7000
    127.0.0.1 eureka-server-7001
    127.0.0.1 eureka-server-7002
    127.0.0.1 provider-service-8001
    127.0.0.1 provider-service-8000
    127.0.0.1 consumer-service-feign-9001
    127.0.0.1 consumer-service-ribbon-9000
    127.0.0.1 zipkin-service-3000
    127.0.0.1 admin-service-2000
    127.0.0.1 config-service-8888
    127.0.0.1 api-gateway-5000
  
  
# 服务启动访问地址
 1. 配置中心 config：http://config-service-8888:8888/eureka-service/dev
 2. 服务注册中心 eureka：http://eureka-server-7000:7000/
 3. 服务提供者: http://provider-service-8000:8000/provider/port
 4. 服务消费者feign：http://consumer-service-feign-9001:9001/consumer/port
 5. 断路器hystrix dashboard：http://consumer-service-feign-9001:9001/hystrix
 6. 链路监控 zipkin：http://zipkin-service-3000:8080/zipkin/
 7. 服务网管zuul：http://api-gateway-5000:5000/api/feign/consumer/port?token=test
 8. 服务监控 amdin：http://admin-service-2000:2000/#/applications
 
 
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
 
 
