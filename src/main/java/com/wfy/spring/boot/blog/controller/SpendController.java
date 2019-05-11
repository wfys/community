package com.wfy.spring.boot.blog.controller;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.wfy.spring.boot.blog.domain.Blog;
import com.wfy.spring.boot.blog.domain.Spend;
import com.wfy.spring.boot.blog.service.BlogService;
import com.wfy.spring.boot.blog.service.SpendService;
import com.wfy.spring.boot.blog.util.ConstraintViolationExceptionHandler;
import com.wfy.spring.boot.blog.vo.Response;

/**
 * 打赏 控制器
 * @author wfy
 *
 */
@Controller
@RequestMapping("/spends")
public class SpendController {
	@Autowired
	private BlogService blogService;
	
	@Autowired
	private SpendService spendService;
	
	/**
	 * 获取打赏列表
	 * @param blogId
	 * @param model
	 * @return
	 */
	@GetMapping
	public ModelAndView listComments(@RequestParam(value="blogId",required=true) Long blogId, Model model) {
		Blog blog = blogService.getBlogById(blogId);
		List<Spend> spends = blog.getSpends();
		model.addAttribute("spends",  spends);
		return new ModelAndView("blogDetail :: #mainSpendsRepleace","model", model);
	}
	/**
	 * 提交打赏
	 * @param blogId
	 * @param commentContent
	 * @return
	 */
	@PostMapping
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  //
	public ResponseEntity<Response> createComment(Long blogId, Integer gold) {
		try {
			blogService.createSpend(blogId, gold);
		} catch (ConstraintViolationException e)  {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}
		
		return ResponseEntity.ok().body(new Response(true, "打赏成功", null));
	}
}
