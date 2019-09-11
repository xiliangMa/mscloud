# kubernetes 实战 基于 rook 搭建 ceph 分布式存储
# 概述
前文介绍了 [kubernetes 实战 使用 nfs 作为动态 storageClass 存储](https://blog.csdn.net/weixin_41806245/article/details/100657034)，本文介绍 kubernetes 集群 使用 rook ceph 作为动态 storageClass 存储。ceph 搭建对于没有基础的童鞋来说还是比较困难的，之前做虚拟化时搞过几次，基本都是专业高存储的操作。现在 出现了rook帮助我们简化了这些过程，本文通过 rook 快速搭建ceph分布式存储。

> **ceph 简介：**
Ceph 是一个开源的分布式存储系统，包括对象存储、块设备、文件系统。它具有高可靠性、安装方便、管理简便、能够轻松管理海量数据。Ceph 存储集群具备了企业级存储的能力，它通过组织大量节点，节点之间靠相互通讯来复制数据、并动态地重分布数据，从而达到高可用分布式存储功能

> **rook 简介：**
Rook 是专用于 Cloud-Native 环境的文件、块、对象存储服务。它实现了一个自动管理的、自动扩容的、自动修复的分布式存储服务。Rook 支持自动部署、启动、配置、分配、扩容/缩容、升级、迁移、灾难恢复、监控以及资源管理。为了实现所有这些功能，Rook 需要依赖底层的容器编排平台，例如 kubernetes、CoreOS 等。Rook 目前支持 Ceph、NFS、Minio Object Store、Edegefs、Cassandra、CockroachDB 存储的搭建，后期会支持更多存储方案。

[rook github 地址]( https://github.com/rook/rook)

**Rook与Kubernetes结合的架构图如下：**
![](https://github.com/xiliangMa/mscloud/blob/master/images/kubernetes/rook-architecture.png)

> Rook的主要组件有两个，功能如下：
**Rook Operator:**
Rook与Kubernetes交互的组件
整个Rook集群只有一个

> **Rook Agent:**
与Rook Operator交互，执行命令
每个Kubernetes的Node上都会启动一个
不同的存储系统，启动的Agent是不同的

# 环境
- [kubernetes 集群 ](https://blog.csdn.net/weixin_41806245/article/details/89381752) or  [高可用集群](https://blog.csdn.net/weixin_41806245/article/details/100115760)
- rook release-1.1

# 部署 rook operator
[rook 官方操作步骤](https://github.com/rook/rook/blob/master/Documentation/ceph-quickstart.md)， 可以通过[helm chart ](https://github.com/rook/rook/blob/master/Documentation/helm-operator.md)方式部署 rook operator，这里不再赘述。

**获取 rook 源码, 切换为release-1.1**

```
git clone https://github.com/rook/rook.git
git checkout  -b release-1.1 origin/release-1.1
```

**部署 operator**

```
cd cluster/examples/kubernetes/ceph
kubectl create -f common.yaml
kubectl create -f operator.yaml
```

**验证 rook-ceph-operator 是否部署成功**

```
[root@node3 ceph]# kubectl -n rook-ceph get pod
NAME                                           READY   STATUS      RESTARTS   AGE
csi-cephfsplugin-44269                         3/3     Running     3          5h34m
csi-cephfsplugin-provisioner-b66d48bc8-6f6wl   4/4     Running     4          5h34m
csi-cephfsplugin-provisioner-b66d48bc8-c89dj   4/4     Running     4          5h34m
csi-rbdplugin-9hgbm                            3/3     Running     3          5h34m
csi-rbdplugin-provisioner-95dd85d6-k74pk       5/5     Running     17         5h34m
csi-rbdplugin-provisioner-95dd85d6-sspfb       5/5     Running     19         5h34m
rook-ceph-agent-f9kpk                          1/1     Running     1          5h34m
rook-ceph-mgr-a-85dbb977cd-5hxw9               1/1     Running     0          119m
rook-ceph-mon-a-756c9d7d7f-djc4s               1/1     Running     0          120m
rook-ceph-operator-6f556bcbff-qjr7h            1/1     Running     20         5h49m
rook-ceph-osd-0-54bb64bdcb-pjghp               1/1     Running     0          118m
rook-ceph-osd-prepare-node3-4k5pp              0/1     Completed   0          119m
rook-ceph-tools-868c87d5b9-rbgqt               1/1     Running     0          68m
rook-discover-thgf4
```

# 部署 ceph cluster
**cluster-test.yaml 配置如下：**

```
apiVersion: ceph.rook.io/v1
kind: CephCluster
metadata:
  name: rook-ceph
  namespace: rook-ceph
spec:
  cephVersion:
    # For the latest ceph images, see https://hub.docker.com/r/ceph/ceph/tags
    image: ceph/ceph:v14.2.3-20190904
  dataDirHostPath: /var/lib/rook
  mon:
    count: 3
  dashboard:
    enabled: true
  storage:
    useAllNodes: true
    useAllDevices: false
    # Important: Directories should only be used in pre-production environments
    directories:
    - path: /var/lib/rook
```

**部署：**

```
kubectl apply -f cluster-test.yaml
```

# 部署  Ceph Dashboard
** 开启 dashboard **
> 上一步操作已经部署ceph cluster，确保 cluster-test.yaml 中 dashboard 开启 ， dashboard 就会自动创建。

```
dashboard:
    enabled: true
```

**确认是否创建成功**

```
[root@node3 ceph]# kubectl get svc -n rook-ceph
NAME                                     TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)             AGE
csi-cephfsplugin-metrics                 ClusterIP   10.43.187.98   <none>        8080/TCP,8081/TCP   5h49m
csi-rbdplugin-metrics                    ClusterIP   10.43.120.23   <none>        8080/TCP,8081/TCP   5h49m
rook-ceph-mgr                            ClusterIP   10.43.93.116   <none>        9283/TCP            133m
rook-ceph-mgr-dashboard                  ClusterIP   10.43.90.79    <none>        8443/TCP            133m
rook-ceph-mgr-dashboard-external-https   ClusterIP    10.43.230.64   <none>        8443/TCP      113m
rook-ceph-mon-a                          ClusterIP   10.43.104.11   <none>        6789/TCP,3300/TCP   4h9m
```

修改 service类型为NodePort：
> service 默认类型为NodePort 只能在集群内部访问，将类型修改为 NodePort。
修改 dashboard-external-https.yaml

```
apiVersion: v1
kind: Service
metadata:
  name: rook-ceph-mgr-dashboard-external-https
  namespace: rook-ceph
  labels:
    app: rook-ceph-mgr
    rook_cluster: rook-ceph
spec:
  ports:
  - name: dashboard
    port: 8443
    protocol: TCP
    targetPort: 8443
    nodePort: 30007 # 固定端口访问
  selector:
    app: rook-ceph-mgr
    rook_cluster: rook-ceph
  sessionAffinity: None
  type: NodePort # 修改类型
```

**确认 service 类型修改成功：**

```
[root@node3 ceph]# kubectl get svc -n rook-ceph
NAME                                     TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)             AGE
csi-cephfsplugin-metrics                 ClusterIP   10.43.187.98   <none>        8080/TCP,8081/TCP   5h49m
csi-rbdplugin-metrics                    ClusterIP   10.43.120.23   <none>        8080/TCP,8081/TCP   5h49m
rook-ceph-mgr                            ClusterIP   10.43.93.116   <none>        9283/TCP            133m
rook-ceph-mgr-dashboard                  ClusterIP   10.43.90.79    <none>        8443/TCP            133m
rook-ceph-mgr-dashboard-external-https   NodePort    10.43.230.64   <none>        8443:30007/TCP      113m
rook-ceph-mon-a                          ClusterIP   10.43.104.11   <none>        6789/TCP,3300/TCP   4h9m
```

**测试dashboard是否可以访问：**

[](https://github.com/xiliangMa/mscloud/blob/master/images/kubernetes/ceph-login.png)

**获取密码:**
用户名 admin
```
kubectl -n rook-ceph get secret rook-ceph-dashboard-password -o jsonpath="{['data']['password']}" | base64 --decode && echo
```

**首页：**
![](https://github.com/xiliangMa/mscloud/blob/master/images/kubernetes/ceph-index.png)


# 部署  Rook toolbox  测试

```
kubectl apply -f toolbox.yaml
```

**验证是否部署成功：**
注意本次演示是单节点的kubernetes 集群。
```
[root@node3 ceph]# kubectl -n rook-ceph get pod -l "app=rook-ceph-tools"
NAME                               READY   STATUS    RESTARTS   AGE
rook-ceph-tools-868c87d5b9-rbgqt   1/1     Running   0          95m
```

**进入pod：**
错误提示不用care
```
[root@node3 ceph]# kubectl -n rook-ceph exec -it $(kubectl -n rook-ceph get pod -l "app=rook-ceph-tools" -o jsonpath='{.items[0].metadata.name}') bash
bash: warning: setlocale: LC_CTYPE: cannot change locale (en_US.UTF-8): No such file or directory
bash: warning: setlocale: LC_COLLATE: cannot change locale (en_US.UTF-8): No such file or directory
bash: warning: setlocale: LC_MESSAGES: cannot change locale (en_US.UTF-8): No such file or directory
bash: warning: setlocale: LC_NUMERIC: cannot change locale (en_US.UTF-8): No such file or directory
bash: warning: setlocale: LC_TIME: cannot change locale (en_US.UTF-8): No such file or directory
```

**测试：**

```
[root@node3 /]# ceph status
  cluster:
    id:     d693bbc6-5c4e-432c-b3ff-1e707cfdbb51
    health: HEALTH_WARN
            Reduced data availability: 5 pgs inactive
            Degraded data redundancy: 5 pgs undersized
            too few PGs per OSD (5 < min 30)

  services:
    mon: 1 daemons, quorum a (age 2h)
    mgr: a(active, since 2h)
    osd: 1 osds: 1 up (since 2h), 1 in (since 2h)

  data:
    pools:   1 pools, 5 pgs
    objects: 0 objects, 0 B
    usage:   14 GiB used, 21 GiB / 35 GiB avail
    pgs:     100.000% pgs not active
             5 undersized+peered


[root@node3 /]# ceph osd status
+----+-------+-------+-------+--------+---------+--------+---------+-----------+
| id |  host |  used | avail | wr ops | wr data | rd ops | rd data |   state   |
+----+-------+-------+-------+--------+---------+--------+---------+-----------+
| 0  | node3 | 14.3G | 20.7G |    0   |     0   |    0   |     0   | exists,up |
+----+-------+-------+-------+--------+---------+--------+---------+-----------+

[[root@node3 /]# ceph df
RAW STORAGE:
    CLASS     SIZE       AVAIL      USED       RAW USED     %RAW USED
    hdd       35 GiB     21 GiB     14 GiB       14 GiB         40.86
    TOTAL     35 GiB     21 GiB     14 GiB       14 GiB         40.86

POOLS:
    POOL          ID     STORED     OBJECTS     USED     %USED     MAX AVAIL
    test-pool      1        0 B           0      0 B         0       6.3 GiB

[root@node3 /]# rados df
POOL_NAME USED OBJECTS CLONES COPIES MISSING_ON_PRIMARY UNFOUND DEGRADED RD_OPS  RD WR_OPS  WR USED COMPR UNDER COMPR
test-pool  0 B       0      0      0                  0       0        0      0 0 B      0 0 B        0 B         0 B

total_objects    0
total_used       14 GiB
total_avail      21 GiB
total_space      35 GiB
```

**创建 pool ：**

```
ceph osd pool create pool1 5
```

**查看 pool：**

```
[root@node3 /]# ceph df
RAW STORAGE:
    CLASS     SIZE       AVAIL      USED       RAW USED     %RAW USED
    hdd       35 GiB     21 GiB     14 GiB       14 GiB         40.86
    TOTAL     35 GiB     21 GiB     14 GiB       14 GiB         40.86

POOLS:
    POOL          ID     STORED     OBJECTS     USED     %USED     MAX AVAIL
    test-pool      1        0 B           0      0 B         0       6.3 GiB
    pool1          2        0 B           0      0 B         0       6.3 GiB
```

**dashboard 查看pool：**
![](https://github.com/xiliangMa/mscloud/blob/master/images/kubernetes/ceph-pool.png)
![](https://github.com/xiliangMa/mscloud/blob/master/images/kubernetes/ceph-pool-list.png)

