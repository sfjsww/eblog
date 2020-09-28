package eblog.demo.config;

import cn.hutool.core.map.MapUtil;
import eblog.demo.shiro.AccountRealm;
import eblog.demo.shiro.AuthFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Configuration
//安全配置
public class ShiroConfig {

    @Bean
    public SecurityManager securityManager(AccountRealm accountRealm){

        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(accountRealm);

        log.info("--------------->SecurityManager注入成功");

        return securityManager;
    }
//    拦截器链
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager){

        ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();
        filterFactoryBean.setSecurityManager(securityManager);
//        配置登录的url和登陆成功的url
        filterFactoryBean.setLoginUrl("/login");
        filterFactoryBean.setSuccessUrl("/user/center");
//        配置未授权跳转页面
        filterFactoryBean.setUnauthorizedUrl("/error/403");
        filterFactoryBean.setFilters(MapUtil.of("auth",authFilter()));


        Map<String,String> hashMap = new LinkedHashMap<>();
        hashMap.put("/user/home","auth");
        hashMap.put("/user/set","auth");
        hashMap.put("/user/upload","auth");
        hashMap.put("/user/index","auth");
        hashMap.put("/user/public","auth");
        hashMap.put("/user/collection","auth");
        hashMap.put("/user/message","auth");
        hashMap.put("/message/remove","auth");
        hashMap.put("/message/nums","auth");


        hashMap.put("/collection/remove/","auth");
        hashMap.put("/collection/find/","auth");
        hashMap.put("/collection/add/","auth");

        hashMap.put("/post/edit","auth");
        hashMap.put("/post/submit","auth");
        hashMap.put("/post/delete","auth");
        hashMap.put("/post/reply/","auth");


        hashMap.put("/res/**","anon");
        hashMap.put("/login","anon");
        filterFactoryBean.setFilterChainDefinitionMap(hashMap);

        return filterFactoryBean;

    }

    @Bean
    public AuthFilter authFilter(){
        return new AuthFilter();
    }

}
