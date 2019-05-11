package com.wfy.spring.boot.blog.controller;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.wfy.spring.boot.blog.domain.Catalog;
import com.wfy.spring.boot.blog.domain.User;
import com.wfy.spring.boot.blog.service.CatalogService;
import com.wfy.spring.boot.blog.service.UserService;
import com.wfy.spring.boot.blog.util.ConstraintViolationExceptionHandler;
import com.wfy.spring.boot.blog.vo.CatalogVO;
import com.wfy.spring.boot.blog.vo.Response;

/**
 * 分类管理
 * @author wfy
 *
 */
@Controller
@RequestMapping("/catalog")
public class CatalogController {
	
	@Autowired
	private CatalogService catalogService;
	
	@Autowired
	private UserService userService;
	/**
	 * 获取分类列表
	 * @param blogId
	 * @param model
	 * @return
	 */
	@GetMapping("/{username}")
	@PreAuthorize("authentication.name.equals(#username)")
	public ModelAndView listCatalogs(@PathVariable("username") String username,Model model) {
		User user = userService.getUserByUserName(username);
		List<Catalog> catalogs = catalogService.listCatalogs(user);
		model.addAttribute("user", user);
		model.addAttribute("catalogs", catalogs);
		return new ModelAndView("creates/catalogManage","model", model);
	}
	/**
	 * 保存分类
	 * @param blogId
	 * @param commentContent
	 * @return
	 */
	@PostMapping("/{username}")
	public ResponseEntity<Response> create(@RequestBody CatalogVO catalogVO) {
		String username = catalogVO.getUsername();
		Catalog catalog = catalogVO.getCatalog();
		User user = userService.getUserByUserName(username);
		try {
			catalog.setUser(user);
			catalogService.saveCatalog(catalog);
		} catch (ConstraintViolationException e)  {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}
		
		return ResponseEntity.ok().body(new Response(true, "处理成功", null));
	}
	
	/**
	 * 删除分类
	 * @return
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Response> delete(@PathVariable("id") Long id) {
		try {
			catalogService.removeCatalog(id);
		} catch (ConstraintViolationException e)  {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}
		
		return ResponseEntity.ok().body(new Response(true, "处理成功", null));
	}
	
	/**
	 * 添加分类
	 * @param id
	 * @param model
	 * @return
	 */
	@GetMapping("/add")
	public ModelAndView getCatalogEdit(Model model) {
		Catalog catalog = new Catalog(null, null);
		model.addAttribute("catalog",catalog);
		return new ModelAndView("creates/catalogedit","userModel", model);
	}
	/**
	 * 编辑分类
	 * @param id
	 * @param model
	 * @return
	 */
	@GetMapping("/edit/{id}")
	public ModelAndView getCatalogById(@PathVariable("id") Long id, Model model) {
		Catalog catalog = catalogService.getCatalogById(id);
		model.addAttribute("catalog",catalog);
		return new ModelAndView("userspace/catalogedit","userModel", model);
	}
	
}
