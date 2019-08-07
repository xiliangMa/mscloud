#前提
- 已经拥有gitlab服务器
- 熟悉 kubernetes 的基础操作
[helm 安装步骤](https://blog.csdn.net/weixin_41806245/article/details/98631199)

# 环境
mac os
kubernetes v1.14.3
helm 2.14 
gitlab-runner 

-------

# 准备
 - **下载 values.yaml **
 `wget https://gitlab.com/charts/gitlab-runner/raw/master/values.yaml`
 
 - **修改 values.yaml **
    两参数 settings > ci/cd > runners 可见
      - gitlabUrl 
      - runnerRegistrationToken

-  **添加gitlab chart repo**
    `helm repo add gitlab https://charts.gitlab.io`

-------

# 安装  
- **创建 gitlab namespace**
`kubectl create ns gitlab`

- 安装 **gitlab-runner**
此过程需要花点时间。。。。
`helm install --namespace gitlab --name gitlab-runner -f values.yaml gitlab/gitlab-runner`

-------

#测试
- 查看 **gitlab-runner pod**
`kubectl get pod -n gitlab
NAME                                           READY   STATUS    RESTARTS   AGE
gitlab-runner-gitlab-runner-847645c96c-225db   1/1     Running   1          16h`

- **注册结果**
![](https://github.com/xiliangMa/mscloud/blob/master/images/gitlab/gitlab-runner-k8s.png)