package com.geshaofeng.security.core.properties;

public class SocialProperties {
	
	/**
	 * 过滤器链 拦截路径前缀 配置
	 */
	private String filterProcessesUrl = "/auth";
	
	private QQProperties qq = new QQProperties();
	
	private WeixinProperties weixin = new WeixinProperties();

	public QQProperties getQq() {
		return qq;
	}

	public void setQq(QQProperties qq) {
		this.qq = qq;
	}
	
	public String getFilterProcessesUrl() {
		return filterProcessesUrl;
	}

	public void setFilterProcessesUrl(String filterProcessesUrl) {
		this.filterProcessesUrl = filterProcessesUrl;
	}

	public WeixinProperties getWeixin() {
		return weixin;
	}

	public void setWeixin(WeixinProperties weixin) {
		this.weixin = weixin;
	}
	
}
