package com.geshaofeng.security.core.social.qq.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.social.SocialAutoConfigurerAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.social.connect.ConnectionFactory;

import com.geshaofeng.security.core.properties.QQProperties;
import com.geshaofeng.security.core.properties.SecurityProperties;
import com.geshaofeng.security.core.social.qq.connet.QQConnectionFactory;

/**
 * @author ShaoFeng
 * 自动适配器 找到这个类 创建QQConnectionFactory
 * QQConnectionFactory需要providerId appId appSecret可配置 此时将其放入QQProperties
 * 
 * @ConditionalOnProperty 如果我的系统里.properties或.yml有QQ的app-id 它才进行配置 如果没有就不进行配置
 */
@Configuration
@ConditionalOnProperty(prefix = "geshaofeng.security.social.qq", name = "app-id")
public class QQAutoConfig extends SocialAutoConfigurerAdapter {

	@Autowired
	private SecurityProperties securityProperties;
	
	/* (non-Javadoc)
	 *	configurer.addConnectionFactory(createConnectionFactory());
	 * @see org.springframework.boot.autoconfigure.social.SocialAutoConfigurerAdapter#createConnectionFactory()
	 */
	@Override
	protected ConnectionFactory<?> createConnectionFactory() {
		QQProperties qqConfig = securityProperties.getSocial().getQq();
		return new QQConnectionFactory(qqConfig.getProviderId(), qqConfig.getAppId(), qqConfig.getAppSecret());
	}

}