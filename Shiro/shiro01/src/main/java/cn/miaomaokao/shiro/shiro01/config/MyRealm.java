package cn.miaomaokao.shiro.shiro01.config;

import cn.miaomaokao.shiro.shiro01.dao.UserDao;
import cn.miaomaokao.shiro.shiro01.entity.UserEntity;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.Resource;

/**
 * @Author miaomaokao
 * @create 2019/8/2 19:16
 */
public class MyRealm extends AuthorizingRealm {
    private final String NAME = "admin";
    private final String PASSWORD = "admin";

    @Resource
    private UserDao userDao;

    /**
     * 执行授权逻辑
     *
     * @param principalCollection 参数
     * @return AuthorizationInfo
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行授权逻辑");
        return null;
    }

    /**
     * 执行认证逻辑
     *
     * @param authenticationToken token
     * @return AuthenticationInfo
     * @throws AuthenticationException 认证异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("执行认证逻辑");
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;

        UserEntity userEntity = userDao.findByName(token.getUsername());
        System.out.println(userEntity.toString());
        //Shiro自动校验密码
        return new SimpleAuthenticationInfo("",PASSWORD,"");
    }
}
