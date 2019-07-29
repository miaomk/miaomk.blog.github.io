# Shiro

### 简介：

- Apache Shiro 是Java的一个安全权限框架

- Shiro可以非常容易的开发出足够好的应用，其不仅可以用在JavaSE环境，也可以用在JavaEE环境
- Shiro可以完成：认证，授权，加密，会话管理，与web集成，缓存等

## Subject

- 任何与应用交互的“用户”

## SecurityManager

- 相当于SpringMVC中的DispatcherServlet，是shiro的心脏；所有具体的交互都通过SecurityManager进行控制；它管理着所有Subject，且负责进行认证，授权，会话及缓存的管理。



## Authenticator

- 负责Subject认证，是一个扩展点，可以自定义实现；可以使用认证策略（Authentication Strategy），即什么情况下算用户认证通过了

## Authorizer

- 授权器，即访问控制器，用来决定主体是否有权限进行相应的操作；即控制着用户能访问应用中的哪些功能

## Relam

- 可以有一个或多个Realm，可以认为是安全实体数据源，即用于获取安全实体的；可以是JDBC实现，也可以是内存实现等等；由用户提供；所以一般在应用中都需要实现自己的Relam；

## SessionManager

- 管理Session生命周期的组件；而Shiro并不仅仅可以用在Web环境，也可以用在普通的JavaSE环境

## CacheManager

- 缓存控制器，用来管理用户，角色，权限等的缓存的；因为这些数据基本上很少改变，放在缓存中可以提高访问的性能

## Cryptography

- 密码模块，Shiro提高了一些常见的加密组件用于如密码加密/解密