package com.geshaofeng.security.core.social;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.security.SpringSocialConfigurer;

import com.geshaofeng.security.core.properties.SecurityProperties;

/**
 * @author ShaoFeng
 * 社交配置 
 */
@Configuration
@EnableSocial
@Order(10)
public class SocialConfig extends SocialConfigurerAdapter {
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private SecurityProperties securityProperties;
	
	
	@Autowired(required = false)
	private ConnectionSignUp connectionSignUp;
	
	@Autowired(required = false)
	private SocialAuthenticationFilterPostProcessor socialAuthenticationFilterPostProcessor;
	
	/* (non-Javadoc)
	 * 默认: InMemoryUsersConnectionRepository
	 * 	connectionFactoryLocator: 配置的多个连接工厂类 如微信、QQ、weibo
	 * @see org.springframework.social.config.annotation.SocialConfigurerAdapter#getUsersConnectionRepository(org.springframework.social.connect.ConnectionFactoryLocator)
	 */
	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		//3个参数：1数据源 2连接工厂类 3是否对插入数据进行加密, Encryptors.noOpText()不加密 
		JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
		//设置表名前缀  JdbcUsersConnectionRepository所在包名下有张表 JdbcUsersConnectionRepository.sql创建对应的表
		repository.setTablePrefix("security_");
		if(connectionSignUp != null) {
			repository.setConnectionSignUp(connectionSignUp);
		}
		return repository;
	}
	
	/**
	 * @return
	 * 此配置类应用于 SecurityConfig配置链上
	 */
	@Bean
	public SpringSocialConfigurer geshaofengSocialSecurityConfig() {
		String filterProcessesUrl = securityProperties.getSocial().getFilterProcessesUrl();
		//配置拦截路径 默认/auth
		GeshaofengSpringSocialConfigurer configurer = new GeshaofengSpringSocialConfigurer(filterProcessesUrl);
		//在数据库表security_userconnection查找不到用户userId
		//就跳转到下面这个注册url 不使用默认的signup
		configurer.signupUrl(securityProperties.getBrowser().getSignUpUrl());
		configurer.setSocialAuthenticationFilterPostProcessor(socialAuthenticationFilterPostProcessor);
		return configurer;
	}
	
	/**
	 * @param connectionFactoryLocator springBoot 帮我们注入
	 * @return
	 */
	@Bean
	public ProviderSignInUtils providerSignInUtils(ConnectionFactoryLocator connectionFactoryLocator) {
		return new ProviderSignInUtils(connectionFactoryLocator,
				getUsersConnectionRepository(connectionFactoryLocator)) {
		};
	}
}
