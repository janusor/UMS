package top.dcenter.security.social.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.GenericTypeResolver;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SocialAuthenticationServiceRegistry;
import org.springframework.social.security.SpringSocialConfigurer;
import top.dcenter.security.core.api.config.SocialWebSecurityConfigurerAware;
import top.dcenter.security.social.SocialProperties;
import top.dcenter.security.social.api.callback.BaseOAuth2ConnectionFactory;
import top.dcenter.security.social.callback.SocialOAuth2AuthenticationService;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * social 第三方登录核心配置, 如果需要自定义，请实现此类的子类，并注册进 IOC 容器。<br>
 *     如果需要在 {@link #postProcess(Object)} 方法以外添加配置且需要通过 {@link WebSecurityConfigurerAdapter#configure(HttpSecurity)}
 *     配置，则请实现 {@link SocialWebSecurityConfigurerAware} 接口
 * @author zhailiang
 * @medifiedBy  zyw
 * @createdDate 2020-05-09 11:37
 */
@SuppressWarnings("JavadocReference")
@Slf4j
public class SocialCoreConfigurer extends SpringSocialConfigurer {

	private SocialProperties socialProperties;

	public SocialCoreConfigurer(SocialProperties socialProperties) {
		this.socialProperties = socialProperties;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <T> T postProcess(T object) {
		SocialAuthenticationFilter filter = (SocialAuthenticationFilter) super.postProcess(object);

		// 更换 OAuth2AuthenticationService，让新添加的 SocialOAuth2AuthenticationService(通过覆写 buildReturnToUrl(request)方法)
		// 支持统一的回调地址。
		SocialAuthenticationServiceRegistry registry = (SocialAuthenticationServiceRegistry) filter.getAuthServiceLocator();

		// 1. 获取 ConnectionFactories 并另外暂存到 Set
		Set<String> providerIds = registry.registeredProviderIds();
		Set<ConnectionFactory> connectionFactorySet =
				providerIds.stream().map(providerId -> registry.getConnectionFactory(providerId)).collect(Collectors.toSet());
		// 2. 用反射或取 registry 中的 ConnectionFactories 并清空 ConnectionFactories 的值
		Map<Class<?>, String> apiTypeIndex;
		try
		{
			Field connectionFactoriesField = registry.getClass().getSuperclass().getDeclaredField("connectionFactories");
			Field apiTypeIndexField = registry.getClass().getSuperclass().getDeclaredField("apiTypeIndex");
			connectionFactoriesField.setAccessible(true);
			apiTypeIndexField.setAccessible(true);
			Map<String, ConnectionFactory<?>> connectionFactories = (Map<String, ConnectionFactory<?>>) connectionFactoriesField.get(registry);
			apiTypeIndex = (Map<Class<?>, String>) apiTypeIndexField.get(registry);
			connectionFactories.clear();
			apiTypeIndex.clear();
		}
		catch (NoSuchFieldException | IllegalAccessException e)
		{
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
		// 3. 更换 Service 并设置 ConnectionFactory
		for (ConnectionFactory connectionFactory : connectionFactorySet)
		{
			if (connectionFactory instanceof BaseOAuth2ConnectionFactory)
			{
				BaseOAuth2ConnectionFactory baseOAuth2ConnectionFactory = (BaseOAuth2ConnectionFactory) connectionFactory;
				registry.addAuthenticationService(new SocialOAuth2AuthenticationService<>(baseOAuth2ConnectionFactory));
				Class<?> apiType = GenericTypeResolver.resolveTypeArgument(connectionFactory.getClass(), ConnectionFactory.class);
				apiTypeIndex.put(apiType, connectionFactory.getProviderId());
			}

		}

		filter.setFilterProcessesUrl(socialProperties.getFilterProcessesUrl());
		filter.setSignupUrl(socialProperties.getSignUpUrl());
		filter.setDefaultFailureUrl(socialProperties.getFailureUrl());
		return (T) filter;
	}

}