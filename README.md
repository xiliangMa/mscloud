[![Build Status](https://api.travis-ci.com/xiliangMa/mscloud.svg?branch=master)](https://travis-ci.org/xiliangMa/mscloud)
[![GitHub license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/xiliangMa/mscloud/blob/master/LICENSE)

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
 
 ![](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/mscloud-module.png)



# Build 镜像
    以config 服务为例
    
    1. cd config
    2. docker build -t xiliangma/mscloud-config .

   
# Deploy by docker
    以config 服务为例
    
    docker run -p 8888:8888 --restart=unless-stopped -d -v /tmp/mscloud/config:/mscloud/config mxiliangma/mscloud-config:latest
    
    
# Deploy by docker-compose 
    docker-compose up -d

![image](https://github.com/xiliangMa/mscloud/blob/master/images/docker-compose-ps.png)
   
   注意：如果部署在公有云服务器，需要将配置中心的各个服务域名配置成ip，或者购买云服务的自定义域名解析，否则服务注册发现失败。
 
 [spring cloud 微服务 如何通过 docker-compose 编排](https://github.com/xiliangMa/mscloud/blob/master/docs/Docker-Compose/Docker-Compose%20实战篇%20二%20Docker%20Compose%20Spring-Cloud%20微服务编排-%20mscloud.md)

# deploy by kubernetes
1. 集群搭建
        
        kubeadm 搭建k8s集群： https://blog.csdn.net/weixin_41806245/article/details/89381752
        
 2. 部署 mscloud

        kubectl apply -f kubernetes/*.yml
        
![image](https://github.com/xiliangMa/mscloud/blob/master/images/k8s-mscloud.png) 

 [spring cloud 微服务 迁移到 kubernetes平台](https://github.com/xiliangMa/mscloud/blob/master/docs/Spring-Cloud/spring-cloud%20实战%20十%20spring%20cloud%20微服务%20迁移到%20kubernetes平台.md)

  
# Srping-Cloud 实战文章链接
1. [Spring-Cloud 实战 一 服务配置中心 config](https://blog.csdn.net/weixin_41806245/article/details/97923108)
2. [Spring-Cloud 实战 二 服务注册与发现 eureka](https://blog.csdn.net/weixin_41806245/article/details/97928982)
3. [Spring-Cloud 实战 三 服务提供者](https://blog.csdn.net/weixin_41806245/article/details/97937802)
4. [Spring-Cloud 实战 四 服务消费 feign](https://blog.csdn.net/weixin_41806245/article/details/97941744)
5. [Spring-Cloud 实战 五 服务消费 feign + 断路器 hystrix](https://blog.csdn.net/weixin_41806245/article/details/98036680)
6. [Spring-Cloud 实战 六 网关 zuul](https://blog.csdn.net/weixin_41806245/article/details/98039798)
7. [Spring-Cloud 实战 七 全链路监控 zipkin](https://blog.csdn.net/weixin_41806245/article/details/98041920)
8. [Spring-Cloud 实战 八 服务监控 spring-boot admin](https://blog.csdn.net/weixin_41806245/article/details/98044072)
9. [Spring-Cloud 实战 十 spring cloud 微服务迁移到 kubernetes 集群](https://blog.csdn.net/weixin_41806245/article/details/98852981)
  
# istio 实战文章链接:
1. [istio 实战 一 Kubernetes 中快速搭建 istio](https://blog.csdn.net/weixin_41806245/article/details/99589663)
2. [istio 实战 二 bookinfo 部署](https://blog.csdn.net/weixin_41806245/article/details/99589830)
3. [istio 实战 三 智能路由](https://blog.csdn.net/weixin_41806245/article/details/99629346)
4. [istio 实战 四 权重路由以及监控](https://blog.csdn.net/weixin_41806245/article/details/99644214)
5. [istio 实战 五 网格可视化 - kiali](https://blog.csdn.net/weixin_41806245/article/details/99674470)
6. [istio 实战 六 全链路监控 - Jaeger](https://blog.csdn.net/weixin_41806245/article/details/99675558)

   
# 服务启动访问地址
 1. 配置中心 config：http://mscloud-config-service:8888/eureka-service/dev
 2. 服务注册中心 eureka：http://mscloud-eureka-service:7000/
 3. 服务提供者: http://mscloud-provider-service:8000/provider/port
 4. 服务消费者feign：http://mscloud-consumer-service-feign:9001/consumer/port
 5. 断路器hystrix dashboard：http://mscloud-consumer-service-feign:9001/hystrix
 6. 链路监控 zipkin：http://mscloud-zipkin-service:8080/zipkin/
 7. 服务网管zuul：http://mscloud-zuul-service:5000/api/feign/consumer/port?token=test
 8. 服务监控 amdin：http://mscloud-admin-service:2000/#/applications
 
 
 ## 解析配置
     修改 hosts 文件，替换为自己的ip（如果以容器部署必须修改成本地ip）
     127.0.0.1 mscloud-config-service
     127.0.0.1 mscloud-eureka-service
     127.0.0.1 mscloud-provider-service
     127.0.0.1 mscloud-consumer-feign-service
     127.0.0.1 mscloud-consumer-ribbon-service
     127.0.0.1 mscloud-zipkin-service
     127.0.0.1 mscloud-admin-service
     127.0.0.1 mscloud-config-service
     127.0.0.1 mscloud-zuul-service
   
   
# 删除mscloud 所有容器
 1. by docker 
  
        docker rm -f $(docker ps  -a --format "table {{.Names}}" | grep mscloud)
    
 2. by docker-compose
    
        docker-compose down
        
 3. by kubernetes
 
        kubectl delete -f kubernetes/01-config.yml
        kubectl delete -f kubernetes/02-eureka.yml
        kubectl delete -f kubernetes/03-provider.yml
        kubectl delete -f kubernetes/04-consumer-feign.yml
        kubectl delete -f kubernetes/05-zuul.yml
        kubectl delete -f kubernetes/06-admin.yml
        kubectl delete -f kubernetes/07-zipkin.yml
        
        或者
        
        kubectl delete -f kubernetes/*.yml

 

 
