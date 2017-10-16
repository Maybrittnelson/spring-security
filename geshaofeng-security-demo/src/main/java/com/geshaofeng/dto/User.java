package com.geshaofeng.dto;

import java.util.Date;

import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonView;
import com.geshaofeng.validator.MyConstraint;

public class User {
	
	public interface UserSimpleView{}
	public interface UserDetailView extends UserSimpleView{}
	
	public String id;
	@MyConstraint(message = "这是一个测试")
	public String username;
	@NotBlank(message = "密码不能为空")
	public String password;
	@Past(message = "生日必须是当前时间之前")
	public Date birthday;
	
	@JsonView(UserSimpleView.class)
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@JsonView(UserDetailView.class)
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@JsonView(UserSimpleView.class)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@JsonView(UserSimpleView.class)
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	
	
	
}
