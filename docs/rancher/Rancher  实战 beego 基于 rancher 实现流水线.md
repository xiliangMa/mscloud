# 简介

CI/CD（持续集成与持续交付）敏捷、稳定、可靠的特性，越来越被企业所青睐与需要。然而真正实现CI／CD却并非易事，pipeline搭建工作复杂，平滑升级难以保障，服务宕机难以避免，那该如何真正把CI/CD在企业里落地并最终带来生产运维效率的提升？来自硅谷的企业级容器管理平台提供商Rancher Labs，始终秉承着“让容器在企业落地”的理念，带来了开源、极简、功能强大的Rancher Pipeline解决方案，助力CI/CD在企业的真正落地。

Rancher Pipeline包含的强大功能有：

同时支持多源码管理

市场中大部分的CI/CD工具无法做到同时支持多种源代码管理，甚至暂不支持任何私有仓库。而在Rancher Pipeline中，Rancher创造性地让同一个Rancher用户可以同时使用GitHub与GitLab进行基于OAuth的身份验证，无需插件，即可在单一环境中同时拉取、使用和管理托管在GitHub和GitLab的代码。

---


# 环境
 - rancher 2.16
 - kubernetes 集群 v1.12
 - github

注意: rancher新版本的pipeline 有很多新的变化可以搭建新版本，操作都一样。

---

# rancher pipeline 组件图
> rancher的所有流水线组件跑在k8s集群内部，并且是自动构建的，这就省去了搭建cicd组件的很多工作。并且也内置了docker registry仓库，当然可以自定义。

![image](https://github.com/xiliangMa/mscloud/blob/master/images/rancher/rancher-cicd-model.png)


---

# 源码
如果想快速了解过程的话，可以使用下面的代码。
    
    github 地址：https://github.com/xiliangMa/restapi

restapi 分为 两部分 1. 基于beego的后台服务 2. mariadb 

[如何部署应用程序到k8s集群](https://blog.csdn.net/weixin_41806245/article/details/93745532)


---
    
# 添加镜像凭证
配置自己的镜像仓库，可以选择docker hub、Quay.io、或者自搭建的harbor等，这里采用docker hub 演示。

**资源 --> 镜像库凭证**

![image](https://github.com/xiliangMa/mscloud/blob/master/images/rancher/racnher-registry.png)

![image](https://github.com/xiliangMa/mscloud/blob/master/images/rancher/rancher-pipeline-hub.png)

---

# 配置代码仓库
本次演示使用的github。
**资源 --> 流水线**
- 选择github 创建application
- 输入客户端用户和秘钥
- 开启使用流水线的项目

![image](https://github.com/xiliangMa/mscloud/blob/master/images/rancher/rancher-cicd-github.png)

![image](https://github.com/xiliangMa/mscloud/blob/master/images/rancher/github-application.png)

![image](https://github.com/xiliangMa/mscloud/blob/master/images/rancher/rancher-cicd-github-restapi-enable.png)


---

# 配置流水线
**工作负载 --> 流水线**


可以通过ui 直接添加或者删除流水线步骤、也可以修改yml 直接提交github。
![image](https://github.com/xiliangMa/mscloud/blob/master/images/rancher/rancher-cicd-setting.png)

这里直接编写好了.racnher-pipeline.yml 也可以通过界面配置，本地cicd 一共有四个步骤:

![image](https://github.com/xiliangMa/mscloud/blob/master/images/rancher/rancher-cicd-stages.png)

编辑publish步骤：
![image](https://github.com/xiliangMa/mscloud/blob/master/images/rancher/rancher-cicd-stages-setting.png)


---

# 触发流水线
 - 手动触发
 - 修改代码提交

大家可以自行选择，触发流水线大家能看大具体的

执行过程如下：

![image](https://github.com/xiliangMa/mscloud/blob/master/images/rancher/racnher-cicd-logs.png)

查看restapi部署结果：
![image](https://github.com/xiliangMa/mscloud/blob/master/images/rancher/rancher-cicd-restapi.png)

查看仓库推送结果：
![image](https://github.com/xiliangMa/mscloud/blob/master/images/rancher/restapi-cicd.png)


---

# 总结

其实市面上cicd 工具挺多的 如gitlabci、jenkins 但是各有不同。

下面是来自rancher提供的一张对比图：


![image](https://github.com/xiliangMa/mscloud/blob/master/images/rancher/cicd-vs.png)


如果你部署在kubernetes 集群并且想简化kubernetes集群 管理，并实现cicd配置的，建议 选择rancher，几乎不用做任何东西，只需要配置流水线脚本即可。


