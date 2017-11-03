package com.geshaofeng.security.core.social.qq.connet;

import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;

import com.geshaofeng.security.core.social.qq.api.QQ;
import com.geshaofeng.security.core.social.qq.api.QQImpl;

/**
 * @author ShaoFeng
 * QQ认证服务器
 * 	
 */
public class QQServiceProvider extends AbstractOAuth2ServiceProvider<QQ> {
	
	private String appId;
	
	/**
	 * 申请授权码
	 */
	private static final String URL_AUTHORIZE = "https://graph.qq.com/oauth2.0/authorize";
	/**
	 * 申请令牌
	 */
	private static final String URL_ACCESS_TOKEN = "https://graph.qq.com/oauth2.0/token";
	
	public QQServiceProvider(String appId, String appSecret) {
		//创建自定义的QQAuth2Template
		super(new QQOAuth2Template(appId, appSecret, URL_AUTHORIZE, URL_ACCESS_TOKEN));
		this.appId = appId;
	}
	
	@Override
	public QQ getApi(String accessToken) {
		return new QQImpl(accessToken, appId);
	}
}
