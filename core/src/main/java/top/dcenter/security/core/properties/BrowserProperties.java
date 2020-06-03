package top.dcenter.security.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.security.config.http.SessionCreationPolicy;
import top.dcenter.security.core.enums.LoginProcessType;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_LOGIN_PAGE_URL;
import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_FORM;
import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_REMEMBER_ME_NAME;
import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_SESSION_INVALID_URL;
import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_UNAUTHENTICATION_URL;

/**
 * security 网页端配置属性
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/3 19:51
 */
@SuppressWarnings("jol")

@ConfigurationProperties("security.browser")
public class BrowserProperties {

    /**
     * 设置查询表是否创建的 SQL
     */
    public static final String QUERY_REMEMBER_ME_TABLE_EXIST_SQL = "SELECT COUNT(1) FROM information_schema.tables WHERE table_schema='sso-demo' AND table_name = 'persistent_logins'";

    private final SessionProperties session = new SessionProperties();

    private final RememberMeProperties rememberMe = new RememberMeProperties();

    /**
     * 设置登录页，用户没有配置则默认为 /security/login.html
     */
    private String loginPage = DEFAULT_LOGIN_PAGE_URL;
    /**
     * 设置处理登录表单的 uri，不需要用户实现此 uri，由 Spring security 自动实现， 默认为 /authentication/form
     */
    private String loginProcessingUrl = DEFAULT_LOGIN_PROCESSING_URL_FORM;
    /**
     * 设置认证失败默认跳转页面
     */
    private String failureUrl = this.loginPage;
    /**
     * 错误页面
     */
    private String errorUrl = "/error";
    /**
     * 4xx 错误页面
     */
    private String error4Url = "/4*.html";
    /**
     * 5xx 错误页面
     */
    private String error5Url = "/5*.html";
    /**
     * 设置认证成功默认跳转页面
     */
    private String successUrl = "/";

    /**
     * 当请求需要身份认证时，默认跳转的url
     * 会根据 authJumpSuffixCondition 条件判断的认证处理类型的 url，默认实现 /authentication/require
     */
    private String loginUnAuthenticationUrl = DEFAULT_UNAUTHENTICATION_URL;
    /**
     * 设置 uri 相对应的跳转登录页, 例如：key=/**: value=/security/login.html。 默认为空
     * 支持通配符 规则具体看 AntPathMatcher.match(pattern, path)
     */
    private Map<String, String> authRedirectSuffixCondition;

    /**
     * 设置默认登录后为 返回 JSON
     */
    private LoginProcessType loginProcessType = LoginProcessType.JSON;


    public BrowserProperties() {
        this.authRedirectSuffixCondition = new HashMap<>();
    }

    public String getQueryRememberMeTableExistSql(String databaseName){
        return "SELECT COUNT(1) FROM information_schema.tables WHERE table_schema='" + databaseName + "' AND table_name = 'persistent_logins'";
    }

    public SessionProperties getSession() {
        return session;
    }

    public RememberMeProperties getRememberMe() {
        return rememberMe;
    }

    public String getLoginPage() {
        return loginPage;
    }

    public void setLoginPage(String loginPage) {
        this.loginPage = loginPage;
    }

    public String getLoginProcessingUrl() {
        return loginProcessingUrl;
    }

    public void setLoginProcessingUrl(String loginProcessingUrl) {
        this.loginProcessingUrl = loginProcessingUrl;
    }

    public String getFailureUrl() {
        return failureUrl;
    }

    public void setFailureUrl(String failureUrl) {
        this.failureUrl = failureUrl;
    }

    public String getErrorUrl() {
        return errorUrl;
    }

    public void setErrorUrl(String errorUrl) {
        this.errorUrl = errorUrl;
    }

    public String getError4Url() {
        return error4Url;
    }

    public void setError4Url(String error4Url) {
        this.error4Url = error4Url;
    }

    public String getError5Url() {
        return error5Url;
    }

    public void setError5Url(String error5Url) {
        this.error5Url = error5Url;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public String getLoginUnAuthenticationUrl() {
        return loginUnAuthenticationUrl;
    }

    public void setLoginUnAuthenticationUrl(String loginUnAuthenticationUrl) {
        this.loginUnAuthenticationUrl = loginUnAuthenticationUrl;
    }

    public Map<String, String> getAuthRedirectSuffixCondition() {
        return authRedirectSuffixCondition;
    }

    public void setAuthRedirectSuffixCondition(Map<String, String> authRedirectSuffixCondition) {
        this.authRedirectSuffixCondition = authRedirectSuffixCondition;
    }

    public LoginProcessType getLoginProcessType() {
        return loginProcessType;
    }

    public void setLoginProcessType(LoginProcessType loginProcessType) {
        this.loginProcessType = loginProcessType;
    }

    public static class SessionProperties {
        /**
         * 当为 false 时允许单个用户拥有任意数量的 session（不同设备或不同浏览器），默认为 false。
         * 当设置 true 时，同时请设置一下选项：maximumSessions 和 maxSessionsPreventsLogin
         */
        private Boolean sessionNumberControl = false;

        /**
         * 当设置为 1 时，maxSessionsPreventsLogin 为 false 时，同个用户登录会自动踢掉上一次的登录状态。
         * 当设置为 1 时，maxSessionsPreventsLogin 为 true 时，同个用户登录会自动自动拒绝用户再登录。
         * 默认为 1。
         * 如要此选项生效，sessionNumberControl 必须为 true
         */
        private int maximumSessions = 1;
        /**
         * 同个用户达到最大 maximumSession 后，当为 true 时自动拒绝用户再登录，默认为 false。
         * 如要此选项生效，sessionNumberControl 必须为 true
         */
        private Boolean maxSessionsPreventsLogin = false;


        /**
         * If set to true,
         * allows HTTP sessions to be rewritten in the URLs when using HttpServletResponse.encodeRedirectURL(String)
         * or HttpServletResponse.encodeURL(String), otherwise disallows HTTP sessions to be included in the URL. This prevents leaking information to external domains. 默认为 false。
         */
        private Boolean enableSessionUrlRewriting = false;
        /**
         * Whether the cookie should be flagged as secure or not. Secure cookies can only be sent over an HTTPS connection and thus cannot be accidentally submitted over HTTP where they could be intercepted.
         * By default the cookie will be secure if the request is secure. If you only want to use remember-me over HTTPS (recommended) you should set this property to true. 默认为 false。
         */
        private Boolean useSecureCookie = false;
        /**
         * Specifies the various session creation policies for Spring Security. 默认为 {@link SessionCreationPolicy.ALWAYS}。
         */
        @SuppressWarnings("JavadocReference")
        private SessionCreationPolicy sessionCreationPolicy = SessionCreationPolicy.ALWAYS;

        /**
         * session 失效后跳转地址, 默认: /session/invalid
         */
        private String invalidSessionUrl = DEFAULT_SESSION_INVALID_URL;

        public Boolean getSessionNumberControl() {
            return sessionNumberControl;
        }

        public void setSessionNumberControl(Boolean sessionNumberControl) {
            this.sessionNumberControl = sessionNumberControl;
        }

        public int getMaximumSessions() {
            return maximumSessions;
        }

        public void setMaximumSessions(int maximumSessions) {
            this.maximumSessions = maximumSessions;
        }

        public Boolean getMaxSessionsPreventsLogin() {
            return maxSessionsPreventsLogin;
        }

        public void setMaxSessionsPreventsLogin(Boolean maxSessionsPreventsLogin) {
            this.maxSessionsPreventsLogin = maxSessionsPreventsLogin;
        }

        public Boolean getEnableSessionUrlRewriting() {
            return enableSessionUrlRewriting;
        }

        public void setEnableSessionUrlRewriting(Boolean enableSessionUrlRewriting) {
            this.enableSessionUrlRewriting = enableSessionUrlRewriting;
        }

        public Boolean getUseSecureCookie() {
            return useSecureCookie;
        }

        public void setUseSecureCookie(Boolean useSecureCookie) {
            this.useSecureCookie = useSecureCookie;
        }

        public SessionCreationPolicy getSessionCreationPolicy() {
            return sessionCreationPolicy;
        }

        public void setSessionCreationPolicy(SessionCreationPolicy sessionCreationPolicy) {
            this.sessionCreationPolicy = sessionCreationPolicy;
        }

        public String getInvalidSessionUrl() {
            return invalidSessionUrl;
        }

        public void setInvalidSessionUrl(String invalidSessionUrl) {
            this.invalidSessionUrl = invalidSessionUrl;
        }
    }

    public static class RememberMeProperties {
        /**
         * 设置记住我功能的 session 的缓存时长，默认 14 天. If a duration suffix is not specified, seconds will be used.
         */
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration rememberMeTimeout = Duration.parse("P14D");

        /**
         * 设置记住我功能的 CookieName，默认 remember-me
         */
        private String rememberMeCookieName = DEFAULT_REMEMBER_ME_NAME;

        public Duration getRememberMeTimeout() {
            return rememberMeTimeout;
        }

        public void setRememberMeTimeout(Duration rememberMeTimeout) {
            this.rememberMeTimeout = rememberMeTimeout;
        }

        public String getRememberMeCookieName() {
            return rememberMeCookieName;
        }

        public void setRememberMeCookieName(String rememberMeCookieName) {
            this.rememberMeCookieName = rememberMeCookieName;
        }
    }

}
