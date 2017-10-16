package com.geshaofeng.security.browser.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geshaofeng.security.core.properties.LoginResponseType;
import com.geshaofeng.security.core.properties.SecurityProperties;

@Component("geshaofengAuthenticationSuccessHandler")
public class GeshaofengAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private SecurityProperties securityProperties;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		logger.info("登录成功");
		
		if (LoginResponseType.JSON.equals(securityProperties.getBrowser().getLoginType())) {
			response.setContentType("application/json;charsest=UTF-8");
			response.getWriter().write(objectMapper.writeValueAsString(authentication));
		} else {
			super.onAuthenticationSuccess(request, response, authentication);
		}
	}

}
