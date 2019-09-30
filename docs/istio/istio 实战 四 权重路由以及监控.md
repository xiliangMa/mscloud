# istio 实战 四 权重路由以及监控

# 简介
本任务将演示如何逐步将流量从一个版本的微服务迁移到另一个版本。例如，您可以将流量从旧版本迁移到新版本。

一个常见的用例是将流量从一个版本的微服务逐渐迁移到另一个版本。在 Istio 中，您可以通过配置一系列规则来实现此目标，这些规则将一定百分比的流量路由到一个或另一个服务。在此任务中，您将 50％ 的流量发送到 reviews:v1，另外 50％ 的流量发送到 reviews:v3。然后将 100％ 的流量发送到 reviews:v3 来完成迁移。

图片来自于istio社区:
![](https://github.com/xiliangMa/mscloud/blob/master/images/istio/istio-flow.png)

-------

# 前提
- kubernetes 集群  [基于centos7 搭建 1.14 kubernetes 集群](https://blog.csdn.net/weixin_41806245/article/details/89381752)
-  Istio 1.2.4: [istio 实战 一 Kubernetes 中快速搭建 istio](https://blog.csdn.net/weixin_41806245/article/details/99589663)
-  bookinfo:  [istio 实战 二 bookinfo 部署](https://blog.csdn.net/weixin_41806245/article/details/99589830)

-------

# 任务
将 50% 的流量从 reviews:v1 转移到 reviews:v3

# 操作步骤
#### 1. 流量全部切换到 reviews:v1  版本
```
kubectl apply -f samples/bookinfo/networking/virtual-service-all-v1.yaml -n bookinfo
```
浏览器访问 http://hostip:port/productpage
不管刷新多少次，页面的评论部分都不会显示评级星号,因为 Istio 被配置为将 reviews 服务的的所有流量都路由到了 reviews:v1 版本， 而该版本的服务不会访问带星级的 ratings 服务。

![](https://github.com/xiliangMa/mscloud/blob/master/images/istio/istio-vsr-v1.png)

#### 2. 切换50%流量到 reviews:v3 版本

```
kubectl apply -f samples/bookinfo/networking/virtual-service-reviews-50-v3.yaml -n bookinfo
```

浏览器访问 http://hostip:port/productpage
刷新浏览器中的 /productpage 页面，大约有 50% 的几率会看到页面中出带红色星级的评价内容。这是因为 v3 版本的 reviews 访问了带星级评级的 ratings 服务，但 v1 版本却没有。

![](https://github.com/xiliangMa/mscloud/blob/master/images/istio/istio-vsr-v1.png)
![](https://github.com/xiliangMa/mscloud/blob/master/images/istio/istio-vsr-50-v3.png)


# 通过 Dashboard 监控流量
istio 内置了 Grafana 和 prometheus。
prometheus： 用来收集指标，并在 Prometheus 服务中查询 Istio 指标。
Grafana： 通过 Grafana Dashboard 对服务网格中的流量进行监控。

```
kubectl get pod -n istio-system
```
![](https://github.com/xiliangMa/mscloud/blob/master/images/istio/istio-gp-dashboard.png)

#### 1. 检测 prometheus

```
kubectl -n istio-system get svc prometheus
NAME         TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE
prometheus   ClusterIP   10.111.73.248   <none>        9090/TCP   23h
```

#### 2. 检测 Grafana 

```
kubectl -n istio-system get svc grafana
NAME      TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE
grafana   ClusterIP   10.106.87.165   <none>        3000/TCP   23h
```

#### 3. 添加端口
由于使用的是ClusterIP 类型集群外部无法访问 ，通过端口转发映射本地端口到指定的应用端口.
```
kubectl -n istio-system port-forward $(kubectl -n istio-system get pod -l app=grafana -o jsonpath='{.items[0].metadata.name}') 3000:3000 &
```
在 Web 浏览器中访问 http://localhost:3000/dashboard/db/istio-mesh-dashboard
查看 Istio 仪表盘
![](https://github.com/xiliangMa/mscloud/blob/master/images/istio/istio-grafana-dashboard.png)

浏览器访问 http://hostip:port/productpage
刷新几次， 再次查看 Istio 仪表盘。
![](https://github.com/xiliangMa/mscloud/blob/master/images/istio/istio-grafana-flow.png)


Grafana + prometheus 可以采集各种维度的数据有兴趣的童鞋 可以自己研究、这里不赘述。


