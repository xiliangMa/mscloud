# istio 实战 五 网格可视化 - kiali

# 简介
kiali 为我们提供了查看相关服务与配置提供了统一化的可视化界面，并且能在其中展示他们的关联；同时他还提供了界面让我们可以很方便的验证 istio 配置与错误提示;

-------

# 前提
- kubernetes 集群  [基于centos7 搭建 1.14 kubernetes 集群](https://blog.csdn.net/weixin_41806245/article/details/89381752)
-  Istio 1.2.4: [istio 实战 一 Kubernetes 中快速搭建 istio](https://blog.csdn.net/weixin_41806245/article/details/99589663)
-  bookinfo:  [istio 实战 二 bookinfo 部署](https://blog.csdn.net/weixin_41806245/article/details/99589830)

-------

# 操作步骤
安装istio时候内置了kiali
注意： 因为么有负载均衡器，下面的操作步骤都基于nodeport的模式，公有云部署并且有lb的忽略。[官网设置链接](https://istio.io/zh/docs/tasks/traffic-management/ingress/#使用外部负载均衡器时确定-ip-和端口)

之前已经做过的可以省略 第 2 步。
##### 1. 检测服务是否正常
```
kubectl -n istio-system get svc kiali
NAME    TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)     AGE
kiali   ClusterIP   10.110.143.163   <none>        20001/TCP   25h
```

#### 2. 设置INGRESS_HOST
设置INGRESS_PORT：
```
export INGRESS_PORT=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}')
```

设置 INGRESS_HOST：

```
export INGRESS_HOST=127.0.0.1
```

设置GATEWAY_URL:
```
export GATEWAY_URL=$INGRESS_HOST:$INGRESS_PORT
```

#### 3. 映射 kiali pod 端口

```
kubectl -n istio-system port-forward $(kubectl -n istio-system get pod -l app=kiali -o jsonpath='{.items[0].metadata.name}') 20001:20001 &
```

####  4. 持续发送请求
如果系统中安装了 watch 命令，就可以用它来持续发送请求

```
watch -n 1 curl -o /dev/null -s -w %{http_code} $GATEWAY_URL/productpage
```
#### 5. 测试
浏览器打开kiali地址 http://nodeip:20001 

截图能看到整体的微服务结构、请求的流量等。
 kiali 还有很多强大功能 
 1. 可视化配置istio config virtualservice、destination 等规则 
 2. 可视化日志查看
 3. 资源监控
 4. service、pod application 等监控
 等等 有兴趣的可以自己研究kiali

![](https://github.com/xiliangMa/mscloud/blob/master/images/istio/istio-kiali-bookinfo-graph.png)
