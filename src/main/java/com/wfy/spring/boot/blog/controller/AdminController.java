package com.wfy.spring.boot.blog.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.wfy.spring.boot.blog.service.BlogService;
import com.wfy.spring.boot.blog.service.PostService;
import com.wfy.spring.boot.blog.util.ConstraintViolationExceptionHandler;
import com.wfy.spring.boot.blog.vo.Menu;
import com.wfy.spring.boot.blog.vo.Response;

/***
 * 后台控制
 * @author wfy
 *
 */
@Controller
@RequestMapping("/admins")
public class AdminController {
	
	@Autowired
	private BlogService blogService;
	
	@Autowired
	private PostService postService;
	
	/**
	 * 后台管理
	 * @return
	 */
	@GetMapping
	public ModelAndView listUsers(Model model) {
		List<Menu> list = new ArrayList<>();
		list.add(new Menu("管理首页", "/admins/echart"));
		list.add(new Menu("用户管理", "/users"));
		list.add(new Menu("博客管理", "/create/adminBlog"));
		list.add(new Menu("论坛管理", "/create/adminPost"));
		model.addAttribute("list", list);
		model.addAttribute("headTitle", "后台管理");
		return new ModelAndView("admins/index", "model", model);
	}
	
	@GetMapping("/echart")
	public ModelAndView showEchrts(Model model) {
		return new ModelAndView("admins/echart", "model", model);
	}
	
	@GetMapping("/echart/blogDay")
	public ResponseEntity<Response> showEchrtBlogDays(Model model) {
		int [] days=new int[7];
		try {
			for(int i=0;i<7;i++){
				days[i]=blogService.findDay((long) i);
			}
		} catch (ConstraintViolationException e)  {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}
		
		return ResponseEntity.ok().body(new Response(true, "处理成功", days));
	}
	
	@GetMapping("/echart/postDay")
	public ResponseEntity<Response> showEchrtPostDays(Model model) {
		int [] days=new int[7];
		try {
			for(int i=0;i<7;i++){
				days[i]=postService.findDay((long) i);
			}
		} catch (ConstraintViolationException e)  {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}
		
		return ResponseEntity.ok().body(new Response(true, "处理成功", days));
	}
}
