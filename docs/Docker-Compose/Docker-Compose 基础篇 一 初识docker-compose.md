# Docker-Compose 简介
> Compose是用于定义和运行多容器Docker应用程序的工具。
> 使用compose，可以使用yaml文件配置应用程序的服务。
> 然后，通过一个命令，可以从配置中创建和启动所有服务。

# Docker-Compose 安装
**[官方安装链接]( https://docs.docker.com/compose/install/ "官方安装链接")**

------------

# 结构
- Docker Compose 编排分为三层，工程（project）、服务（service）、容器（container）

- Docker Compose 运行目录下的所有文件（docker-compose.yml）组成一个工程，
一个工程包含多个服务，每个服务中定义了容器运行的镜像、参数、依赖，
一个服务可包括多个容器实例

```yaml
version: '3'
services:
  # -------- config --------
  config:
    restart: always
    container_name: mscloud-config
    image: xiliangma/mscloud-config:latest
    ports:
      - "8888:8888"
    volumes:
      - /tmp/mscloud/config/:/maslcoud/config
```
------------

# 使用

> **使用compose基本上是一个三步过程:**

> 1. 用dockerfile，或者镜像定义应用程序的环境，以便在任何地方复制。

> 2. 在docker-compose.yml中定义组成应用程序的服务，以便它们可以在单独的环境中一起运行。

> 3. 运行docker compose up启动并运行整个应用程序。

------------
#常用命令
 #### 列出所有运行容器
`docker-compose ps`

#### 构建或者重新构建服务
`docker-compose build`

#### 启动指定服务已存在的容器
`docker-compose start test`

#### 删除指定服务的容器
`docker-compose rm test`

#### 构建、启动容器
`docker-compose up`
`docker-compose up -d #后台启动`

### 关闭服务
`docker-compose down`
`docker-compose down -v #关闭删除`
	
#### 查看当前服务images
`docker-compose images`

#### 通过发送 SIGKILL 信号来停止指定服务的容器
`docker-compose kill eureka`

#### 停止已运行的容器
`docker-copose stop test`

#### 查看服务日志输出
`docker-compose logs`

等等还有很多可以通过 docker-compose -h 查看学习

------------
# docker-compose.yml 属性
#### version：
> 指定 docker-compose.yml 文件的写法格式 现在使用3

#### services：
> 多个容器集合

#### build：
> 构建时，Compose 会利用它自动构建镜像，该值可以是一个路径，也可以是一个对象，用于指定 Dockerfile 参数
	
#### environment：
> 环境变量配置，可以用数组或字典两种方式

	environment:
    	USER: test
    	PASSWD: abc123

#### env_file:
> 从文件中获取环境变量，可以指定一个文件路径或路径列表
> 注意：其优先级低于 environment 指定的环境变量

#### image:
> 指定运行镜像

	image: nginx:lastest

#### ports:
> 指定端口、参数是列表

	ports:
	 - "8080:8080"
	 - "8081:8081"
	
#### volumes:
> 指定存储目录

	volumes:
      - /tmp/mscloud/config/:/maslcoud/config
	  - /tmp/mscloud/admin/:/maslcoud/admin
	
#### network_mode:
> 指定网络模式, 没有需求一般不需要动

	network_mode: "bridge"
	network_mode: "host"
	network_mode: "none"
	network_mode: "service:[service name]"
	network_mode: "container:[container name/id]"
	
#### depends_on:
> 指定启动顺序 A 依赖于 B， B先启动 A后启动，只保启动顺序，但是不保证B启动后，A在启动；

#### links:
> 服务之间可以使用服务名称相互访问，links 允许定义一个别名，从而使用该别名访问其它服务

	version: 3
	services:
    web:
        build: .
        links:
            - "db:database"
    db:
        image: postgres

