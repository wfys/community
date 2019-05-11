package com.wfy.spring.boot.blog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.wfy.spring.boot.blog.domain.Post;
import com.wfy.spring.boot.blog.domain.User;
import com.wfy.spring.boot.blog.domain.Vote;
import com.wfy.spring.boot.blog.service.PostService;

/***
 * post控制器
 * @author wfy
 *
 */
@Controller
@RequestMapping("/post")
public class PostController {
	@Autowired
	private PostService postService;
	@GetMapping
	public ModelAndView listsPosts(
			@RequestParam(value="order",required=false,defaultValue="new") String order,
			@RequestParam(value="keyword",required=false,defaultValue="" ) String keyword,
			@RequestParam(value="async",required=false) boolean async,
			@RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
			@RequestParam(value="pageSize",required=false,defaultValue="20") int pageSize,
			Model model) {
 
		Page<Post> page = null;
		if(order.equals("hot")){  // 最热查询
			Sort sort = new Sort(Direction.DESC,"readSize","followSize","spendGold");
			Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
			page=postService.listsByTitleLikeOrTagLikeAndSort(keyword, keyword, pageable);
			
		}else if(order.equals("new")){  // 最新查询
			Pageable pageable = new PageRequest(pageIndex, pageSize);
			page=postService.listsByTitleLikeOrTagLike(keyword, keyword, pageable);
		}
		List<Post> list = page.getContent();
		model.addAttribute("order", order);
		model.addAttribute("keyword", keyword);
		model.addAttribute("page", page);
		model.addAttribute("postList", list);
		model.addAttribute("headTitle", "论坛");
		return new ModelAndView(async==true? "post :: #mainContainerRepleace":"post","model", model);
	}
	@GetMapping("/detail/{id}")
	public ModelAndView getPostById(@PathVariable("id") Long id, Model model) {
		// 每次读取，简单的可以认为阅读量增加1次
		postService.readingIncrease(id);
		Post post=postService.getPostById(id);
		User principal = null;
		
		// 判断操作用户是否是博客的所有者
		if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
				 &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
			principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		}
		
		model.addAttribute("post",post);
		model.addAttribute("user",post.getUser());
		model.addAttribute("follows",null);
		model.addAttribute("principal",principal);
		model.addAttribute("headTitle", post.getTitle());
		return new ModelAndView("postDetail","model", model);
	}
	
	@GetMapping("/rank")
	public ModelAndView getBlogRank(@RequestParam(value="order",required=false,defaultValue="new") String order,
			@RequestParam(value="pageSize",required=false,defaultValue="10") int pageSize,
			Model model) {
		Page<Post> page = null;
		String title="";
		if (order.equals("hot")) { // 最热查询
			Sort sort = new Sort(Direction.DESC, "readSize", "followSize","spendGold");
			Pageable pageable = new PageRequest(0, pageSize, sort);
			page = postService.listsByTitleLikeOrTagLikeAndSort("", "", pageable);
			title="最热帖子";
		} else if (order.equals("new")) { // 最新查询
			Pageable pageable = new PageRequest(0, pageSize);
			page = postService.listsByTitleLikeOrTagLike("", "", pageable);
			title="最新帖子";
		}
		List<Post> list = page.getContent();
		model.addAttribute("postRank",list);
		model.addAttribute("rankTitle",title);
		return new ModelAndView("fragments/postRank","model", model);
	}
	
}
