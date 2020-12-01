/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package top.dcenter.ums.security.core.demo.service;

import me.zhyd.oauth.model.AuthUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;
import top.dcenter.ums.security.core.api.tenant.handler.TenantContextHolder;
import top.dcenter.ums.security.core.exception.RegisterUserFailureException;
import top.dcenter.ums.security.core.exception.UserNotExistException;

import java.util.ArrayList;
import java.util.List;

/**
 *  用户密码与手机短信登录与注册服务：<br><br>
 *  1. 用于第三方登录与手机短信登录逻辑。<br><br>
 *  2. 用于用户密码登录逻辑。<br><br>
 *  3. 用户注册逻辑。<br><br>
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/20 11:06
 */
@Service
public class UserDetailsServiceImpl implements UmsUserDetailsService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    /**
     * 用户名
     */
    public static final String PARAM_USERNAME = "username";

    /**
     * 密码
     */
    public static final String PARAM_PASSWORD = "password";

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private UserCache userCache;
    /**
     * 用于密码加解密
     */
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private PasswordEncoder passwordEncoder;

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private TenantContextHolder tenantContextHolder;

    @SuppressWarnings("AlibabaUndefineMagicConstant")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        try
        {
            // 从缓存中查询用户信息:
            // 从缓存中查询用户信息
            if (this.userCache != null)
            {
                UserDetails userDetails = this.userCache.getUserFromCache(username);
                if (userDetails != null)
                {
                    return userDetails;
                }
            }

            /* 如果 ORM 使用的是 Mybatis-plus, Mybatis-plus 提供了一种多租户的解决方案，实现方式是基于分页插件(拦截器)进行实现的。
             * 那么此语句可以放在直接在 Mybatis-plus 分页插件(拦截器)中调用就 Ok, 此方法逻辑就跟单租户逻辑一样. 调用数据库接口的 sql 语句
             * 也不需要修改.
             */
            String tenantId = tenantContextHolder.getTenantId();
            // 根据 用户名 与 tenantId 获取用户信息
            // 获取用户信息逻辑。。。
            // ...

            log.info("Demo ======>: 登录用户名：{}, 登录成功, tenantId: {}", username, tenantId);
            return new User(username,
                            passwordEncoder.encode("admin"),
                            true,
                            true,
                            true,
                            true,
                            AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_VISIT, ROLE_USER, TENANT_" + tenantId));

        }
        catch (Exception e)
        {
            String msg = String.format("Demo ======>: 登录用户名：%s, 登录失败: %s", username, e.getMessage());
            log.error(msg, e);
            throw new UserNotExistException(ErrorCodeEnum.QUERY_USER_INFO_ERROR, e, username);
        }
    }


    @Override
    public UserDetails registerUser(String mobile) throws RegisterUserFailureException {

        if (mobile == null)
        {
            throw new RegisterUserFailureException(ErrorCodeEnum.MOBILE_NOT_EMPTY, null);
        }

        /* 如果 ORM 使用的是 Mybatis-plus, Mybatis-plus 提供了一种多租户的解决方案，实现方式是基于分页插件(拦截器)进行实现的。
         * 那么此语句可以放在直接在 Mybatis-plus 分页插件(拦截器)中调用就 Ok, 此方法逻辑就跟单租户逻辑一样. 调用数据库接口的 sql 语句
         * 也不需要修改.
         */
        String tenantId = tenantContextHolder.getTenantId();
        // 用户信息持久化逻辑。。。
        // ...

        log.info("Demo ======>: 手机短信登录用户 {}：注册成功, tenantId: {}", mobile, tenantId);

        User user = new User(mobile,
                             passwordEncoder.encode("admin"),
                             true,
                             true,
                             true,
                             true,
                             // 多租户应用必须添加 TENANT_tenantId 权限, 以便在授权时能正确的根据租户id来授权.
                             AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_VISIT, ROLE_USER, TENANT_" + tenantId)
        );

        // 把用户信息存入缓存
        if (userCache != null)
        {
            userCache.putUserInCache(user);
        }

        return user;
    }

    @Override
    public UserDetails registerUser(ServletWebRequest request) throws RegisterUserFailureException {

        String username = getValueOfRequest(request, PARAM_USERNAME, ErrorCodeEnum.USERNAME_NOT_EMPTY);
        String password = getValueOfRequest(request, PARAM_PASSWORD, ErrorCodeEnum.PASSWORD_NOT_EMPTY);
        // ...

        /* 如果 ORM 使用的是 Mybatis-plus, Mybatis-plus 提供了一种多租户的解决方案，实现方式是基于分页插件(拦截器)进行实现的。
         * 那么此语句可以放在直接在 Mybatis-plus 分页插件(拦截器)中调用就 Ok, 此方法逻辑就跟单租户逻辑一样. 调用数据库接口的 sql 语句
         * 也不需要修改.
         */
        String tenantId = tenantContextHolder.getTenantId();
        // UserInfo userInfo = getUserInfo(request)

        // 用户信息持久化逻辑。。。
        // ...

        String encodedPassword = passwordEncoder.encode(password);

        log.info("Demo ======>: 用户名：{}, 注册成功, tenantId: {}", username, tenantId);
        User user = new User(username,
                             encodedPassword,
                             true,
                             true,
                             true,
                             true,
                             // 多租户应用必须添加 TENANT_tenantId 权限, 以便在授权时能正确的根据租户id来授权.
                             AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_VISIT, ROLE_USER, TENANT_" + tenantId)
        );

        // 把用户信息存入缓存
        if (userCache != null)
        {
            userCache.putUserInCache(user);
        }

        return user;

    }

    @Override
    public UserDetails registerUser(@NonNull AuthUser authUser, @NonNull String username, @NonNull String defaultAuthority,
                                    String decodeState) throws RegisterUserFailureException {

        // 第三方授权登录不需要密码, 这里随便设置的, 生成环境按自己的逻辑
        String encodedPassword = passwordEncoder.encode(authUser.getUuid());

        // 这里的 decodeState 可以根据自己实现的 top.dcenter.ums.security.core.oauth.service.Auth2StateCoder 接口的逻辑来传递必要的参数.
        // 比如: 第三方登录成功后的跳转地址
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 假设 decodeState 就是 redirectUrl, 我们直接把 redirectUrl 设置到 request 上
        // 后续经过成功处理器时直接从 requestAttributes.getAttribute("redirectUrl", RequestAttributes.SCOPE_REQUEST) 获取并跳转
        if (requestAttributes != null) {
            requestAttributes.setAttribute("redirectUrl", decodeState, RequestAttributes.SCOPE_REQUEST);
        }
        // 当然 decodeState 也可以传递从前端传到后端的用户信息, 注册到本地用户

        /* 如果 ORM 使用的是 Mybatis-plus, Mybatis-plus 提供了一种多租户的解决方案，实现方式是基于分页插件(拦截器)进行实现的。
         * 那么此语句可以放在直接在 Mybatis-plus 分页插件(拦截器)中调用就 Ok, 此方法逻辑就跟单租户逻辑一样. 调用数据库接口的 sql 语句
         * 也不需要修改.
         */
        String tenantId = tenantContextHolder.getTenantId();
        // 多租户应用必须添加 TENANT_tenantId 权限, 以便在授权时能正确的根据租户id来授权.
        defaultAuthority = defaultAuthority + ", TENANT_" + tenantId;

        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(defaultAuthority);
        // ... 用户注册逻辑

        log.info("Demo ======>: 用户名：{}, 注册成功, tenantId: {}", username, tenantId);

        // @formatter:off
        UserDetails user = User.builder()
                               .username(username)
                               .password(encodedPassword)
                               .disabled(false)
                               .accountExpired(false)
                               .accountLocked(false)
                               .credentialsExpired(false)
                               .authorities(grantedAuthorities)
                               .build();
        // @formatter:off

        // 把用户信息存入缓存
        if (userCache != null)
        {
            userCache.putUserInCache(user);
        }

        return user;
    }

    @Override
    public UserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        UserDetails userDetails = loadUserByUsername(userId);
        User.withUserDetails(userDetails);
        return User.withUserDetails(userDetails).build();
    }

    @Override
    public List<Boolean> existedByUsernames(String... usernames) throws UsernameNotFoundException {
        // ... 在本地账户上查询 userIds 是否已被使用
        List<Boolean> list = new ArrayList<>();
        list.add(true);
        list.add(false);
        list.add(false);

        return list;
    }

    private String getValueOfRequest(ServletWebRequest request, String paramName, ErrorCodeEnum usernameNotEmpty) throws RegisterUserFailureException {
        String result = request.getParameter(paramName);
        if (result == null)
        {
            throw new RegisterUserFailureException(usernameNotEmpty, request.getSessionId());
        }
        return result;
    }
}