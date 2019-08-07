# Windows 10 下 安装 MySQL

### 1.解压从官网上下载的mysql-5.7.27-winx64.zip

![](https://github.com/miaomk/miaomk.blog.github.io/blob/master//环境搭建/Windows%2010%20安装%20MySQL/images/1-1.png))

#### 解压后文件夹中的内容

![](https://github.com/miaomk/miaomk.blog.github.io/blob/master//环境搭建/Windows%2010%20安装%20MySQL/images/1.png))

### 2.新建my.ini 并往my.ini中写入数据

####  新建的内容

![](https://github.com/miaomk/miaomk.blog.github.io/blob/master//环境搭建/Windows%2010%20安装%20MySQL/images/2-1.png))

#### my.ini中的内容：

![](https://github.com/miaomk/miaomk.blog.github.io/blob/master//环境搭建/Windows%2010%20安装%20MySQL/images/2-2.png))

```
[mysqld]

port = 3306

basedir=E:\Java\MySQL\mysql-5.7.27-winx64

datadir=E:\Java\MySQL\mysql-5.7.27-winx64\data

max_connections=200

character-set-server=utf8

default-storage-engine=INNODB

sql_mode=NO_ENGINE_SUBSTITUTION,STRICT_TRANS_TABLES

[mysql]

default-character-set=utf8
```

### 3.配置系统变量

#### MYSQL_HOME

![](https://github.com/miaomk/miaomk.blog.github.io/blob/master//环境搭建/Windows%2010%20安装%20MySQL/images/3-1.png))

#### Path

![](https://github.com/miaomk/miaomk.blog.github.io/blob/master//环境搭建/Windows%2010%20安装%20MySQL/images/3-2.png))

### 4.开始安装MySQL

#### 4.1以管理员身份运行cmd

![](https://github.com/miaomk/miaomk.blog.github.io/blob/master//环境搭建/Windows%2010%20安装%20MySQL/images/4-1.png))

#### 4.2进入bin目录下

![](https://github.com/miaomk/miaomk.blog.github.io/blob/master//环境搭建/Windows%2010%20安装%20MySQL/images/4-2.png))

#### 4.3 执行安装命令

```
 //此时会生成data目录
mysqld  --initialize

```

踩坑：

![](https://github.com/miaomk/miaomk.blog.github.io/blob/master//环境搭建/Windows%2010%20安装%20MySQL/images/踩坑1-1.png))

如果运行命令提示：**由于找不到MSVCR120.dll,无法继续执行代码.重新安装程序可能...**

这种情况需要安装 vcredist 

下载vcredist ：https://www.microsoft.com/zh-CN/download/details.aspx?id=40784

下载后，直接安装。

![](https://github.com/miaomk/miaomk.blog.github.io/blob/master//环境搭建/Windows%2010%20安装%20MySQL/images/踩坑1-2.png))

#### 4.4安装并查看是否安装成功

![](https://github.com/miaomk/miaomk.blog.github.io/blob/master//环境搭建/Windows%2010%20安装%20MySQL/images/4-4.png))

### 5.设置root密码

#### 5.1 更改配置

在my.ini文件（MySQL的配置文件）的[mysqld]下加一行skip-grant-tables

![](https://github.com/miaomk/miaomk.blog.github.io/blob/master//环境搭建/Windows%2010%20安装%20MySQL/images/5-1-1.png))

然后在任务管理器中重启MySQL服务

![](https://github.com/miaomk/miaomk.blog.github.io/blob/master//环境搭建/Windows%2010%20安装%20MySQL/images/5-1-2.png))

#### 5.2  mysql -u root -p

- 因为第一次安装所以Entry password时候直接回车就行了

![](https://github.com/miaomk/miaomk.blog.github.io/blob/master//环境搭建/Windows%2010%20安装%20MySQL/images/5-2.png))

#### 5.3 使用Navicat 测试MySQL是否成功

![](https://github.com/miaomk/miaomk.blog.github.io/blob/master//环境搭建/Windows%2010%20安装%20MySQL/images/5-3.png))

#### 5.4 修改MySQL密码

- MySQL5.7已经没有了PASSWORD字段，已经改成了authentication_string

```mysql
update mysql.user set authentication_string=password('root') where user='root' ;
```

![](https://github.com/miaomk/miaomk.blog.github.io/blob/master//环境搭建/Windows%2010%20安装%20MySQL/images/5-4.png))

#### 5.5 使用Navicat 测试更新后的密码是否有效

![](https://github.com/miaomk/miaomk.blog.github.io/blob/master//环境搭建/Windows%2010%20安装%20MySQL/images/5-5.png))