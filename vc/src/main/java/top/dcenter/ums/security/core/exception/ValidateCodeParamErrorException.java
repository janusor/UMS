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

package top.dcenter.ums.security.core.exception;

import top.dcenter.ums.security.common.enums.ErrorCodeEnum;

/**
 * 验证码参数异常
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/7 13:19
 */
public class ValidateCodeParamErrorException extends ValidateCodeException {

    private static final long serialVersionUID = 5071331297299386304L;

    public ValidateCodeParamErrorException(ErrorCodeEnum errorCodeEnum, String data, String ip) {
        super(errorCodeEnum, ip, data);
    }

    public ValidateCodeParamErrorException(ErrorCodeEnum errorCodeEnum, Throwable cause, String data,
                                           String ip) {
        super(errorCodeEnum, cause, ip, data);
    }
}