package com.geshaofeng.security.core;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.geshaofeng.security.core.properties.SecurityProperties;

/**
 * @author ShaoFeng
 * {@link @Configuration} 声明成Bean工厂类
 * {@link @EnableConfigurationProperties} 声明成配置类 可读取SecurityProperties
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityCoreConfig {

	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();//返回bcrypt密码编码方式
	}
}
