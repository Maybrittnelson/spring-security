package com.geshaofeng.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import com.fasterxml.jackson.annotation.JsonView;
import com.geshaofeng.dto.User;
import com.geshaofeng.exception.UserNotExistException;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private ProviderSignInUtils providerSignInUtils;
	
	@PostMapping("/regist")
	public void regist(User user, HttpServletRequest request) {
		//不管是注册用户还是绑定用户，都会拿到一个用户唯一标识。
		String userId = user.getUsername();
		providerSignInUtils.doPostSignUp(userId, new ServletWebRequest(request));
	}
	
	@GetMapping("/me")
	public Object getCurrentUser(@AuthenticationPrincipal UserDetails user) {
		return user;
	}
	@GetMapping
	@JsonView(User.UserSimpleView.class)
	public List<User> listUsers() {
		List<User> list = new ArrayList<User>();
		list.add(new User());
		list.add(new User());
		list.add(new User());
		return list;
	}
	
	@PostMapping
	public User createUser(@Valid @RequestBody User user, BindingResult errors) {
		if (errors.hasErrors()) {
			errors.getAllErrors().stream().forEach(error -> System.out.println(error.getDefaultMessage()));
		}
		System.out.println(user.getBirthday());
		
		user.setId("1");
		return user;
	}
	
	@DeleteMapping("/{id:\\d+}")
	public void deleteUser(@PathVariable String id) {
		System.out.println(id);
	}
	
	@PutMapping("/{id:\\d+}")
	public User updateUser(@Valid @RequestBody User user, BindingResult errors) {
		if (errors.hasErrors()) {
			errors.getAllErrors().stream().forEach(error ->{
				FieldError fieldError = (FieldError)error;
				String errorMessage = fieldError.getField()+" "+error.getDefaultMessage();
				System.out.println(errorMessage);
			});
		}
		System.out.println(user.getBirthday());
		
		user.setId("1");
		return user;
	}
	
	@GetMapping("/{id:\\d+}")
	@JsonView(User.UserDetailView.class)
	public User getUser(@PathVariable String id) {
		throw new UserNotExistException(id);
/*		if (!"1".equals(id)) {
			return null;
		}
		User user = new User();
		user.setUsername("tom");
		user.setPassword("123");
		return user;*/
	}
}
