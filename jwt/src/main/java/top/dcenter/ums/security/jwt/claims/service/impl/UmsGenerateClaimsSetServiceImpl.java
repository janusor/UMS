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
package top.dcenter.ums.security.jwt.claims.service.impl;

import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import top.dcenter.ums.security.common.utils.UuidUtils;
import top.dcenter.ums.security.core.api.tenant.handler.TenantContextHolder;
import top.dcenter.ums.security.jwt.api.claims.service.CustomClaimsSetService;
import top.dcenter.ums.security.jwt.claims.service.GenerateClaimsSetService;
import top.dcenter.ums.security.jwt.enums.JwtCustomClaimNames;

import java.time.Instant;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * 根据 {@link Authentication} 生成 {@link JWTClaimsSet} 的接口简单的实现.<br>
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.9 22:24
 */
@Slf4j
public class UmsGenerateClaimsSetServiceImpl implements GenerateClaimsSetService {

    /**
     * jwt 的有效期, 单位: 秒
     */
    private final long timeout;
    /**
     * jwt 的 issue
     */
    private final String iss;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired(required = false)
    private TenantContextHolder tenantContextHolder;
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired(required = false)
    private CustomClaimsSetService customClaimsSetService;

    /**
     * @param timeout jwt 的有效期, 单位: 秒
     */
    public UmsGenerateClaimsSetServiceImpl(long timeout, String iss) {
        this.timeout = timeout;
        this.iss = iss;
    }

    @Override
    public JWTClaimsSet generateClaimsSet(Authentication authentication) {

        // Prepare JWT with claims set
        JWTClaimsSet.Builder builder = null;
        if (nonNull(customClaimsSetService)) {
            builder = new JWTClaimsSet.Builder(customClaimsSetService.toClaimsSet(authentication));
        }

        if (isNull(builder)) {
        	builder = new JWTClaimsSet.Builder();
        }

        // tenantId
        if (nonNull(tenantContextHolder)) {
            builder.claim(JwtCustomClaimNames.TENANT_ID.getClaimName(), tenantContextHolder.getTenantId(authentication));
        }

        // iss
        if (nonNull(iss)) {
            builder.issuer(this.iss);
        }

        // jti
        builder.jwtID(UuidUtils.getUUID());
        builder.claim(JwtCustomClaimNames.USER_ID.getClaimName(), authentication.getName())
               .claim(JwtClaimNames.EXP, Instant.now().plusSeconds(timeout).getEpochSecond());

        return builder.build();
    }

}