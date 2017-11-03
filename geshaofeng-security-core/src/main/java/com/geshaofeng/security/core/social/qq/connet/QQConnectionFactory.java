package com.geshaofeng.security.core.social.qq.connet;

import org.springframework.social.connect.support.OAuth2ConnectionFactory;

import com.geshaofeng.security.core.social.qq.api.QQ;

/**
 * @author ShaoFeng
 * Connection工厂类 负责创建 含有用户信息的Connection
 */
public class QQConnectionFactory extends OAuth2ConnectionFactory<QQ> {

	/**
	 * @param providerId 第三方应用的Id
	 * @param appId
	 * @param appSecret
	 * 存放服务提供商
	 */
	public QQConnectionFactory(String providerId, String appId, String appSecret) {
		super(providerId, new QQServiceProvider(appId, appSecret), new QQAdapter());
	}
}
