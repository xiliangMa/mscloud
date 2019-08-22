# spring-cloud-kubernetes 实战 二 configmap
# 简介：
Spring Cloud Config 相信大家都很熟悉，之前文章有介绍过 [Spring-Cloud 实战 一 服务配置中心 config](https://blog.csdn.net/weixin_41806245/article/details/97923108)
本章主要介绍Spring-Cloud-Kubernetes  如何获取 kubernetes 中部署的 configmap 信息。
kubernetes 提供两种配置 configmap 和 secrets, configmap是普通配置文件，secrets 是密文主要保存一些敏感的信息比如密码之类的。


---

# 环境
- kubernetes 集群  [基于centos7 搭建 1.14 kubernetes 集群](https://blog.csdn.net/weixin_41806245/article/details/89381752)
- macos 10.13.6
- Java：1.8.0_211
- Maven：3.6.1
- fabric8-maven-plugin插件：4.2.0
- spring-cloud-kubernetes：1.0.0.RELEASE
- spring-cloud: Greenwich.RELEASE
- spring-boot: 2.1.6.RELEASE


---

# 不想写代码的关注
mscloud 代码比较多，本次只需要关注  spring-cloud-kubernetes 目录下的代码即可。
如果想学习 spring-cloud、kubernetes、istio 可以关注
[github 地址](https://github.com/xiliangMa/mscloud)

```
git clone https://github.com/xiliangMa/mscloud
```
![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud-kubernetes/mscloud-kubernetes-reload.png) 


---

# 开发代码
- 添加依赖
- 定义启动配置
- 实现启动类
- 实现confgmap controller
- 通过 fabric8 部署到 kubernetes 集群
- 测试

#### 1. 添加依赖
主要添加 spring-cloud-starter-kubernetes-config、abric8 依赖

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>mscloud</artifactId>
        <groupId>com.examples.spcloud</groupId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-cloud-kubernetes-reload</artifactId>

    <properties>
        <fabric8.maven.plugin.version>4.2.0</fabric8.maven.plugin.version>
    </properties>

    <dependencies>
        <!-- spring boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-autoconfigure</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- spring-cloud-kubernetes -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-kubernetes-config</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <!-- 通过fabric8 build-->
            <plugin>
                <groupId>io.fabric8</groupId>
                <artifactId>fabric8-maven-plugin</artifactId>
                <version>${fabric8.maven.plugin.version}</version>
                <executions>
                    <execution>
                        <id>fmp</id>
                        <goals>
                            <goal>resource</goal>
                            <goal>build</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <enricher>
                        <config>
                            <fmp-controller>
                                <name>mscloud-spring-kubernetes-reload</name>
                            </fmp-controller>
                            <fmp-service>
                                <name>mscloud-spring-kubernetes-reload</name>
                                <type>NodePort</type>
                            </fmp-service>
                        </config>
                    </enricher>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```
#### 2.定义启动配置
这个比较简单，注意的是config部分， reload 是策略
```
spring:
  application:
    name: mscloud-reload-demo
  cloud:
    kubernetes:
      reload:
        enabled: true
        mode: polling
        period: 5000
      config:
        sources:
        # 指定configmap
        - name: ${spring.application.name}
        # 指定 命名空间 默认是default，如果指定其他的空间需要设置 权限
          namespce: default
```
#### 3. 实现启动类
CloudKubernetesReloadApp.java 这个就是普通springboot 启动类
```
@SpringBootApplication
public class CloudKubernetesReloadApp {
    public static void main(String[] args) {
        SpringApplication.run(CloudKubernetesReloadApp.class, args);
    }
}
```
#### 4. 实现confgmap controller
MyConfigMap.java  注意事项已经添加注释
```
// 这里需要注意的是  prefix = "config" 对应configmap 中定义的变量
@Configuration
@ConfigurationProperties(prefix = "config")
@RestController
public class MyConfigMap {

    private String message = "update your message...";

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
  
  // 接口测试
    @GetMapping("/config")
    public String GetConfigMap(){
        return this.getMessage();
    }
}
```
#### 4. 通过 fabric8 部署到 kubernetes 集群
部署前保证你的kubernetes 集群时正常的

```
mvn clean install fabric8:build fabric8:deploy
```

执行成功后查看 pod 和 service 信息：

```
pod 信息如下：
➜  mscloud git:(master) kubectl get pod
NAME                                                READY   STATUS    RESTARTS   AGE
mscloud-spring-kubernetes-reload-7bf7fd694b-9fkhv   1/1     Running   0          24m

service 信息如下：
➜  mscloud git:(master) kubectl get svc
NAME                               TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
kubernetes                         ClusterIP   10.96.0.1        <none>        443/TCP          16d
mscloud-spring-kubernetes-reload   NodePort    10.107.208.215   <none>        8080:31078/TCP   24m
```

注意svc 的类型是nodeport 所以测试时候需要用到31078端口，根据自己的定就可以。

#### 5. 测试
看到截图的效果就已经成功了：
![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud-Kubernetes/cloud-k8s-configmap-default.png) 

5.1 修改bootstrap.yml 切换成dev 模式 测试

```
spring:
  application:
    name: mscloud-reload-demo
  profiles:
    active: dev
  cloud:
    kubernetes:
      reload:
        enabled: true
        mode: polling
        period: 5000
      config:
        sources:
        # 指定configmap
        - name: ${spring.application.name}
        # 指定 命名空间 默认是default，如果指定其他的空间需要设置 权限
          namespce: default
```

删除已部署的deployment：

```
    kubectl delete deploy mscloud-spring-kubernetes-reload

```
> 注意：不要删除pod 否则他还会在自动出现、不理解的自己google下kubernetes deplyment 控制器。这个不用多说吧。。。

重新部署：

```
    mvn clean install fabric8:build fabric8:deploy
```
看到截图的效果就已经成功了：
![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud-Kubernetes/cloud-k8s-configmap-dev.png) 

5.1 修改bootstrap.yml prod 模式 测试

```
spring:
  application:
    name: mscloud-reload-demo
  profiles:
    active: prod
  cloud:
    kubernetes:
      reload:
        enabled: true
        mode: polling
        period: 5000
      config:
        sources:
        # 指定configmap
        - name: ${spring.application.name}
        # 指定 命名空间 默认是default，如果指定其他的空间需要设置 权限
          namespce: default
```

删除已部署的deployment：

```
    kubectl delete deploy mscloud-spring-kubernetes-reload

```
> 注意：不要删除pod 否则他还会在自动出现、不理解的自己google下kubernetes deplyment 控制器。这个不用多说吧。。。

重新部署：

```
    mvn clean install fabric8:build fabric8:deploy
```
看到截图的效果就已经成功了：
![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud-Kubernetes/cloud-k8s-configmap-prod.png) 


看上上面的过程是不是看到问题了，每次切换模式还得重新部署是不是很。。。。。。。


# 总结

 优点:
 - 使得configmap加载更像是加载srping-boot文件。
    
其实没有spring-cloud-kubernetes时，也可以通过env 模式获取configmap的信息。
好处实在是想不到了。。。。。。。。
      
 缺点：
 - 不支持热加载很鸡肋。。。
 - 相比spring-cloud-config  阿波罗等 没感觉到有啥优势。
    
    