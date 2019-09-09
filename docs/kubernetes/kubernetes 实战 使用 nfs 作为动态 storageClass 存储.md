# kubernetes 实战 使用 nfs 作为动态 storageClass 存储
# 概述
之前有介绍过 [Kubernetes 实战 pv and pvc](https://blog.csdn.net/weixin_41806245/article/details/91390281)， 相信使用过的pv和pvc的同学或者有过虚拟化经验的人来说肯定会想到很多问题，比如每次申请 pvc 都需要手动添加pv，这岂不是太不方便了。那我们如何实现类似于公有云或者私有云的共享存储模式呢？kubernetes 提供了 [storageclass](https://kubernetes.io/docs/concepts/storage/storage-classes/#the-storageclass-resource) 的概念，接下来我们来一探究竟。
本文通过 nfs 来讲解，首先需要注意的是 nfs默认不支持动态存储，使用了第三方的NFS插件 [external-storage](https://github.com/kubernetes-incubator/external-storage), kubernetes
官网也有会叫详细的解析。

先上一张图大家就比较清楚了：
![image](https://github.com/xiliangMa/mscloud/blob/master/images/kubernetes/storageclass.png) 

# 基础环境
- [nfs 存储](https://blog.csdn.net/weixin_41806245/article/details/91355467)
- [pv、pvc 的基础](https://blog.csdn.net/weixin_41806245/article/details/91390281)
- [kubernetes 集群](https://blog.csdn.net/weixin_41806245/article/details/89381752)

# 操作步骤
> 保证基础环境部分就绪

### 1. 准备
clone external-storage
```
git clone https://github.com/kubernetes-incubator/external-storage.git
```
部署 rbac
```
cd external-storage/nfs-client/deploy
kubectl apply -f rbac.yaml
```
### 2. 部署 nfs client 插件
> 修改deployment.yml配置, 修改 nfs 配置信息。

```
  spec:
      serviceAccountName: nfs-client-provisioner
      containers:
        - name: nfs-client-provisioner
          image: quay.io/external_storage/nfs-client-provisioner:latest
          volumeMounts:
            - name: nfs-client-root
              mountPath: /persistentvolumes
          env:
            - name: PROVISIONER_NAME
              value: fuseim.pri/ifs
            - name: NFS_SERVER
              value: 192.168.8.220 # nfs 服务器ip
            - name: NFS_PATH 
              value: /root/work/storage/nfs # 存储地址
      volumes:
        - name: nfs-client-root
          nfs:
            server: 192.168.8.220  # nfs 服务器ip 
            path: /root/work/storage/nfs  # 存储地址
```
创建客户端

```
kubectl apply -f deployment.yaml
```
创建storage class
```
kubectl apply -f class.yaml
```

查看 nfs 客户端 和 storage class：

```
客户端
[root@node3 deploy]# kubectl  get pod
NAME                                      READY   STATUS    RESTARTS   AGE
nfs-client-provisioner-7695c66c6b-56sbw   1/1     Running   0          10m

storage class
[root@node3 deploy]# kubectl  get sc
NAME                  PROVISIONER      AGE
managed-nfs-storage   fuseim.pri/ifs   10m
``` 

### 3. 创建pvc

```
kind: PersistentVolumeClaim
apiVersion: v1
metadata:
  name: test-claim
  annotations:
    volume.beta.kubernetes.io/storage-class: "managed-nfs-storage"
spec:
  accessModes: # 策略
    - ReadWriteMany
  resources: 
    requests:
      storage: 1Mi # 请求空间大小
```

创建
```
kubectl  apply -f test-claim.yaml
```

查看 pvc
```
[root@node3 deploy]# kubectl  get pvc
NAME         STATUS   VOLUME                                     CAPACITY   ACCESS MODES   STORAGECLASS          AGE
test-claim   Bound    pvc-2febc25a-d2b0-11e9-9623-000c29b3776e   1Mi        RWX            managed-nfs-storage   14s
```
### 4. 部署测试pod
```
kind: Pod
apiVersion: v1
metadata:
  name: test-pod
spec:
  containers:
  - name: test-pod
    image: nginx
    command:
      - "/bin/sh"
    args:
      - "-c"
      - "touch /mnt/SUCCESS && exit 0 || exit 1"
    volumeMounts:
      - name: nfs-pvc # 绑定pvc
        mountPath: "/mnt"
  restartPolicy: "Never"
  volumes:
    - name: nfs-pvc #pvc 通过pvc 动态创建pv
      persistentVolumeClaim:
        claimName: test-claim
```

创建 测试 pod

```
kubectl  apply -f test-pod.yaml
```

查看pod
```
[root@node3 deploy]# kubectl  get pod | grep pod
test-pod                                  0/1     Completed   0          81s
```

查看 pv 是否动态创建:

```
[root@node3 deploy]# kubectl  get pv
NAME                                       CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS   CLAIM                STORAGECLASS          REASON   AGE
pvc-e78d0817-d2b0-11e9-9623-000c29b3776e   1Mi        RWX            Delete           Bound    default/test-claim   managed-nfs-storage            22m
```

好了是不是用起来很方便，还有其他的很多存储可以测试自己搞一套试试。


