# istio 实战 六 全链路监控 - Jaeger

# 简介
做过sping-cloud微服务以及其他微服务的童鞋对此应该很清楚，那么istio 怎么实现的呢？
istio 支持 Jaeger、Zipkin、LightStep 三种，之前spring-cloud 系列文章中有讲到
[ Spring-Cloud 实战 七 全链路监控 zipkin](https://blog.csdn.net/weixin_41806245/article/details/98041920)
这次我们通过 Jaeger 来认识 istio 中的全链路监控，我们现在正在使用的也是 Jaeger。

-------

# 前提
- kubernetes 集群  [基于centos7 搭建 1.14 kubernetes 集群](https://blog.csdn.net/weixin_41806245/article/details/89381752)
-  Istio 1.2.4: [istio 实战 一 Kubernetes 中快速搭建 istio](https://blog.csdn.net/weixin_41806245/article/details/99589663)
-  bookinfo:  [istio 实战 二 bookinfo 部署](https://blog.csdn.net/weixin_41806245/article/details/99589830)

# 操作步骤
#### 1. 安装jaeger
```
kubectl create -f https://raw.githubusercontent.com/jaegertracing/jaeger-kubernetes/master/all-in-one/jaeger-all-in-one-template.yml -n istio-system
```

#### 2. 如要配置到追踪仪表盘的访问，请使用端口转发
```
kubectl -n istio-system port-forward $(kubectl -n istio-system get pod -l app=jaeger -o jsonpath='{.items[0].metadata.name}') 16686:16686 &
```

![](https://github.com/xiliangMa/mscloud/blob/master/images/istio/istio-jaeger.png)

#### 3. 测试
持续放送请求
```
watch -n 1 curl -o /dev/null -s -w %{http_code} $GATEWAY_URL/productpage
```
可以看到每个服务请求的链路

Jaeger、Zipkin、LightStep 基本都一样有兴趣的可以自己搭建。