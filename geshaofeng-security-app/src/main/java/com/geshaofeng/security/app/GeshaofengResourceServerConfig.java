package com.geshaofeng.security.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.social.security.SpringSocialConfigurer;

import com.geshaofeng.security.app.social.openid.OpenIdAuthenticationSecurityConfig;
import com.geshaofeng.security.core.authentication.mobile.SmsCodeAuthenticationSecurityConfig;
import com.geshaofeng.security.core.properties.SecurityConstants;
import com.geshaofeng.security.core.properties.SecurityProperties;
import com.geshaofeng.security.core.validate.code.ValidateCodeSecurityConfig;

@Configuration
@EnableResourceServer
public class GeshaofengResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Autowired
	protected AuthenticationSuccessHandler geshaofengAuthenticationSuccessHandler;
	
	@Autowired
	protected AuthenticationFailureHandler geshaofengAuthenticationFailureHandler;
	
	@Autowired
	private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;
	
	@Autowired
	private ValidateCodeSecurityConfig validateCodeSecurityConfig;
	
	@Autowired
	private SpringSocialConfigurer geshaofengSocialSecurityConfig;
	
	@Autowired
	private SecurityProperties securityProperties;
	
	@Autowired
	private OpenIdAuthenticationSecurityConfig openIdAuthenticationSecurityConfig;
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		
		http.formLogin()
			.loginPage(SecurityConstants.DEFAULT_UNAUTHENTICATION_URL)
			.loginProcessingUrl(SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_FORM)
			.successHandler(geshaofengAuthenticationSuccessHandler)
			.failureHandler(geshaofengAuthenticationFailureHandler);
		
		http.apply(validateCodeSecurityConfig)
				.and()
			.apply(smsCodeAuthenticationSecurityConfig)
				.and()
			.apply(geshaofengSocialSecurityConfig)
				.and()
			.apply(openIdAuthenticationSecurityConfig)
				.and()
			.authorizeRequests()
				.antMatchers(
					SecurityConstants.DEFAULT_UNAUTHENTICATION_URL,
					SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_MOBILE,
					securityProperties.getBrowser().getLoginPage(),
					SecurityConstants.DEFAULT_VALIDATE_CODE_URL_PREFIX+"/*",
					securityProperties.getBrowser().getSignUpUrl(),
					securityProperties.getBrowser().getSession().getSessionInvalidUrl(),
					securityProperties.getBrowser().getSignOutUrl(),
					"/user/regist")
					.permitAll()
				.anyRequest()
				.authenticated()
				.and()
			.csrf().disable();
	}
}
