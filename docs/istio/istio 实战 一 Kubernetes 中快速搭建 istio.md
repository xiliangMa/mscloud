# istio 实战 一 Kubernetes 中快速搭建 istio
> 在 Kubernetes 中快速搭建 istio

-------

# 前提
- Kubernetes 集群环境 （[基于centos7 搭建 1.14 kubernetes 集群](https://blog.csdn.net/weixin_41806245/article/details/89381752)）
- Istio 1.2.4 [release](https://github.com/istio/istio/releases)

-------

# 安装步骤
- 下载 Istio 1.2.4

```
curl -L https://git.io/getLatestIstio | ISTIO_VERSION=1.2.4 sh -
```

-------

# 目录简介：
- 在 install/ 目录中包含了 Kubernetes 安装所需的 .yaml 文件
- samples/ 目录中是示例应用
- istioctl 客户端文件保存在 bin/ 目录之中。istioctl 的功能是手工进行 Envoy Sidecar 的注入。
- istio.VERSION 配置文件
这里咱们基本不适用istioctl 操作所以就不配置环境变量了，有需要的自行配置。

-------

# 安装istio
#### 安装istio crd

```
for i in install/kubernetes/helm/istio-init/files/crd*yaml; do kubectl apply -f $i; done
```


```
kubectl get crd | grep istio
adapters.config.istio.io               2019-08-14T02:35:08Z
attributemanifests.config.istio.io     2019-08-14T02:35:08Z
authorizationpolicies.rbac.istio.io    2019-08-14T02:35:09Z
clusterrbacconfigs.rbac.istio.io       2019-08-14T02:35:08Z
destinationrules.networking.istio.io   2019-08-14T02:35:08Z
envoyfilters.networking.istio.io       2019-08-14T02:35:08Z
gateways.networking.istio.io           2019-08-14T02:35:08Z
handlers.config.istio.io               2019-08-14T02:35:08Z
httpapispecbindings.config.istio.io    2019-08-14T02:35:08Z
httpapispecs.config.istio.io           2019-08-14T02:35:08Z
instances.config.istio.io              2019-08-14T02:35:08Z
meshpolicies.authentication.istio.io   2019-08-14T02:35:08Z
policies.authentication.istio.io       2019-08-14T02:35:08Z
quotaspecbindings.config.istio.io      2019-08-14T02:35:08Z
quotaspecs.config.istio.io             2019-08-14T02:35:08Z
rbacconfigs.rbac.istio.io              2019-08-14T02:35:08Z
rules.config.istio.io                  2019-08-14T02:35:08Z
serviceentries.networking.istio.io     2019-08-14T02:35:08Z
servicerolebindings.rbac.istio.io      2019-08-14T02:35:08Z
serviceroles.rbac.istio.io             2019-08-14T02:35:08Z
sidecars.networking.istio.io           2019-08-14T02:35:09Z
templates.config.istio.io              2019-08-14T02:35:08Z
virtualservices.networking.istio.io    2019-08-14T02:35:08Z
```


-------

#### 部署 istio 控制器
```
kubectl apply -f install/kubernetes/istio-demo.yaml 
```

-------

#### 确认 istio-system pod 、svc 状态 :

```
kubectl get pod,svc -n istio-system
NAME                                          READY   STATUS      RESTARTS   AGE
pod/grafana-6fb9f8c5c7-l22ss                  1/1     Running     0          14m
pod/istio-citadel-66866dfc58-fq277            1/1     Running     0          14m
pod/istio-cleanup-secrets-1.2.4-r6vh7         0/1     Completed   0          14m
pod/istio-egressgateway-6cdc5584cb-krd9b      1/1     Running     0          14m
pod/istio-galley-b88497745-k6ttz              1/1     Running     0          14m
pod/istio-grafana-post-install-1.2.4-nknk2    0/1     Completed   0          14m
pod/istio-ingressgateway-86c7bf8ccb-k2cn8     1/1     Running     0          14m
pod/istio-pilot-5b989fc9f9-cvkq8              2/2     Running     0          14m
pod/istio-policy-7db84b89dc-hp8qq             2/2     Running     5          14m
pod/istio-security-post-install-1.2.4-k2bn4   0/1     Completed   0          14m
pod/istio-sidecar-injector-785d58b878-z57fc   1/1     Running     0          14m
pod/istio-telemetry-5c8f77b54b-rljzg          2/2     Running     5          14m
pod/istio-tracing-5d8f57c8ff-jzkph            1/1     Running     0          14m
pod/kiali-7d749f9dcb-vdkmg                    1/1     Running     0          14m
pod/prometheus-776fdf7479-8dqjj               1/1     Running     0          14m

NAME                             TYPE           CLUSTER-IP       EXTERNAL-IP   PORT(S)                                                                                                                                      AGE
service/grafana                  ClusterIP      10.104.191.184   <none>        3000/TCP                                                                                                                                     14m
service/istio-citadel            ClusterIP      10.106.94.115    <none>        8060/TCP,15014/TCP                                                                                                                           14m
service/istio-egressgateway      ClusterIP      10.98.134.100    <none>        80/TCP,443/TCP,15443/TCP                                                                                                                     14m
service/istio-galley             ClusterIP      10.105.55.99     <none>        443/TCP,15014/TCP,9901/TCP                                                                                                                   14m
service/istio-ingressgateway     LoadBalancer   10.109.252.32    localhost     15020:31452/TCP,80:31380/TCP,443:31390/TCP,31400:31400/TCP,15029:30072/TCP,15030:32368/TCP,15031:31526/TCP,15032:30163/TCP,15443:30970/TCP   14m
service/istio-pilot              ClusterIP      10.111.85.39     <none>        15010/TCP,15011/TCP,8080/TCP,15014/TCP                                                                                                       14m
service/istio-policy             ClusterIP      10.107.157.107   <none>        9091/TCP,15004/TCP,15014/TCP                                                                                                                 14m
service/istio-sidecar-injector   ClusterIP      10.102.242.215   <none>        443/TCP                                                                                                                                      14m
service/istio-telemetry          ClusterIP      10.105.124.34    <none>        9091/TCP,15004/TCP,15014/TCP,42422/TCP                                                                                                       14m
service/jaeger-agent             ClusterIP      None             <none>        5775/UDP,6831/UDP,6832/UDP                                                                                                                   14m
service/jaeger-collector         ClusterIP      10.102.210.7     <none>        14267/TCP,14268/TCP                                                                                                                          14m
service/jaeger-query             ClusterIP      10.105.80.14     <none>        16686/TCP                                                                                                                                    14m
service/kiali                    ClusterIP      10.104.104.222   <none>        20001/TCP                                                                                                                                    14m
service/prometheus               ClusterIP      10.101.248.36    <none>        9090/TCP                                                                                                                                     14m
service/tracing                  ClusterIP      10.107.167.126   <none>        80/TCP                                                                                                                                       14m
service/zipkin                   ClusterIP      10.105.243.186   <none>        9411/TCP
```
细心等待需要拉取镜像。。。。


