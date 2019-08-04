package cn.miaomaokao.shiro.shiro01.config;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author miaomaokao
 * @create 2019/8/2 14:08
 */
@Configuration
public class ShiroConfig {
    /**
     * 创建 ShiroFilterFactoryBean
     *
     * @param securityManager
     * @return ShiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean( DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //添加Shiro内置过滤器
        Map<String, String> filterMap = new LinkedHashMap<>();

        filterMap.put("/first/add", "anon");
        filterMap.put("/first/checkLogin", "anon");
        filterMap.put("/first/test", "anon");


        filterMap.put("/first/*", "authc");


        shiroFilterFactoryBean.setLoginUrl("/first/toLogin");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        return shiroFilterFactoryBean;
    }

    /**
     * 创建 DefaultWebSecurityManager
     *
     * @param myRealm 自己的Realm
     * @return DefaultWebSecurityManager
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager( MyRealm myRealm) {

        DefaultWebSecurityManager SecurityManager = new DefaultWebSecurityManager();

        //关联Realm
        SecurityManager.setRealm(myRealm);
        return SecurityManager;
    }

    /**
     * 创建Realm
     *
     * @return MyRealm
     */
    @Bean(name = "myRealm")
    public MyRealm getRealm() {

        return new MyRealm();
    }
}
