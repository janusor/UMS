package top.dcenter.security.social.authtication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import top.dcenter.security.core.SocialWebSecurityConfigurerAware;
import top.dcenter.security.social.SocialProperties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * social 第三方授权登录注册相关配置
 * @author zyw
 * @version V1.0  Created by 2020/5/15 22:22
 */
@Configuration
@Slf4j
public class SocialAuthenticationConfigurerAware implements SocialWebSecurityConfigurerAware {
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private SocialAuthenticationConfig socialAuthenticationConfig;
    private final SocialProperties socialProperties;

    public SocialAuthenticationConfigurerAware(SocialProperties socialProperties) {
        this.socialProperties = socialProperties;
    }

    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
        // do nothing
    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {
        // 短信验证码登录配置
        if (socialAuthenticationConfig != null)
        {
            http.apply(socialAuthenticationConfig);
        }
    }

    @Override
    public Map<String, Set<String>> getAuthorizeRequestMap() {
        Set<String> uriSet = new HashSet<>();
        uriSet.add(socialProperties.getSignUpUrl());
        uriSet.add(socialProperties.getFailureUrl());
        uriSet.add(socialProperties.getSignInUrl());
        uriSet.add(socialProperties.getRegistUrl());

        Map<String, Set<String>> authorizeRequestMap = new HashMap<>(1);
        authorizeRequestMap.put(permitAll, uriSet);
        return authorizeRequestMap;
    }
}
