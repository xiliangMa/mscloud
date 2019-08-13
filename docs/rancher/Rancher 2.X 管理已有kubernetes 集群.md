# rancher 2.X 管理 已有 k8s 集群

# 前提
kubernetes 集群环境 （[基于centos7 搭建 1.14 kubernetes 集群](https://blog.csdn.net/weixin_41806245/article/details/89381752)）
-------
| 主机名 | ip | 角色 |
| --- | --- | --- |
| master.k8s.com | 192.168.8.181 | Master |
| node1.k8s.com | 192.168.8.182 | Node |
| node2.k8s.com | 192.168.8.183 | Node |

# 操作步骤
- 搭建rancher v2.2.7
- 导入环境集群

## 搭建 rancher v2.2.7 环境

```
sudo docker run -d -v /tmp/rancher:/tmp/rancher --restart=unless-stopped -p 80:80 -p 443:443 rancher/rancher:stable
```
![](https://github.com/xiliangMa/mscloud/blob/master/images/rancher/rancher-2.2.7.png)

## 导入集群
- 新建导入集群
新建集群 --> 选择导入已有 kubernetes 集群
![](https://github.com/xiliangMa/mscloud/blob/master/images/rancher/import-setting.png)

- 执行导入集群命令
将截图的命令在master 执行（自签名证书使用第三条命令，可根据实际情况选择）

![](https://github.com/xiliangMa/mscloud/blob/master/images/rancher/import-info.png)

- 查看 ns, pod 

    _你会发现集群下多了一个命名空间 **cattle-system** 里面有四个pod，rancher 通过这些agent监控操控你的主机和集群执行相关的操作_

```
[root@master k8s]# kubectl  get ns
NAME              STATUS   AGE
cattle-system     Active   30m
default           Active   8h
kube-node-lease   Active   8h
kube-public       Active   8h
kube-system       Active   8h

[root@master k8s]# kubectl get pod -n cattle-system
NAME                                    READY   STATUS    RESTARTS   AGE
cattle-cluster-agent-75fd784965-jcbxp   1/1     Running   0          30m
cattle-node-agent-b9hb2                 1/1     Running   0          30m
cattle-node-agent-g8crn                 1/1     Running   0          30m
cattle-node-agent-ghbhs                 1/1     Running   0          30m
```

- 导入结果

![](https://github.com/xiliangMa/mscloud/blob/master/images/rancher/rancher-import-k8s.png)


好了到这一步你就成功了，可以通过rancher 来操作你的 kubernetes 集群了。


有问题欢迎讨论！！！