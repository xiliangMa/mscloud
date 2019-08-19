# Kubernetes 实战 二 CRD 自定义控制器
# 简介：
之前介绍过 [Kubernetes 实战 一 CRD 自定义资源](https://blog.csdn.net/weixin_41806245/article/details/94451734)  。
但是CRD只能实现资源的定义，kubernetes operator =  crd + controller。 那如何实现自定义的控制呢？ 接下来教你10分钟快速实现自定义controller。（可以合理上网的前提下。。。。）

-------
 
# 环境
 - mac os 10.13.6
 - kubernetes 1.14.0 --> [Centos 7 快速搭建kubernetes 集群](https://blog.csdn.net/weixin_41806245/article/details/89381752)
 - kubebuilder 2.0.0-alpha.4
 - kustomize 3.1.0
 - golang 1.12.7
 
-------

# 创建 operator 操作步骤
#### 0. 写在前面
接下来的来的过程有一些小坑，很多步骤都需要翻墙、希望能够合理的翻墙 否则不用多说了吧 。。。。。
建议使用我的[github](https://github.com/xiliangMa/vm-crd)代码，熟悉流程后在自行搭建，可以直接跳过 2、3 两个步骤。第8步 就是编译镜像然后通过yml部署到kubernetes集群 不想尝试的可以跳过。

**代码下载：** 
```
git clone https://github.com/xiliangMa/vm-crd.git
```
#### 1. kubebuilder 安装
> kubebuilder 基于 client go 能帮我们节省大量工作，让开发CRD和adminsion webhook变得异常简单。go 语言开发的首选，如果你是 java 开发的话可以使用 [Fabric8](http://fabric8.io/guide/getStarted/openshift.html)。

二进制安装：
```
os=$(go env GOOS)
arch=$(go env GOARCH)

# download kubebuilder and extract it to tmp
curl -sL https://go.kubebuilder.io/dl/2.0.0-rc.0/${os}/${arch} | tar -xz -C /tmp/

# move to a long-term location and put it on your path
# (you'll need to set the KUBEBUILDER_ASSETS env var if you put it somewhere else)
sudo mv /tmp/kubebuilder_2.0.0-rc.0_${os}_${arch} /usr/local/kubebuilder
export PATH=$PATH:/usr/local/kubebuilder/bin
```
#### 2.  kustomize 安装

```
go install sigs.k8s.io/kustomize/v3/cmd/kustomize
```

#### 3. 创建 operator project
需要翻墙。。。。
```
kubebuilder init --domain xiliangma.com --license apache2 --owner "xiliangMa"
```
#### 4. 创建 api
需要翻墙。。。。
```
kubebuilder create api --group mscloud --version v1 --kind VM
```
#### 5. 部署 CRD 到集群
需要翻墙。。。。
部署 crd： 
```
make install
```

查看 crd：
```
➜  crd git:(master) ✗ kubectl get crd | grep vm
vms.mscloud.xiliangam.com              2019-08-19T08:00:31Z
```
#### 6. 本地启动 controller
```
➜  crd git:(master) ✗ make run
go get sigs.k8s.io/controller-tools/cmd/controller-gen@v0.2.0-beta.2
/Users/maxiliang/go/bin/controller-gen object:headerFile=./hack/boilerplate.go.txt paths=./api/...
go fmt ./...
go vet ./...
go run ./main.go
2019-08-19T16:44:26.614+0800	INFO	controller-runtime.controller	Starting EventSource	{"controller": "vm", "source": "kind source: /, Kind="}
2019-08-19T16:44:26.615+0800	INFO	setup	starting manager
2019-08-19T16:44:26.718+0800	INFO	controller-runtime.controller	Starting Controller	{"controller": "vm"}
2019-08-19T16:44:26.819+0800	INFO	controller-runtime.controller	Starting workers	{"controller": "vm", "worker count": 1}
Get vm spec info success,  vm1 Centos 7.2 1 1024 true
2019-08-19T16:44:26.827+0800	DEBUG	controller-runtime.controller	Successfully Reconciled	{"controller": "vm", "request": "default/vm-sample2"}
Get vm spec info success,  vm1 Centos 7.2 1 1024 true
2019-08-19T16:44:26.834+0800	DEBUG	controller-runtime.controller	Successfully Reconciled	{"controller": "vm", "request": "default/vm-sample2"}
```

####  7. 创建 vm
创建：
```
kubectl apply -f config/samples/mscloud_v1_vm.yaml 
```
查看：

```
➜  crd git:(master) kubectl get vm
NAME         AGE
vm-sample2   35m
```
#### 8. build 镜像部署
需要翻墙。。。。此步骤可以不做，本地启动controller 也可以
```
make docker-build docker-push IMG=xiliangma/vmcontroller
make deploy
```
好了已经成功发布controller 到集群了。好了 到这里是不是就完事了，散伙回家。。。
```
➜  crd git:(master) docker images | grep xiliangma/vmcontroller
xiliangma/vmcontroller                                                       latest              a1d7fa46abbf        8 days ago          815MB
```

哈哈  接下来才是自定义controller部分，很简单其实就是对 vm 资源的增删改查。。。。

-------

# 自定义 controller 开发
下面可以看到spec中只有foo 属性，那如何像 deployment 一样实现多参数配置呢？请看下面的操作步骤。
```
➜  crd git:(master) cat config/samples/mscloud_v1_vm.yaml
apiVersion: mscloud.xiliangam.com/v1
kind: VM
metadata:
  name: vm-sample
spec:
  # Add fields here
  foo: bar
```

#### 1. 自定义 vm
- 1.1 修改 VMSpec 
- 1.2 创建 vm
- 1.3 添加控制逻辑

**1.1 修改 VMSpec 添加属性**
```
添加 Name 、 类型、HA 等。。
type VMSpec struct {
	// INSERT ADDITIONAL SPEC FIELDS - desired state of cluster
	// Important: Run "make" to regenerate code after modifying this file

	// xiliangma test vm crd controller
	Name   string `json:"name"`
	Type   string `json:"type"`
	CPU    int    `json:"cpu"`
	Memory int    `json:"memory"`
	HA     bool   `json:"ha"`
}
```

**1.3 创建 vm**
mscloud_v2_vm.yaml：
```
 apiVersion: mscloud.xiliangam.com/v1
kind: VM
metadata:
  name: vm-sample2
spec:
  # Add fields here
  foo: bar
  name: "vm1"
  type: "Centos 7.2"
  cpu: 1
  memory: 1024
  ha: true
```
创建：
```
kubectl apply -f config/samples/mscloud_v2_vm.yaml
```

好了添加的属性已经成功配置：

```
➜  crd git:(master) ✗ kubectl describe vms
Name:         vm-sample2
Namespace:    default
Labels:       <none>
Annotations:  kubectl.kubernetes.io/last-applied-configuration:
                {"apiVersion":"mscloud.xiliangam.com/v1","kind":"VM","metadata":{"annotations":{},"name":"vm-sample2","namespace":"default"},"spec":{"cpu"...
API Version:  mscloud.xiliangam.com/v1
Kind:         VM
Metadata:
  Creation Timestamp:  2019-08-19T08:40:59Z
  Generation:          1
  Resource Version:    261536
  Self Link:           /apis/mscloud.xiliangam.com/v1/namespaces/default/vms/vm-sample2
  UID:                 11803e5e-c25d-11e9-970d-025000000001
--------- 成功添加vm 属性
Spec:
  Cpu:     1
  Foo:     bar
  Ha:      true
  Memory:  1024
  Name:    vm1
  Type:    Centos 7.2
Events:              <none>
```

**1.4 添加获取逻辑**
> 通过 kubebuilder 初始化的项目结构比较简单，控制逻辑都在controller 里实即可。

Reconcile 里添加获取逻辑：
```
    ctx := context.Background()
	log := r.Log.WithValues("vm", req.NamespacedName)

	// 1. xiliangma 获取vm 信息
	var vm mscloudv1.VM
	if err := r.Get(ctx, req.NamespacedName, &vm); err != nil {
		log.Error(err, "unable to get vm")
	} else {
		fmt.Println("Get vm spec info success, ", vm.Spec.Name, vm.Spec.Type, vm.Spec.CPU, vm.Spec.Memory, vm.Spec.HA)

	}
```
测试  make && make install && make run 日志中能看到打印出的信息

#### 2. 更新
- 2.1 修改 VMStatus
- 2.2 添加修改逻辑
**1.2 修改 VMStatus**
```
添加 UpdateLastTime 、 Status 
type VMStatus struct {
	// INSERT ADDITIONAL STATUS FIELD - define observed state of cluster
	// Important: Run "make" to regenerate code after modifying this file

	// xiliangma test vm crd controller
	UpdateLastTime metav1.Time `json:"update_last_time"`
	Status         string      `json:"status"`
}
```

**2.2 添加修改逻辑**

```
// 2. 更新虚拟机状态
vm.Status.UpdateLastTime = metav1.Now()
vm.Status.Status = "Running"
if err := r.Status().Update(ctx, &vm); err != nil {
	log.Error(err, "not update vm  status.")
}
```

重新执行：  make && make install && make run 

> 如果出现:the server could not find the requested resource 这个错误，那么在CRD结构体上需要加个注释 // +kubebuilder:subresource:status

```
// +kubebuilder:subresource:status
// +kubebuilder:object:root=true

// VM is the Schema for the vms API
type VM struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   VMSpec   `json:"spec,omitempty"`
	Status VMStatus `json:"status,omitempty"`
}
```
看到下面结果 修改成功：

```
➜  crd git:(master) ✗ kubectl describe vms
Name:         vm-sample2
Namespace:    default
Labels:       <none>
Annotations:  kubectl.kubernetes.io/last-applied-configuration:
                {"apiVersion":"mscloud.xiliangam.com/v1","kind":"VM","metadata":{"annotations":{},"name":"vm-sample2","namespace":"default"},"spec":{"cpu"...
API Version:  mscloud.xiliangam.com/v1
Kind:         VM
Metadata:
  Creation Timestamp:  2019-08-19T08:40:59Z
  Generation:          1
  Resource Version:    261536
  Self Link:           /apis/mscloud.xiliangam.com/v1/namespaces/default/vms/vm-sample2
  UID:                 11803e5e-c25d-11e9-970d-025000000001
Spec:
  Cpu:     1
  Foo:     bar
  Ha:      true
  Memory:  1024
  Name:    vm1
  Type:    Centos 7.2
Status:
---- 修改成功
  Status:            Running
  Update Last Time:  2019-08-19T08:44:26Z
Events:              <none>
```

#### 3. 删除 vm
添加删除逻辑：
```
// 3. 删除虚拟机
time.Sleep(time.Second * 5)
if err := r.Delete(ctx, &vm); err != nil {
	log.Error(err, "unable to delete vm ", "vm", vm)
}
```
重新执行：  make && make install && make run
5s 后vm 被删除。



好了，到此你已经实现了基础的控制器是不是很简单。