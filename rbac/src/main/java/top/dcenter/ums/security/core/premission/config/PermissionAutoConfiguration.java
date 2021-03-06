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

package top.dcenter.ums.security.core.premission.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableAsync;
import top.dcenter.ums.security.core.api.premission.service.UpdateAndCacheRolesResourcesService;
import top.dcenter.ums.security.core.api.premission.service.UriAuthorizeService;
import top.dcenter.ums.security.core.premission.advice.PermissionAdviceHandler;
import top.dcenter.ums.security.core.premission.aspect.RolePermissionsServiceAspect;
import top.dcenter.ums.security.core.premission.evaluator.UriAuthoritiesPermissionEvaluator;
import top.dcenter.ums.security.core.premission.listener.UpdateRolesResourcesListener;
import top.dcenter.ums.security.core.premission.service.DefaultUriAuthorizeService;

/**
 * Permission 配置
 *
 * @author YongWu zheng
 * @author zhailiang
 * @version V1.0  Created by 2020/11/6 19:59
 */
@Configuration()
@EnableAsync
@EnableAspectJAutoProxy
@Order(99)
@AutoConfigureAfter({RbacPropertiesAutoConfiguration.class})
public class PermissionAutoConfiguration {

    @Bean
    @ConditionalOnBean(type = "top.dcenter.ums.security.core.premission.advice.PermissionAdviceHandler")
    public PermissionAdviceHandler permissionAdviceHandler() {
        return new PermissionAdviceHandler();
    }

    @Bean
    @ConditionalOnBean(type = "top.dcenter.ums.security.core.api.premission.service.UpdateAndCacheRolesResourcesService")
    public UpdateRolesResourcesListener updateRolesAuthoritiesListener(UpdateAndCacheRolesResourcesService updateAndCacheRolesResourcesService) {
        return new UpdateRolesResourcesListener(updateAndCacheRolesResourcesService);
    }

    @Bean
    @ConditionalOnBean(type = "top.dcenter.ums.security.core.api.premission.service.UpdateAndCacheRolesResourcesService")
    public RolePermissionsServiceAspect rolePermissionsServiceAspect() {
        return new RolePermissionsServiceAspect();
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.premission.service.UriAuthorizeService")
    public UriAuthorizeService uriAuthorizeService() {
        return new DefaultUriAuthorizeService();
    }

    @Bean
    @ConditionalOnMissingBean(type = "org.springframework.security.access.PermissionEvaluator")
    public UriAuthoritiesPermissionEvaluator uriAuthoritiesPermissionEvaluator(UriAuthorizeService uriAuthorizeService) {
        return new UriAuthoritiesPermissionEvaluator(uriAuthorizeService);
    }

}