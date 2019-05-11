package com.wfy.spring.boot.blog.controller;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.wfy.spring.boot.blog.domain.Catalog;
import com.wfy.spring.boot.blog.domain.Contact;
import com.wfy.spring.boot.blog.domain.User;
import com.wfy.spring.boot.blog.service.ContactService;
import com.wfy.spring.boot.blog.service.UserService;
import com.wfy.spring.boot.blog.util.ConstraintViolationExceptionHandler;
import com.wfy.spring.boot.blog.vo.Response;

/**
 * 关注控制器
 * @author wfy
 *
 */
@Controller
@RequestMapping("/contacts")
public class ContactController {
	
	@Autowired
	private ContactService contactService;
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/{username}") 
	public ModelAndView userSpace(@PathVariable("username") String username,Model model) {
		User  user = userService.getUserByUserName(username);
		User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		model.addAttribute("headTitle", "个人空间-关注");
		model.addAttribute("user", user);
		model.addAttribute("principal", principal);
		return new ModelAndView("userspace/center-contact", "model", model);
	}
	
	//关注者
	@GetMapping("/noticer/{username}")
	public ModelAndView listContacts(@PathVariable("username") String username,
			@RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
			@RequestParam(value="pageSize",required=false,defaultValue="20") int pageSize,Model model) {
		
		Boolean isOwner=false;
		User  user = userService.getUserByUserName(username);
		User noticer = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(user.getUsername().equals( noticer.getUsername())){
			isOwner=true;
		}
		Page<Contact> page = null;
		Pageable pageable = new PageRequest(pageIndex, pageSize);
		page = contactService.listContactsByNoticer(user, pageable);
		List<Contact> list= page.getContent();
		model.addAttribute("urlInfo", "/contacts/noticer/" +username);
		model.addAttribute("contactList", list);
		model.addAttribute("user", user);
		model.addAttribute("page", page);
		model.addAttribute("isOwner",isOwner);
		
		return new ModelAndView("userspace/contactManage","model", model);
	}
	
	//粉丝
	@GetMapping("/user/{username}")
	public ModelAndView listUsers(@PathVariable("username") String username,
		    @RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
		    @RequestParam(value="pageSize",required=false,defaultValue="20") int pageSize,Model model) {
			
		User  user = userService.getUserByUserName(username);
		Page<Contact> page = null;
		Pageable pageable = new PageRequest(pageIndex, pageSize);
		page = contactService.listContactsByUser(user, pageable);
		List<Contact> list= page.getContent();
		model.addAttribute("urlInfo", "/contacts/user/" +username);
		model.addAttribute("contactList", list);
		model.addAttribute("user", user);
		model.addAttribute("page", page);
			
		return new ModelAndView("userspace/noticerManage","model", model);
	}
	
	//添加关注
	@PostMapping("/add/{username}")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
	public ResponseEntity<Response> createContact(@PathVariable("username") String username) {
		try {
			User  user = userService.getUserByUserName(username);
			User noticer = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if(user.getUsername().equals( noticer.getUsername())){
				return ResponseEntity.ok().body(new Response(true, "自己不能关注自己", null));
			}
			Contact org=contactService.findByNoticerAndUser(noticer, user);
			if(org!=null){
				return ResponseEntity.ok().body(new Response(true, "已关注", null));
			}
			Contact contact=new Contact(noticer, user);
			contactService.saveContact(contact);
		} catch (ConstraintViolationException e)  {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}
		
		return ResponseEntity.ok().body(new Response(true, "关注成功", null));
	}
	
	//取消关注
	@PostMapping("/cancel/{username}")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
	public ResponseEntity<Response> cancelContact(@PathVariable("username") String username) {
		try {
			User  user = userService.getUserByUserName(username);
			User noticer = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Contact org=contactService.findByNoticerAndUser(noticer, user);
			if(org == null){
				return ResponseEntity.ok().body(new Response(false, "你没有关注Ta", null));
			}
			System.out.println(org.getId());
			contactService.removeContact(org.getId());
		} catch (ConstraintViolationException e)  {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}
		return ResponseEntity.ok().body(new Response(true, "取消关注成功", null));
	}
}
