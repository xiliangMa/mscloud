# spring-cloud-kubernetes 实战 一 服务发现
# 简介
spring-cloud-kubernetes是springcloud官方推出的开源项目，用于将Spring Cloud和Spring Boot应用运行在kubernetes环境，并且提供了通用的接口来调用kubernetes服务，[Spring-Cloud-kubernetes 地址](https://github.com/spring-cloud/spring-cloud-kubernetes)。

之前介绍过[Spring-Cloud](https://blog.csdn.net/weixin_41806245/article/details/98044072) 实战相关系列文章，相信大家都应该清楚 迁移到kubernetes 平台并不是一件简单的事情。 对于没有kubernets 或者 容器经验的同学，不仅仅是对kubernetes 知识的学习、还要处理 spring-cloud 自身 与 kubernetes 冲突部分。 这大大增加了过度的成本、显然这是大家不想看到的。现在有了 spring-cloud-kubernetes 这些事情变的简单，下面通过 demo 快速了解是如何获取kubernetes 的服务 service。

# 环境
- kubernetes 集群  [基于centos7 搭建 1.14 kubernetes 集群](https://blog.csdn.net/weixin_41806245/article/details/89381752)
-  macos 10.13.6
- Java：1.8.0_211
- Maven：3.6.1
- fabric8-maven-plugin插件：4.2.0
- spring-cloud-kubernetes：1.0.0.RELEASE
- spring-cloud: Greenwich.RELEASE
- spring-boot: 2.1.6.RELEASE

# 不想写代码的关注
mscloud 代码比较多，本次只需要关注  spring-cloud-kubernetes 目录下的代码即可。
如果想学习 spring-cloud、kubernetes、istio 可以关注
[github 地址](https://github.com/xiliangMa/mscloud)

```
git clone https://github.com/xiliangMa/mscloud
```
![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud-kubernetes/mscloud-kubernetes-discovery.png) 

# 开发代码
- 1. 添加依赖
- 2. 实现启动类
- 3. 实现服务发现 controller
- 4. 测试
- 5. 通过 fabric8 部署到 kubernetes 集群
#### 1. 添加依赖
主要添加 cloud-kubernets、fabric8依赖
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

    <artifactId>spring-cloud-kubernetes-discovery</artifactId>

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
            <artifactId>spring-cloud-starter-kubernetes</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-commons</artifactId>
            <scope>compile</scope>
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
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <enricher>
                        <config>
                            <fmp-controller>
                                <name>mscloud-spring-kubernetes-discovery</name>
                            </fmp-controller>
                            <fmp-service>
                                <name>mscloud-spring-kubernetes-discovery</name>
                                <!-- 部署到 kubernets 通过 nodeport 类型访问 -->
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
#### 2. 编写启动类
CloudKubernetesApp.java
```
// 添加服务发现客户端注解
@SpringBootApplication
@EnableDiscoveryClient
public class CloudKubernetesApp {
    public static void main(String[] args) {
        SpringApplication.run(CloudKubernetesApp.class, args);
    }
}
```

#### 3.  实现服务发现 controller
DIscoveryController.java
```
@RestController
public class DIscoveryController {

    private static final Log log = LogFactory.getLog(DIscoveryController.class);

    // 定义服务发现客户端
    @Autowired
    private DiscoveryClient discoveryClient;


    // 获取kubernetes 集群中的service， 是不是很简单，以后可以使用kubernetes 服务发现了    @RequestMapping("services")
    public List<String> Services() {
        return this.discoveryClient.getServices();
    }
}
```


#### 4. 测试
本地测试前首先保证你的kubernets 已经启动，否则不能获取services。
如何启动这里不用多说了吧，不会的回家跪搓衣板。。。。
![image](https://github.com/xiliangMa/mscloud/blob/master/images/Spring-Cloud-Kubernetes/cloud-k8s-services.png) 

获取了一堆service，咱们得验证下获取的对不对。

```
➜  ~  kubectl get svc --all-namespaces | awk '{print $2}'
NAME
details
productpage
ratings
reviews
kubernetes
compose-api
grafana
istio-citadel
istio-egressgateway
istio-galley
istio-ingressgateway
istio-pilot
istio-policy
istio-sidecar-injector
istio-telemetry
jaeger-agent
jaeger-collector
jaeger-query
kiali
prometheus
tracing
zipkin
kube-dns
kubernetes-dashboard
tiller-deploy
```

对比下能看到是一致的说明已经成功。

#### 5. 通过 fabric8 部署到 kubernetes 集群

执行下面的命令：
```
mvn clean package fabric8:deploy
```
执行成功后查看 pod 和 service 信息：

```
pod 信息如下：
➜  spring-cloud-kubernetes git:(master) ✗ kubectl get pod
NAME                                       READY   STATUS    RESTARTS   AGE
spring-cloud-kubernetes-6f7fdc9df9-xlvwz   1/1     Running   0          2m10s

service 信息如下：
能看到service 的类型是 nodeport ，这是通过pom.xml 配置的。
➜  spring-cloud-kubernetes git:(master) ✗ kubectl get svc
NAME                              TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
kubernetes                        ClusterIP   10.96.0.1        <none>        443/TCP          15d
mscloud-spring-cloud-kubernetes   NodePort    10.109.180.188   <none>        8080:31703/TCP   2m13s
```
是不是很神奇，不需要了解kubernetes 的service、deployment 也不需要写 yml 文件就把自己的java 程序部署到了kubernetes 集群。

