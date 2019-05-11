package com.wfy.spring.boot.blog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.wfy.spring.boot.blog.domain.Blog;
import com.wfy.spring.boot.blog.domain.Catalog;
import com.wfy.spring.boot.blog.domain.Post;
import com.wfy.spring.boot.blog.domain.User;
import com.wfy.spring.boot.blog.service.BlogService;
import com.wfy.spring.boot.blog.service.CatalogService;
import com.wfy.spring.boot.blog.service.PostService;
import com.wfy.spring.boot.blog.service.UserService;

/***
 * 个人空间控制器
 * @author wfy
 *
 */
@Controller
@RequestMapping("/u")
public class UserspaceController {
	
	@Autowired
	private UserService userService;
	@Autowired
	private BlogService blogService;
	@Autowired
	private CatalogService catalogService;
	@Autowired
	private PostService postService;
	
	@GetMapping("/{username}") 
	public ModelAndView userSpace(@PathVariable("username") String username,Model model) {
		User  user = userService.getUserByUserName(username);
		List<Catalog> catalogs = catalogService.listCatalogs(user);
		User principal=null;
		// 判断操作用户是否登录
		if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
			&&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
			principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		}
		model.addAttribute("catalogs", catalogs);
		model.addAttribute("headTitle", "个人空间-博客");
		model.addAttribute("user", user);
		model.addAttribute("principal", principal);
		return new ModelAndView("userspace/center", "model", model);
	}
	//博客列表
	@GetMapping("/{username}/blogManage")
	public ModelAndView userBlogManage(@PathVariable("username") String username, 
			@RequestParam(value="catalog",required=false ) Long catalogId,
			@RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
			@RequestParam(value="pageSize",required=false,defaultValue="20") int pageSize,Model model) {
			
		User  user = userService.getUserByUserName(username);
		Page<Blog> page = null;
		if(catalogId!=null&& catalogId > 0){ // 分类查询
			Catalog catalog = catalogService.getCatalogById(catalogId);
			Pageable pageable = new PageRequest(pageIndex, pageSize);
			page=blogService.listsByUserAndCatalogAndTitleLike(user, catalog, "", pageable);
			
		}else if(catalogId == 0){  // 所有
			Pageable pageable = new PageRequest(pageIndex, pageSize);
			page=blogService.listsByUserAndTitleLike(user, "", pageable);	
		}
		List<Blog> list = page.getContent();
		
		model.addAttribute("urlInfo", "/u/" +username+"/blogManage?catalog="+catalogId);
		model.addAttribute("blogList", list);
		model.addAttribute("user", user);
		model.addAttribute("page", page);
		return new ModelAndView("userspace/blogManage","model", model);
	}
	//帖子列表
	@GetMapping("/{username}/postManage")
	public ModelAndView userPostManage(@PathVariable("username") String username,
			@RequestParam(value="async",required=false,defaultValue="false") boolean async,
			@RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
			@RequestParam(value="pageSize",required=false,defaultValue="20") int pageSize,Model model) {
			User  user = userService.getUserByUserName(username);
			User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			Page<Post> page = null;
			Pageable pageable = new PageRequest(pageIndex, pageSize);
			page=postService.listsByUserAndTitleLikeOrTagLike(user, "", "", pageable);
			List<Post> list = page.getContent();
			System.out.println("1:"+list);
			model.addAttribute("username", username);
			model.addAttribute("postList", list);
			model.addAttribute("user", user);
			model.addAttribute("principal", principal);
			model.addAttribute("page", page);
			model.addAttribute("headTitle", "个人空间-论坛");
			
			return new ModelAndView(async==true?"userspace/postManage :: #mainContainerRepleace":"userspace/postManage",
					"model", model);
		}
}
