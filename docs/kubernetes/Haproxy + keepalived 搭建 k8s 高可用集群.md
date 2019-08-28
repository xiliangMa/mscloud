# 概述
教你快速搭建 生产环境下的 kubernetes 高可用集群。

本文通过 HAProxy + Keepalived 实现。

**Keepalived:** 提供对外服务的虚拟IP（VIP） 是一主多备运行模式，故至少需要两个 LB 节点。在运行过程中周期检查本机的 HAProxy 进程状态，如果检测到 HAProxy 进程异常，则触发重新选主的过程，VIP 将飘移到新选出来的主节点，从而实现 VIP 的高可用。

**HAProxy:** 监听 Keepalived VIP，运行 Keepalived 和 HAProxy 的节点称为 LB（负载均衡）节点。

---

# 节点配置

主机名 | ip | 系统 | 角色 | 磁盘 | CPU/MEM
---|---|---|---|---|---
master1.k8s.com | 192.168.8.181 | Centos7.6 | master | 40G | 4核/4G
master2.k8s.com | 192.168.8.182 | Centos7.6 | master | 40G | 4核/4G
master3.k8s.com | 192.168.8.183 | Centos7.6 | master | 40G | 4核/4G
node1.k8s.com | 192.168.8.191 | Centos7.6 | node | 40G | 4核/4G
node2.k8s.com | 192.168.8.192 | Centos7.6 | node | 40G | 4核/4G
 VIP | 192.168.8.10 | -- | -- | -- | --


---

# 环境
- kubernetes v1.14.0
- kebeadm
- haproxy-k8s
- keepalived-k8s
- docker 1.13.1


---

# 准备工作（所有节点）
- 配置hosts 解析（按照上面表格操作即可）
- 关闭交换分区
- 关闭防火墙
- 关闭selinux
- 更新centos repo
- 安装docker
- 配置内核参数
- 下载kubernetes镜像

上面的步骤可以查看之前的文章：[Centos7 快速搭建Kubernetes 集群](https://blog.csdn.net/weixin_41806245/article/details/89381752)，按照步骤一步步操作即可。


---

# ipvs 设置 （所有节点）
1. 安装 ipset ipvsadm

```
yum install install -y ipset ipvsadm
```

2. 配置加载 ipvs 模块

这里开启是一次性的重启后失效。
```
modprobe -- ip_vs
modprobe -- ip_vs_rr
modprobe -- ip_vs_wrr
modprobe -- ip_vs_sh
modprobe -- nf_conntrack_ipv4
```

3. 检查是否配置成功

```
[root@master ~]# lsmod | grep -e ip_vs -e nf_conntrack_ipv4
ip_vs_sh               12688  0 
ip_vs_wrr              12697  0 
ip_vs_rr               12600  0 
ip_vs                 145497  6 ip_vs_rr,ip_vs_sh,ip_vs_wrr
nf_conntrack_ipv4      15053  3 
nf_defrag_ipv4         12729  1 nf_conntrack_ipv4
nf_conntrack          133095  6 ip_vs,nf_nat,nf_nat_ipv4,xt_conntrack,nf_nat_masquerade_ipv4,nf_conntrack_ipv4
libcrc32c              12644  4 xfs,ip_vs,nf_nat,nf_conntrack
```

---

# 配置 HAProxy （所有 Master 节点）
1. 准备 haproxy-start.sh

```
#!/bin/bash
# -----------------修改 Master 地址
MasterIP1=192.168.8.181
MasterIP2=192.168.8.182
MasterIP3=192.168.141.183

# ----------------- kube-apiserver 默认端口 6443 不需要修改
MasterPort=6443
HaproxyPort=6444

# 启动
docker run -d --restart=always --name=HAProxy -p $HaproxyPort:$HaproxyPort \
        -e MasterIP1=$MasterIP1 \
        -e MasterIP2=$MasterIP2 \
        -e MasterIP3=$MasterIP3 \
        -e MasterPort=$MasterPort \
        wise2c/haproxy-k8s
```
2. 添加权限

```
chmod +x haproxy-start.sh
```
3. 初始化 haproxy

```
初始化
./haproxy-start.sh

查看：
[root@master1 k8s]# docker ps
CONTAINER ID        IMAGE                       COMMAND                  CREATED              STATUS              PORTS                    NAMES
4024d285442c        wise2c/haproxy-k8s   "/docker-entrypoin..."   About a minute ago   Up About a minute   0.0.0.0:6444->6444/tcp   HAProxy
```

---

# 配置 Keepalived （所有 Master 节点）
1. 准备 keepalived-start.sh 脚本

```
#!/bin/bash
# ----------------- 修改虚拟 IP 地址
VIRTUAL_IP=192.168.8.10
# ----------------- 网卡名
INTERFACE=ens33
# ----------------- 子网掩码
NETMASK_BIT=24
# ----------------- HAProxy 暴露端口，内部指向 kube-apiserver 的 6443 端口
CHECK_PORT=6444
# ----------------- 路由标识符
RID=10
# ----------------- 虚拟路由标识符
VRID=160
# ----------------- IPV4 多播地址，默认 224.0.0.18
MCAST_GROUP=224.0.0.18

docker run -itd --restart=always --name=Keepalived \
        --net=host --cap-add=NET_ADMIN \
        -e VIRTUAL_IP=$VIRTUAL_IP \
        -e INTERFACE=$INTERFACE \
        -e CHECK_PORT=$CHECK_PORT \
        -e RID=$RID \
        -e VRID=$VRID \
        -e NETMASK_BIT=$NETMASK_BIT \
        -e MCAST_GROUP=$MCAST_GROUP \
        wise2c/keepalived-k8s
```

2. 添加权限

```
chmod +x keepalived-start.sh
```

3. 初始化 keepalived

```
启动：
./keepalived-start.sh

查看：
[root@master1 k8s]# docker ps
CONTAINER ID        IMAGE                   COMMAND                  CREATED             STATUS              PORTS                    NAMES
bd211be2184f        wise2c/keepalived-k8s   "/usr/bin/keepaliv..."   2 seconds ago       Up 2 seconds                                 Keepalived
946700915a01        wise2c/haproxy-k8s      "/docker-entrypoin..."   6 seconds ago       Up 6 seconds        0.0.0.0:6444->6444/tcp   HAProxy
```

4. 查看 VIP 是否绑定成功
网卡根据自己指定的查看：

```
[root@master1 k8s]# ip a| grep ens33
2: ens33: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc pfifo_fast state UP group default qlen 1000
    inet 192.168.8.181/24 brd 192.168.8.255 scope global noprefixroute ens33
    inet 192.168.8.10/24 scope global secondary ens33
```

---

# 初始化 Master 节点（Mster1 节点操作）
1. 通过kubeadm 导出 默认配置

```
kubeadm config print init-defaults --kubeconfig ClusterConfiguration > kubeadm.yml
```
2. 修改配置
按照注释部分修改即可

```
apiVersion: kubeadm.k8s.io/v1beta1
bootstrapTokens:
- groups:
  - system:bootstrappers:kubeadm:default-node-token
  token: abcdef.0123456789abcdef
  ttl: 24h0m0s
  usages:
  - signing
  - authentication
kind: InitConfiguration
localAPIEndpoint:
  # -------- 修改为 当前Master 节点ip
  advertiseAddress: 192.168.8.181
  bindPort: 6443
nodeRegistration:
  criSocket: /var/run/dockershim.sock
  # 当前master节点
  name: master1.k8s.com
  taints:
  - effect: NoSchedule
    key: node-role.kubernetes.io/master
---
apiServer:
  timeoutForControlPlane: 4m0s
apiVersion: kubeadm.k8s.io/v1beta1
certificatesDir: /etc/kubernetes/pki
clusterName: kubernetes
# -------- 修改为 VIP
controlPlaneEndpoint: "192.168.8.10:6444"
controllerManager: {}
dns:
  type: CoreDNS
etcd:
  local:
    dataDir: /var/lib/etcd
# 这里的仓库地址可以不修改，按照前面的文章已经可以下载k8s相关镜像
imageRepository: k8s.gcr.io
kind: ClusterConfiguration
kubernetesVersion: v1.14.0
networking:
  dnsDomain: cluster.local
  # -------- 这里使用的是flannel 网络，默认网段
  podSubnet: 10.244.0.0/16
scheduler: {}
---
# -------- 开启 ipvs
apiVersion: kubeproxy.config.k8s.io/v1alpha1
kind: KubeProxyConfiguration
featureGates:
  SupportIPVSProxyMode: true
mode: ipvs

```

3. 初始化集群

```
kubeadm  init --config=kubeadm.yml --experimental-upload-certs
```

结果如下：

```
。。。。省略。。。。
。。。。。。。。。。
Your Kubernetes control-plane has initialized successfully!

To start using your cluster, you need to run the following as a regular user:

  mkdir -p $HOME/.kube
  sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
  sudo chown $(id -u):$(id -g) $HOME/.kube/config

You should now deploy a pod network to the cluster.
Run "kubectl apply -f [podnetwork].yaml" with one of the options listed at:
  https://kubernetes.io/docs/concepts/cluster-administration/addons/

You can now join any number of the control-plane node running the following command on each as root:

  kubeadm join 192.168.8.10:6444 --token abcdef.0123456789abcdef \
    --discovery-token-ca-cert-hash sha256:08cfe73c0d333ccdc9b94f8cf2795809b5308b1805413f332929cb0854d94c4e \
    --experimental-control-plane --certificate-key a5dd02d91627bc2218b2cc3ffbee3406571e01543dd96d9c7f1202a96f41e052

Please note that the certificate-key gives access to cluster sensitive data, keep it secret!
As a safeguard, uploaded-certs will be deleted in two hours; If necessary, you can use 
"kubeadm init phase upload-certs --experimental-upload-certs" to reload certs afterward.

Then you can join any number of worker nodes by running the following on each as root:

kubeadm join 192.168.8.10:6444 --token abcdef.0123456789abcdef \
    --discovery-token-ca-cert-hash sha256:08cfe73c0d333ccdc9b94f8cf2795809b5308b1805413f332929cb0854d94c4e

```

如果出错了重置即可

```
kubeadm reset
```

4. 配置 kubectl

```
mkdir -p $HOME/.kube
  sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
  sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

5. 查看节点

```
[root@master1 k8s]# kubectl  get node
NAME              STATUS     ROLES    AGE     VERSION
master1.k8s.com   NotReady   master   9m25s   v1.14.0
```


6. 安装 flannel 网络插件

```
kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml
```

---

# 加入 Master 节点（Master2 和 Master3）
```
kubeadm join 192.168.8.10:6444 --token abcdef.0123456789abcdef \
    --discovery-token-ca-cert-hash sha256:893c45bed3210b8b787084cb6467feb93235cb6765cbd186124c4c4e73c9b3bc \
    --experimental-control-plane --certificate-key b11dd02f03926a70ba7632607a386adfc89948436a0d67927d447c2c12824c4b
```


---

# 加入Node 节点 （所有的 Node 节点）

```
kubeadm join 192.168.8.10:6444 --token abcdef.0123456789abcdef \
    --discovery-token-ca-cert-hash sha256:893c45bed3210b8b787084cb6467feb93235cb6765cbd186124c4c4e73c9b3bc
```


---

# 测试集群
查看主机

```
[root@master1 ~]# kubectl  get node
NAME              STATUS     ROLES    AGE   VERSION
master1.k8s.com   Ready      master   45m   v1.14.0
master1.k8s.com   Ready      master   55m   v1.14.0
master1.k8s.com   Ready      master   60m   v1.14.0
node1.k8s.com     Ready      <none>   70m   v1.14.0
node2.k8s.com     Ready      <none>   80m   v1.14.0
```

测试集群高可用：
reboot Master节点 或者 重启 HAProxy 容器这样会导致VIP 漂移到其他的Master 从而达到高可用的作用

重启Master1:

```
reboot
```
查看 Master2 或者 Master3 的网卡是否已经已经有虚拟网卡的信息


```
master2 已经成为VIP所在的节点：
[root@master2 ~]# ip a | grep ens33
2: ens33: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc pfifo_fast state UP group default qlen 1000
    inet 192.168.8.182/24 brd 192.168.8.255 scope global noprefixroute ens33
    inet 192.168.8.10/24 scope global secondary ens33
```





