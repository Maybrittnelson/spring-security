package com.geshaofeng.security.core.social.qq.api;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ShaoFeng
 * 第六步 获取用户信息的行为
 * 
 * AbstractOAuth2ApiBinding
 * 		private final String accessToken;//令牌多实例
 * 		private RestTemplate restTemplate;//发送http请求获取用户信息
 * 注意：
 * 	不得将此类声明Component 这个类是多例的 对应多个用户有多个token
 */
public class QQImpl extends AbstractOAuth2ApiBinding implements QQ {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private static final String URL_GET_OPENID = "https://graph.qq.com/oauth2.0/me?access_token=%s";
	
	private static final String URL_GET_USERINFO = "https://graph.qq.com/user/get_user_info?oauth_consumer_key=%s&openid=%s";
	
	/**
	 * 第三方应用Id
	 */
	private String appId;
	/**
	 * 获取用户qq对应的openId provideUserId
	 */
	private String openId;
	
	/**
	 * 将string字符串对象 转换成对象
	 */
	private ObjectMapper objectMapper = new ObjectMapper();
	
	public QQImpl(String accessToken, String appId) {
		//默认父类的一个参数构造函数 是将token放在请求头位置 而qq需要将token当成参数
		super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
		
		this.appId = appId;
		
		//将accessToken 替换%s
		String url = String.format(URL_GET_OPENID, accessToken);
		//发送请求 获取当前登录人用户opendId
		//http://wiki.connect.qq.com/%E8%8E%B7%E5%8F%96%E7%94%A8%E6%88%B7openid_oauth2-0
		String result = getRestTemplate().getForObject(url, String.class);
		
		logger.info(result);
		
		this.openId = StringUtils.substringBetween(result, "\"openid\":\"", "\"}");
	}
	
	@Override
	public QQUserInfo getUserInfo() {
		
		String url = String.format(URL_GET_USERINFO, appId, openId);
		String result = getRestTemplate().getForObject(url, String.class);
		
		System.out.println(result);
		
		QQUserInfo userInfo = null;
		try {
			userInfo = objectMapper.readValue(result, QQUserInfo.class);
			userInfo.setOpenId(openId);
			return userInfo;
		} catch (Exception e) {
			throw new RuntimeException("获取用户信息失败", e);
		}
	}
}
