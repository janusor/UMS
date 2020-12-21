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
package top.dcenter.ums.security.core.tasks.handler;

import org.springframework.beans.factory.annotation.Autowired;
import top.dcenter.ums.security.common.api.tasks.handler.JobHandler;
import top.dcenter.ums.security.core.api.oauth.job.RefreshTokenJob;

import java.util.Collection;
import java.util.Map;

/**
 * 刷新第三方授权登录的 accessToken 有效期的定时任务处理器
 * @author YongWu zheng
 * @version V2.0  Created by 2020/11/2 10:28
 */
public class RefreshAccessTokenJobHandler implements JobHandler {

    private final String cronExp;
    @Autowired
    private Map<String, RefreshTokenJob> refreshTokenJobMap;

    public RefreshAccessTokenJobHandler(String cronExp) {
        this.cronExp = cronExp;
    }

    @Override
    public void run() {
        if (this.refreshTokenJobMap == null) {
            return;
        }
        Collection<RefreshTokenJob> validateCodeJobs = this.refreshTokenJobMap.values();
        // 刷新 accessToken
        validateCodeJobs.forEach(RefreshTokenJob::refreshTokenJob);
    }

    @Override
    public String cronExp() {
        return this.cronExp;
    }
}
