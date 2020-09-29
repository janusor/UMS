package demo.validate.code.slider;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.core.api.validate.code.ValidateCodeGenerator;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeType;
import top.dcenter.ums.security.core.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.exception.ValidateCodeException;
import top.dcenter.ums.security.core.properties.ValidateCodeProperties;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.VALIDATE_CODE_EXPIRED;
import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.VALIDATE_CODE_FAILURE;
import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.VALIDATE_CODE_NOT_EMPTY;

/**
 * @author zyw
 * @version V1.0  Created by 2020/9/21 12:32
 */
@Component
@Slf4j
public class SliderValidateCodeGenerator implements ValidateCodeGenerator<SliderCode> {


    /**
     * request token param name
     */
    public static final String TOKEN_PARAM_NAME = "token";

    /**
     * request X param name
     */
    public static final String X_PARAM_NAME = "x";

    /**
     * request Y param name
     */
    public static final String Y_PARAM_NAME = "y";


    private final SliderCodeFactory sliderCodeFactory;

    private final ValidateCodeProperties validateCodeProperties;

    public SliderValidateCodeGenerator(SliderCodeFactory sliderCodeFactory, ValidateCodeProperties validateCodeProperties) {
        this.sliderCodeFactory = sliderCodeFactory;
        this.validateCodeProperties = validateCodeProperties;
    }

    @Override
    public SliderCode generate(ServletRequest request) {

        SliderCode sliderCode = sliderCodeFactory.getSliderCode();
        log.info("Demo =====>: {} = {}", getValidateCodeType(),
                  sliderCode);
        return sliderCode;
    }

    @Override
    public String getValidateCodeType() {
        return ValidateCodeType.SLIDER.name().toLowerCase();
    }

    @Override
    public String getRequestParamValidateCodeName() {
        // 前端把第一次验证通过后的 token 设置到请求参数名称为 sliderToken 上.
        return validateCodeProperties.getSlider().getRequestParamName();
    }

    @SuppressWarnings({"ConstantConditions", "AlibabaLowerCamelCaseVariableNaming"})
    @Override
    public void validate(ServletWebRequest request) throws ValidateCodeException {
        // 获取 session 中的 SliderCode
        HttpServletRequest req = request.getRequest();
        String sessionKey = ValidateCodeType.SLIDER.getSessionKey();
        HttpSession session = req.getSession();
        SliderCode sliderCodeInSession = (SliderCode) session.getAttribute(sessionKey);

        // 检查 session 是否有值
        if (sliderCodeInSession == null)
        {
            throw new ValidateCodeException(VALIDATE_CODE_EXPIRED, req.getRemoteAddr(), request.getSessionId());
        }

        // 检测是否是第二此校验
        if (sliderCodeInSession.getSecondCheck())
        {
            defaultValidate(request);
            return;
        }

        // 获取 request 中的验证码
        String token = request.getParameter(TOKEN_PARAM_NAME);
        String x = request.getParameter(X_PARAM_NAME);
        String y = request.getParameter(Y_PARAM_NAME);

        // 校验参数是否有效
        checkParam(sessionKey, session, req, !StringUtils.isNotBlank(token), VALIDATE_CODE_NOT_EMPTY, TOKEN_PARAM_NAME);
        checkParam(sessionKey, session, req, !StringUtils.isNotBlank(x), VALIDATE_CODE_NOT_EMPTY, X_PARAM_NAME);
        checkParam(sessionKey, session, req, !StringUtils.isNotBlank(y), VALIDATE_CODE_NOT_EMPTY, Y_PARAM_NAME);

        token = token.trim();
        Integer locationX = Integer.parseInt(x);
        Integer locationY = Integer.parseInt(y);


        // 校验是否过期
        checkParam(sessionKey, session, req, sliderCodeInSession.isExpired(), VALIDATE_CODE_EXPIRED, token);

        // 验证码校验
        boolean verify = sliderCodeInSession.getLocationY().equals(locationY) && Math.abs(sliderCodeInSession.getLocationX() - locationX) < 2;
        if (!verify)
        {
            if (!sliderCodeInSession.getReuse())
            {
                session.removeAttribute(sessionKey);
            }
            throw new ValidateCodeException(VALIDATE_CODE_FAILURE, req.getRemoteAddr(), token);
        }

        // 更新 session 中的验证码信息, 以便于第二次校验
        sliderCodeInSession.setSecondCheck(true);
        // 方便二次校验时, 调用 ValidateCodeGenerator.defaultValidate 方法.
        sliderCodeInSession.setCode(sliderCodeInSession.getToken());
        // 这里第一次校验通过, 第二次校验不需要使用复用功能, 不然第二次校验时不会清除 session 中的验证码缓存
        sliderCodeInSession.setReuse(false);

        session.setAttribute(sessionKey, sliderCodeInSession);

    }

    /**
     * 根据 condition 是否删除 session 指定 sessionKey 缓存, 并抛出异常
     * @param sessionKey        sessionKey
     * @param session           session
     * @param req               req
     * @param condition         condition
     * @param errorCodeEnum     errorCodeEnum
     * @param errorData         errorData
     * @throws ValidateCodeException ValidateCodeException
     */
    private void checkParam(String sessionKey, HttpSession session, HttpServletRequest req, boolean condition,
                            ErrorCodeEnum errorCodeEnum, String errorData) throws ValidateCodeException {
        if (condition)
        {
            // 按照逻辑是前端过滤无效参数, 如果进入此逻辑, 按非正常访问处理
            session.removeAttribute(sessionKey);
            throw new ValidateCodeException(errorCodeEnum, req.getRemoteAddr(), errorData);
        }
    }
}
