# 简介
上文介绍到 [istio bookinfo](https://blog.csdn.net/weixin_41806245/article/details/99589830)，浏览器访问 product 服务，界面五角星颜色发生变化， 这是通过service 访问 details、reviews 服务以及不同的版实现。

如何通过istio智能路由访问details、review是服务的某个版本，使用 Bookinfo 示例应用，演示运行中的应用进行请求路由的动态配置以及故障注入的方法。

-------

# 前提
- kubernetes 集群  [基于centos7 搭建 1.14 kubernetes 集群](https://blog.csdn.net/weixin_41806245/article/details/89381752)
-  Istio 1.2.4: [istio 实战 一 Kubernetes 中快速搭建 istio](https://blog.csdn.net/weixin_41806245/article/details/99589663)
-  bookinfo:  [istio 实战 二 bookinfo 部署](https://blog.csdn.net/weixin_41806245/article/details/99589830)

-------

# istio 组件
- 数据面 
    - envoy
- 控制面板
    - Pilot：服务发现、流量管理
    - Mixer：访问控制、遥测
    - Citadel：终端用户认证、流量加密


-------

# istio 管理基础概念

- Gateway： 类似于k8s ingress， HTTP/TCP 流量配置负载均衡器，流量入口
- VirtualService： 定义路由规则，控制流量路由到服务上的各种行为
- DestinationRule： 在 VirtualService 路由生效后，配置应用与请求的策略集
- ServiceEntry： 网格内部请求外部服务的策略

-------

# 任务
![](https://istio.io/docs/examples/bookinfo/withistio.svg)

- 1. 流量全部导向 reviews 服务的 v1 版本。（reviews:v1 不包含星级评分功能的版本）
- 2. 特定用户的请求发送给 v2 版本，其他用户则不受影响（reviews:v2 是包含星级评分功能的版本）

-------

# 任务一操作步骤
#### 1.1 yaml 解析
virtual-service-all-v1.yaml 

```
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: productpage
spec:
  hosts:
  - productpage
  http:
  - route:
    - destination:
        host: productpage
        subset: v1
---
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: reviews
spec:
  hosts:
  - reviews
  http:
  - route:
    - destination:
        host: reviews
        subset: v1
---
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: ratings
spec:
  hosts:
  - ratings
  http:
  - route:
    - destination:
        host: ratings
        subset: v1
---
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: details
spec:
  hosts:
  - details
  http:
  - route:
    - destination:
        host: details
        subset: v1
---

```

- 该配置中流量的目标主机是 reviews，如果该服务和规则部署在 Kubernetes 的 default namespace 下的话，对应于 Kubernetes 中的服务的 DNS 名称就是 reviews.default.svc.cluster.local。
- 我们在 hosts 配置了服务的名字只是表示该配置是针对 reviews.default.svc.cluster.local 的服务的路由规则，但是具体将对该服务的访问的流量路由到哪些服务的哪些实例上，就是要通过 destination 的配置了。
- 我们看到上面的 VirtualService 的 HTTP 路由中还定义了一个 destination。destination 用于定义在网络中可寻址的服务，请求或连接在经过路由规则的处理之后，就会被发送给 destination。destination.host 应该明确指向服务注册表中的一个服务。Istio 的服务注册表除包含平台服务注册表中的所有服务（例如 Kubernetes 服务、Consul 服务）之外，还包含了 ServiceEntry 资源所定义的服务。VirtualService 中只定义流量发送给哪个服务的路由规则，但是并不知道要发送的服务的地址是什么，这就需要 DestinationRule 来定义了。
- subset 配置流量目的地的子集，下文会讲到。VirtualService 中其实可以除了 hosts 字段外其他什么都不配置，路由规则可以在 DestinationRule 中单独配置来覆盖此处的默认规则。
#### 1.2 应用 virtual service

```
kubectl apply -f samples/bookinfo/networking/virtual-service-all-v1.yaml
```

#### 1.3 测试
浏览器中打开http://ingressip:port/productpage 刷新
v1版本的服务不访问星级评分服务，无论您刷新多少次，页面的评论部分都不会显示评级星标。
此时已经成功的将所有流量路由到版本 reviews:v1。

-------

# 任务二 操作步骤
#### 2.1 yaml解析
virtual-service-reviews-test-v2.yaml 主要增加了用户检测
```
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: reviews
spec:
  hosts:
    - reviews
  http:
  - match:
    - headers:
        end-user:
          exact: jason
    route:
    - destination:
        host: reviews
        subset: v2
  - route:
    - destination:
        host: reviews
        subset: v1
```

#### 2.2 应用 virtual service

```
kubectl apply -f samples/bookinfo/networking/virtual-service-reviews-test-v2.yaml
```


#### 2.3 测试
- 在 Bookinfo 应用程序的 /productpage 上，以用户 jason 身份登录。
刷新浏览器。你看到了什么？星级评分显示在每个评论旁边。
- 以其他用户身份登录（选择您想要的任何名称）。
刷新浏览器。现在星星消失了。这是因为除了 Jason 之外，所有用户的流量都被路由到 reviews:v1。


您已成功配置 Istio 以根据用户身份路由流量。