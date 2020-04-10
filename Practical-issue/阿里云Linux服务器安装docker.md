### 阿里云Linux服务器安装docker
##### 1. 查看Linux服务器内核版本（docker要求CentOS系统内核版本高于3.10）

```java
uname -r
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200407093047434.jpg)
##### 2. 确保yum包更新到最新
```java
yum update
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200407093309622.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80Mzg2NDkyNw==,size_16,color_FFFFFF,t_70)
**因为我已经更新过了，小伙伴执行这个命令后让你确认是否安装，直接Y就行了**


##### 3. 卸载旧版本docker（如果已经安装过旧版本的话）
```java
yum remove docker  docker-common docker-selinux docker-engine
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020040709402850.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80Mzg2NDkyNw==,size_16,color_FFFFFF,t_70)

##### 4. 安装需要的软件包， yum-util 提供yum-config-manager功能，另外两个是devicemapper驱动依赖的

```java
yum install -y yum utils device-mapper-persistent-data lvm2
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200407092814328.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80Mzg2NDkyNw==,size_16,color_FFFFFF,t_70)
##### 5. 设置yum源

```java
yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200407094350793.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80Mzg2NDkyNw==,size_16,color_FFFFFF,t_70)
##### 6. 安装docker

```java
yum install docker-ce docker-ce-cli containerd.io
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200407094610990.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80Mzg2NDkyNw==,size_16,color_FFFFFF,t_70)

##### 7. 启动Docker并设置开机启动

```java
systemctl start docker
systemctl enable docker
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020040709482373.jpg)

##### 8. 验证Docker是否安装成功

```java
docker version
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200407094957577.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80Mzg2NDkyNw==,size_16,color_FFFFFF,t_70)

##### 9. Docker HelloWorld测试

```java
docker run hello-world
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200407095402391.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80Mzg2NDkyNw==,size_16,color_FFFFFF,t_70)

**因为本地没有这个镜像，所以从远程官方仓库去拉取，下载。然后我们再执行一次**

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200407095619785.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80Mzg2NDkyNw==,size_16,color_FFFFFF,t_70)

##### 安装成功！！！

#### 拓展，配置阿里云镜像仓库
#### 1. 登录你的阿里云，进入镜像服务，[点这里快速进入 ](https://cr.console.aliyun.com/cn-hangzhou/instances/mirrors)

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020040710105029.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80Mzg2NDkyNw==,size_16,color_FFFFFF,t_70)
#### 2. 在/etc/docker目录下找到在daemon.json文件（没有就新建），将下面内容写入

```bash
{

 "registry-mirrors": ["https://xxxxxxx.mirror.aliyuncs.com"]

}
```
#### 3. 重启daemon

```bash
systemctl daemon-reload
```

 

####  4. 重启docker服务

```bash
systemctl restart docker
```

### 配置完成！！！