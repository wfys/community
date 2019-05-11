package com.wfy.spring.boot.blog.controller;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.wfy.spring.boot.blog.domain.Follow;
import com.wfy.spring.boot.blog.domain.Post;
import com.wfy.spring.boot.blog.domain.Spend;
import com.wfy.spring.boot.blog.domain.User;
import com.wfy.spring.boot.blog.domain.Vote;
import com.wfy.spring.boot.blog.service.FollowService;
import com.wfy.spring.boot.blog.service.PostService;
import com.wfy.spring.boot.blog.service.SpendService;
import com.wfy.spring.boot.blog.service.UserService;
import com.wfy.spring.boot.blog.util.ConstraintViolationExceptionHandler;
import com.wfy.spring.boot.blog.vo.Response;

/**
 * 跟帖控制器
 * @author wfy
 *
 */
@Controller
@RequestMapping("/follow")
public class FollowController {
	@Autowired
	private PostService postService;
	
	@Autowired
	private FollowService followService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SpendService spendService;
	
	/**
	 * 获取跟帖列表
	 * @param postId
	 * @param model
	 * @return
	 */
	@GetMapping
	public ModelAndView listFollows(@RequestParam(value="postId",required=true) Long postId, Model model) {
		Post post=postService.getPostById(postId);
		List<Follow> follows = post.getFollows();
		
		// 判断操作用户是否是跟帖的所有者
		String followOwner = "";
		if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
				 &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
			User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
			if (principal !=null) {
				followOwner = principal.getUsername();
			} 
		}
		String blogOWner = post.getUser().getUsername();
		
		model.addAttribute("followOwner", followOwner);
		model.addAttribute("blogOWner", blogOWner);
		model.addAttribute("follows", follows);
		model.addAttribute("post", post);
		return new ModelAndView("postDetail :: #mainContainerRepleace","model", model);
	}
	/**
	 * 发表跟帖
	 * @param postId
	 * @param commentContent
	 * @return
	 */
	@PostMapping
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
	public ResponseEntity<Response> createFollow(Long postId, String followContent) {
		try {
			postService.createFollow(postId, followContent);
		} catch (ConstraintViolationException e)  {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}
		
		return ResponseEntity.ok().body(new Response(true, "处理成功", null));
	}
	
	/**
	 * 删除跟帖
	 * @return
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Response> deleteFollow(@PathVariable("id") Long id, Long postId) {
		
		boolean isOwner = false;
		User user = followService.getFollowById(id).getUser();
		
		// 判断操作用户是否是博客的所有者
		if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
				 &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
			User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
			if (principal !=null && user.getUsername().equals(principal.getUsername())) {
				isOwner = true;
			} 
		} 
		if (!isOwner) {
			return ResponseEntity.ok().body(new Response(false, "没有操作权限"));
		}
		try {
			postService.removeFollow(postId, id);
			followService.removeFollow(id);
		} catch (ConstraintViolationException e)  {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}
		return ResponseEntity.ok().body(new Response(true, "处理成功", null));
	}
	
	//采纳
	@PostMapping("/accept")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
	public ResponseEntity<Response> isAccept(@RequestParam(value="followId",required=true) Long followId,
			@RequestParam(value="postId",required=true) Long postId) {
		try {
			Follow follow=followService.getFollowById(followId);
			Post post=postService.getPostById(postId);
			User consumer=post.getUser();
			User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if(!consumer.getUsername().equals(principal.getUsername())){
				return ResponseEntity.ok().body(new Response(false, "你不是发帖者不能采纳"));
			}
			post.setIsSolve(true);
			follow.setIsAccept(true);
			User user=follow.getUser();
			Spend spend = new Spend(post.getSpendGold(),consumer);
			spend.setWebsiteInfo("帖子："+post.getTitle());
			spend.setWebsite("/post/detail/"+post.getId());
			user.setGold(user.getGold()+post.getSpendGold());
			spend.setUser(user);
			userService.saveUser(user);
			spendService.saveSpend(spend);
			followService.saveFollow(follow);
			postService.savePost(post);
		} catch (ConstraintViolationException e)  {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}
		
		return ResponseEntity.ok().body(new Response(true, "处理成功", null));
	}
}
