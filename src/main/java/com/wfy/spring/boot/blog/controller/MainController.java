package com.wfy.spring.boot.blog.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.wfy.spring.boot.blog.domain.Authority;
import com.wfy.spring.boot.blog.domain.User;
import com.wfy.spring.boot.blog.service.AuthorityService;
import com.wfy.spring.boot.blog.service.UserService;

/***
 * 主页控制器
 * @author wfy
 *
 */
@Controller
public class MainController {
	
	private static final Long ROLE_USER_AUTHORITY_ID = 2L;
	
	@Autowired
	private UserService userService;
	@Autowired
	private AuthorityService  authorityService;
	
	@GetMapping("/")
	public String root() {
		return "redirect:/index";
	}
	/*
	 * 首页
	 */
	@GetMapping("/index")
	public ModelAndView index(Model model) {
		model.addAttribute("headTitle", "首页");
		return new ModelAndView("index","model", model);
	}
	/**
	 * 获取登录界面
	 * @return
	 */
	@GetMapping("/login")
	public ModelAndView login(Model model) {
		model.addAttribute("headTitle", "登录");
		return new ModelAndView("login","model", model);
	}

	@GetMapping("/login-error")
	public ModelAndView loginError(Model model) {
		model.addAttribute("loginError", true);
		model.addAttribute("errorMsg", "登录失败，账号或者密码错误！");
		model.addAttribute("headTitle", "登录失败");
		return new ModelAndView("login","model", model);
	}
	
	@GetMapping("/register")
	public ModelAndView register(Model model) {
		model.addAttribute("headTitle", "注册");
		return new ModelAndView("register","model", model);
	}
	
	@GetMapping("/register-error")
	public ModelAndView registerError(Model model) {
		model.addAttribute("headTitle", "注册失败");
		model.addAttribute("errorMsg", "注册失败，账号已经被注册！");
		return new ModelAndView("register","model", model);
	}
	
	/**
	 * 注册用户
	 * @param user
	 * @param result
	 * @param redirect
	 * @return
	 */
	@PostMapping("/register")
	public String registerUser(User user) {
		User oldUser=userService.getUserByUserName(user.getUsername());
		if(oldUser!=null){
			return "redirect:/register-error";
		}
		List<Authority> authorities = new ArrayList<>();
		authorities.add(authorityService.getAuthorityById(ROLE_USER_AUTHORITY_ID));
		user.setAuthorities(authorities);
		user.setEncodePassword(user.getPassword()); // 加密密码
		userService.saveUser(user);
		return "redirect:/login";
	}
    
	@GetMapping("/search")
	public String search() {
		return "search";
	}
}
