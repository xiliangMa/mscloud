# spring-cloud 实战：spring cloud 微服务 迁移到 kubernetes平台

# 前提
- 熟悉 Docker 操作、Dockefile编写
- 熟悉 kubernetes 基础操作
- 熟悉 kubernetes 基本resource
*可以查看之前文章*

-------

# 环境
- kubernetes v1.14.3 [kubernetes 集群搭建](https://blog.csdn.net/weixin_41806245/article/details/89381752)

-------

# 整体架构图
![](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/mscloud-module.png)

[mscloud 地址](https://github.com/xiliangMa/mscloud) 

[mscloud-config 地址](https://github.com/xiliangMa/mscloud-config)

-------

# build 镜像
以配置中心为例其他的一致
	
```
cd config
docker build -t xiliangma/mscloud-config .
```
	
-------

# 准备deployment、service
以配置中心为例其他的一致

```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mscloud-config
spec:
  replicas: 1
  selector:
    matchLabels:
      app: mscloud-config
      project: mscloud
  template:
    metadata:
      labels:
        app: mscloud-config
        project: mscloud
    spec:
      containers:
      - name: config
        image: xiliangma/mscloud-config:latest
        imagePullPolicy: IfNotPresent
        ports:
        - name: dev
          containerPort: 8888
          hostPort: 30001
        resources:
          limits:
            cpu: 1000m
            memory: 1024Mi
          requests:
            cpu: 300m
            memory: 256Mi
        volumeMounts:
        - mountPath: /mscloud/config
          name: config-data
      volumes:
      - name: config-data
        hostPath:
          path: /tmp/mscloud/config

---
apiVersion: v1
kind: Service
metadata:
  name: mscloud-config-service
  labels:
    app: mscloud-config
    project: mscloud
spec:
  selector:
    app: mscloud-config
    project: mscloud
  ports:
  - name: dev
    port: 8888

```

注意： 大部分module需要创建service，这里服务间访问通过service访问。

-------

# 部署
以配置中心为例其他的一致;
kubectl apply -f 01-config.yml

![image](https://github.com/xiliangMa/mscloud/blob/master/images/k8s-mscloud.png)
-------

# 测试

**config：**
![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/config-test.png)

**eureka 服务注册：**
![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/eureka-server-test-admin.png)

**熔断监控：**
![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/hystrix-test.png)

**zipkin 请求监控：**
![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/zipkin-request-test.png)

**admin 服务：**
![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/admin-test.png)

**admin 监控：**
![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud/admin-info-test.png)


