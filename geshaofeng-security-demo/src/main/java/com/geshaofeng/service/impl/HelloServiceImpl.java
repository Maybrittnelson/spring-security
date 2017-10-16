package com.geshaofeng.service.impl;

import com.geshaofeng.service.HelloService;

public class HelloServiceImpl implements HelloService {

	@Override
	public String greeting(String name) {
		System.out.println("greeting");
		return "hello"+name;
	}

}
