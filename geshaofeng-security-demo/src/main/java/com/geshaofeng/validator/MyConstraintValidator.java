package com.geshaofeng.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.geshaofeng.service.HelloService;

public class MyConstraintValidator implements ConstraintValidator<MyConstraint, Object> {
	
	@Autowired
	private HelloService service;
	
	@Override
	public void initialize(MyConstraint arg0) {
		System.out.println("my validator init");
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext arg1) {
		System.out.println(value);
		service.greeting("tom");
		return true;
	}


}
