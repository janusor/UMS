package top.dcenter.security.social.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;
import top.dcenter.security.core.enums.ErrorCodeEnum;

/**
 * 第三方登录注册异常，如用户名重名等
 * @author zyw
 * @version V1.0  Created by 2020/5/21 23:06
 */
@SuppressWarnings({"unused", "AlibabaClassNamingShouldBeCamel"})
public class OAuth2SignUpException extends AuthenticationException {

    private static final long serialVersionUID = 1078063791016707032L;
    @Getter
    private ErrorCodeEnum errorCodeEnum;

    public OAuth2SignUpException(ErrorCodeEnum errorCodeEnum, Throwable t) {
        super(errorCodeEnum.getMsg(), t);
        this.errorCodeEnum = errorCodeEnum;
    }

    public OAuth2SignUpException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMsg());
        this.errorCodeEnum = errorCodeEnum;
    }
}
