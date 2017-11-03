package com.geshaofeng.security.core.social.qq.connet;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;

import com.geshaofeng.security.core.social.qq.api.QQ;
import com.geshaofeng.security.core.social.qq.api.QQUserInfo;

/**
 * @author ShaoFeng
 * 获取用户信息 与connection中用户信息的适配
 */
public class QQAdapter implements ApiAdapter<QQ> {

	/* (non-Javadoc)
	 * @see org.springframework.social.connect.ApiAdapter#test(java.lang.Object)
	 * 测试QQ服务是否还能连接
	 */
	@Override
	public boolean test(QQ api) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.social.connect.ApiAdapter#setConnectionValues(java.lang.Object, org.springframework.social.connect.ConnectionValues)
	 * 适配connection中的用户信息
	 */
	@Override
	public void setConnectionValues(QQ api, ConnectionValues values) {
		QQUserInfo userInfo = api.getUserInfo();
		
		values.setDisplayName(userInfo.getNickname());
		values.setImageUrl(userInfo.getFigureurl_qq_1());
		values.setProfileUrl(null);//个人主页
		values.setProviderUserId(userInfo.getOpenId());
	}

	/* (non-Javadoc)
	 * @see org.springframework.social.connect.ApiAdapter#fetchUserProfile(java.lang.Object)
	 * 稍后的绑定用户 会介绍
	 */
	@Override
	public UserProfile fetchUserProfile(QQ api) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.springframework.social.connect.ApiAdapter#updateStatus(java.lang.Object, java.lang.String)
	 * 类似发个message 去更新微博
	 */
	@Override
	public void updateStatus(QQ api, String message) {
		//do noting
	}
}
