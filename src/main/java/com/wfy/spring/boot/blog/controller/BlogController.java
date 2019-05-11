package com.wfy.spring.boot.blog.controller;

import java.util.ArrayList;
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

import com.wfy.spring.boot.blog.domain.Blog;
import com.wfy.spring.boot.blog.domain.User;
import com.wfy.spring.boot.blog.domain.Vote;
import com.wfy.spring.boot.blog.service.BlogService;

import com.wfy.spring.boot.blog.vo.TagVO;

/***
 * blog控制器
 * @author wfy
 *
 */
@Controller
@RequestMapping("/blog")
public class BlogController {
	@Autowired
	private BlogService blogService;
	@GetMapping
	public ModelAndView listsBlogs(
			@RequestParam(value="order",required=false,defaultValue="new") String order,
			@RequestParam(value="keyword",required=false,defaultValue="" ) String keyword,
			@RequestParam(value="async",required=false) boolean async,
			@RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
			@RequestParam(value="pageSize",required=false,defaultValue="20") int pageSize,
			Model model) {
 
		Page<Blog> page = null;
		if(order.equals("hot")){  // 最热查询
			Sort sort = new Sort(Direction.DESC,"readSize","commentSize","likeSize");
			Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
			page=blogService.listsByTitleLikeOrTagLikeAndSort(keyword, keyword, pageable);
			
		}else if(order.equals("new")){  // 最新查询
			Pageable pageable = new PageRequest(pageIndex, pageSize);
			page=blogService.listsByTitleLikeOrTagLike(keyword, keyword, pageable);
		}
		List<Blog> list = page.getContent();
		model.addAttribute("order", order);
		model.addAttribute("keyword", keyword);
		model.addAttribute("page", page);
		model.addAttribute("blogList", list);
		model.addAttribute("headTitle", "博客");
		return new ModelAndView(async==true? "blog :: #mainContainerRepleace":"blog","model", model);
	}
	@GetMapping("/detail/{id}")
	public ModelAndView getBlogById(@PathVariable("id") Long id, Model model) {
		// 每次读取，简单的可以认为阅读量增加1次
		blogService.readingIncrease(id);
		Blog blog = blogService.getBlogById(id);
		User principal = null;
		
		// 判断操作用户是否是博客的所有者
		if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
				 &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
			principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		}
		// 判断操作用户的点赞情况
		List<Vote> votes = blog.getVotes();
		Vote currentVote = null; // 当前用户的点赞情况
	
		if (principal !=null) {
			for (Vote vote : votes) {
				if(vote.getUser().getUsername().equals(principal.getUsername())){
					currentVote = vote;
					break;
				}
			}
		}
		
		model.addAttribute("blog",blog);
		model.addAttribute("user",blog.getUser());
		model.addAttribute("comments",null);
		model.addAttribute("spends", null);
		model.addAttribute("currentVote",currentVote);
		model.addAttribute("principal",principal);
		model.addAttribute("headTitle", blog.getTitle());
		
		return new ModelAndView("blogDetail","model", model);
	}
	
	@GetMapping("/rank")
	public ModelAndView getBlogRank(@RequestParam(value="order",required=false,defaultValue="new") String order,
			@RequestParam(value="pageSize",required=false,defaultValue="10") int pageSize,
			Model model) {
		Page<Blog> page = null;
		String title="";
		if (order.equals("hot")) { // 最热查询
			Sort sort = new Sort(Direction.DESC, "readSize", "commentSize", "likeSize");
			Pageable pageable = new PageRequest(0, pageSize, sort);
			page = blogService.listsByTitleLikeOrTagLikeAndSort("", "", pageable);
			title="最热博客";
		} else if (order.equals("new")) { // 最新查询
			Pageable pageable = new PageRequest(0, pageSize);
			page = blogService.listsByTitleLikeOrTagLike("", "", pageable);
			title="最新博客";
		}
		List<Blog> list = page.getContent();
		model.addAttribute("blogRank",list);
		model.addAttribute("rankTitle",title);
		return new ModelAndView("fragments/blogRank","model", model);
		
	}
	
}
