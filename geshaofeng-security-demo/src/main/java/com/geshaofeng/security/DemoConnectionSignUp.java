package com.geshaofeng.security;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Component;

/**
 * @author ShaoFeng
 * 作用  授权后 自动注册
 * @see org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository.findUserIdsWithConnection(Connection<?>)
 * 上述方法判断 ConnectionSignUp不为空
 * 则自动执行 自动注册
 */
@Component
public class DemoConnectionSignUp implements ConnectionSignUp {

	@Override
	public String execute(Connection<?> connection) {
		//根据社交用户信息默认创建用户并返回用户唯一标识
		return connection.getDisplayName();
	}

}
