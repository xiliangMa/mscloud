# istio 实战 二 bookinfo 部署
 
# 简介
[官方链接](https://istio.io/zh/docs/examples/bookinfo/)

部署一个样例应用，它由四个单独的微服务构成，用来演示多种 Istio 特性。这个应用模仿在线书店的一个分类，显示一本书的信息。页面上会显示一本书的描述，书籍的细节（ISBN、页数等），以及关于这本书的一些评论。

Bookinfo 应用分为四个单独的微服务：

productpage ：productpage 微服务会调用 details 和 reviews 两个微服务，用来生成页面。
details ：这个微服务包含了书籍的信息。
reviews ：这个微服务包含了书籍相关的评论。它还会调用 ratings 微服务。
ratings ：ratings 微服务中包含了由书籍评价组成的评级信息。
reviews 微服务有 3 个版本：

v1 版本不会调用 ratings 服务。
v2 版本会调用 ratings 服务，并使用 1 到 5 个黑色星形图标来显示评分信息。
v3 版本会调用 ratings 服务，并使用 1 到 5 个红色星形图标来显示评分信息。
下图展示了这个应用的端到端架构。

![](https://istio.io/docs/examples/bookinfo/withistio.svg)

-------

# 操作步骤
> 在使用 kubectl apply 进行应用部署的时候，如果目标命名空间已经打上了标签 istio-injection=enabled，Istio sidecar injector 会自动把 Envoy 容器注入到你的应用 Pod 之中。

#### 创建命名空间
```
➜ kubectl create ns bookinfo
```


#### 添加label
- 添加 label:
```
➜ kubectl label ns bookinfo istio-injection=enabled
```

- 查看 label:
```
➜  istio-1.2.4  kubectl describe ns bookinfo
Name:         bookinfo
Labels:       istio-injection=enabled
Annotations:  <none>
Status:       Active
No resource quota.
No resource limits.
```

#### 部署 bookinfo

- 部署:
```
➜ kubectl apply -f samples/bookinfo/platform/kube/bookinfo.yaml -n bookinfo
```

- 获取 pod:
```
➜  istio-1.2.4  kubectl get pod -n bookinfo
NAME                             READY   STATUS    RESTARTS   AGE
details-v1-c5b5f496d-7c6sc       2/2     Running   0          29m
productpage-v1-c7765c886-5j2mr   2/2     Running   0          29m
ratings-v1-f745cf57b-lr5js       2/2     Running   0          29m
reviews-v1-75b979578c-xwv7r      2/2     Running   0          29m
reviews-v2-597bf96c8f-gmtc5      2/2     Running   0          29m
reviews-v3-54c6c64795-xcpz9      2/2     Running   0          29m
```

- 确认app是否正常：
```
➜  istio-1.2.4  kubectl exec -it $(kubectl get pod -l app=ratings -o jsonpath='{.items[0].metadata.name}') -c ratings -- curl productpage:9080/productpage | grep -o "<title>.*</title>"
<title>Simple Bookstore App</title>
```

#### 确认集群是否支持 负载均衡

执行以下命令以确定您的 Kubernetes 集群是否在支持外部负载均衡器的环境中运行；
```
kubectl get svc istio-ingressgateway -n istio-system
```

> 如果 EXTERNAL-IP 有值（IP 地址或主机名），则说明您的环境具有可用于 Ingress 网关的外部负载均衡器。如果 EXTERNAL-IP 值是 <none>（或一直是 <pending> ），则说明可能您的环境并没有为 Ingress 网关提供外部负载均衡器的功能。在这种情况下，您可以使用 Service 的 node port 方式访问网关

*由于使测试环境用的本地k8s集群肯定不支持，本次使用nodeport 方式访问网关，在公有云部署的可以忽略此步骤*

[官方链接](https://istio.io/zh/docs/tasks/traffic-management/ingress/#使用外部负载均衡器时确定-ip-和端口)

#### 设置 istio-ingressgateway service 为 node port 
- 修改 service type
```
➜  kubectl patch service istio-ingressgateway -n istio-system -p '{"spec":{"type":"NodePort"}}'
```

- 查看service：
```
➜  istio-1.2.4  kubectl get svc istio-ingressgateway -n istio-system
NAME                   TYPE       CLUSTER-IP       EXTERNAL-IP   PORT(S)                                                                                                                                      AGE
istio-ingressgateway   NodePort   10.110.171.111   <none>        15020:32681/TCP,80:31380/TCP,443:31390/TCP,31400:31400/TCP,15029:30352/TCP,15030:31338/TCP,15031:31328/TCP,15032:30977/TCP,15443:31688/TCP   23m
```

#### 部署 istio gateway
- 部署:
```
➜ kubectl apply -f samples/bookinfo/networking/bookinfo-gateway.yaml
```

- 查看 gateway：
```
➜  istio-1.2.4  kubectl get gateway -n bookinfo
NAME               AGE
bookinfo-gateway   46s
```

- 设置 INGRESS_PORT:
```
➜  export INGRESS_PORT=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}')
```

- 确认 port
```
➜  istio-1.2.4  echo $INGRESS_PORT
31380
```

- 应用缺省目标规则

```
kubectl apply -f samples/bookinfo/networking/destination-rule-all-mtls.yaml -n bookinfo
```
-------

# 测试
浏览器访问 http://hostip:port/productpage

![](https://github.com/xiliangMa/mscloud/blob/master/images/istio/istio-bookinfo-product.png)


刷新浏览器，看到星级评论发生变化，好了你已经成功部署istio 的demo。



 