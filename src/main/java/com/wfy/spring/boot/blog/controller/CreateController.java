package com.wfy.spring.boot.blog.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.wfy.spring.boot.blog.domain.Blog;
import com.wfy.spring.boot.blog.domain.Catalog;
import com.wfy.spring.boot.blog.domain.Post;
import com.wfy.spring.boot.blog.domain.User;
import com.wfy.spring.boot.blog.service.BlogService;
import com.wfy.spring.boot.blog.service.CatalogService;
import com.wfy.spring.boot.blog.service.PostService;
import com.wfy.spring.boot.blog.service.UserService;
import com.wfy.spring.boot.blog.util.ConstraintViolationExceptionHandler;
import com.wfy.spring.boot.blog.vo.Response;

/**
 * 创作中心控制器
 * @author wfy
 *
 */

@Controller
@RequestMapping("/create")
public class CreateController {
	
	@Autowired
	private UserService userService;
	@Autowired
	private BlogService blogService;
	@Autowired
	private PostService postService;
	@Autowired
	private CatalogService catalogService;
	
	@GetMapping("/{username}") 
	public ModelAndView userSpace(Model model) {
		model.addAttribute("headTitle", "创作中心");
		return new ModelAndView("creates/center", "model", model);
	}
	
	//博客列表
	@GetMapping("/{username}/blogManage")
	@PreAuthorize("authentication.name.equals(#username)")
	public ModelAndView userBlogManage(@PathVariable("username") String username, 
			@RequestParam(value="order",required=false,defaultValue="new") String order,
			@RequestParam(value="catalog",required=false ) Long catalogId,
			@RequestParam(value="keyword",required=false,defaultValue="" ) String keyword,
			@RequestParam(value="async",required=false,defaultValue="false") boolean async,
			@RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
			@RequestParam(value="pageSize",required=false,defaultValue="20") int pageSize,Model model) {
		
		User  user = userService.getUserByUserName(username);
		if(async!=true) {
			List<Catalog> catalogs = catalogService.listCatalogs(user);
			model.addAttribute("catalogs", catalogs);
		}
		Page<Blog> page = null;
		if(catalogId!=null&& catalogId > 0){ // 最热
			Catalog catalog = catalogService.getCatalogById(catalogId);
			if(order.equals("hot")){
				Sort sort = new Sort(Direction.DESC,"readSize","commentSize","likeSize");
				Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
				page = blogService.listsByUserAndCatalogAndTitleLikeAndSort(user, catalog, keyword, pageable);
			}
			else if(order.equals("new")){
				Pageable pageable = new PageRequest(pageIndex, pageSize);
				page=blogService.listsByUserAndCatalogAndTitleLike(user, catalog, keyword, pageable);
			}
		}else if(order.equals("hot")){  // 最热
			Sort sort = new Sort(Direction.DESC,"readSize","commentSize","likeSize");
			Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
			page=blogService.listsByUserAndTitleLikeAndSort(user, keyword, pageable);
			
		}else if(order.equals("new")){  // 最新
			Pageable pageable = new PageRequest(pageIndex, pageSize);
			page=blogService.listsByUserAndTitleLike(user, keyword, pageable);
		}
		List<Blog> list = page.getContent();
		
		model.addAttribute("username", username);
		model.addAttribute("order", order);
		model.addAttribute("catalogId", catalogId);
		model.addAttribute("blogList", list);
		model.addAttribute("user", user);
		model.addAttribute("page", page);
		return new ModelAndView(async==true?"creates/blogManage :: #mainContainerRepleace":"creates/blogManage",
				"model", model);
	}
	
	//获取新增博客的界面
	@GetMapping("/{username}/blogEdit")
	public ModelAndView createBlog(@PathVariable("username") String username,Model model) {
		User user = userService.getUserByUserName(username);
		List<Catalog> catalogs = catalogService.listCatalogs(user);
		
		model.addAttribute("blog", new Blog(null, null, null));
		model.addAttribute("catalogs", catalogs);
		return new ModelAndView("creates/blogedit", "model", model);
	}
	
	// 获取编辑博客的界面
	@GetMapping("/{username}/blogEdit/{id}")
	public ModelAndView editBlog(@PathVariable("username") String username, @PathVariable("id") Long id, 
			Model model) {
		// 获取用户分类列表
		User user = userService.getUserByUserName(username);
		List<Catalog> catalogs = catalogService.listCatalogs(user);
		
		model.addAttribute("blog", blogService.getBlogById(id));
		model.addAttribute("catalogs", catalogs);
		return new ModelAndView("creates/blogedit", "model", model);
	}
	
	//博客保存
	@PostMapping("/{username}/blogEdit")
	@PreAuthorize("authentication.name.equals(#username)") 
	public ResponseEntity<Response> saveBlog(@PathVariable("username") String username, @RequestBody Blog blog) {
		// 对 Catalog 进行空处理
		if (blog.getCatalog().getId() == null) {
				return ResponseEntity.ok().body(new Response(false,"未选择分类"));
		}
		try {
			
			// 判断是修改还是新增
			if (blog.getId()!= null) {
				Blog orignalBlog = blogService.getBlogById(blog.getId());
				orignalBlog.setTitle(blog.getTitle());
				orignalBlog.setContent(blog.getContent());
				orignalBlog.setSummary(blog.getSummary());
				orignalBlog.setCatalog(blog.getCatalog());
				orignalBlog.setTags(blog.getTags());
				blogService.saveBlog(orignalBlog);
			} else {
				User user = userService.getUserByUserName(username);
				blog.setUser(user);
				blogService.saveBlog(blog);
			}
		} catch (ConstraintViolationException e)  {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}
		
		String redirectUrl = "/create/" + username; // + "/blogs/" + blog.getId();
		return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
	}
	
	//删除博客
	@DeleteMapping("/{username}/blogDelete/{id}")
	public ResponseEntity<Response> deleteBlog(@PathVariable("username") String username,@PathVariable("id") Long id) {
		
		try {
			blogService.removeBlog(id);
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}
		String redirectUrl = "/create/" + username;
		return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
	}
	
	//保存博客中的图片
	@PostMapping("/{username}/blogPhoto")
	public ResponseEntity<Response> saveBlogPhoto(@PathVariable("username") String username, @RequestParam("file") MultipartFile file){
		if(!file.isEmpty()) {
			// 获取文件名称,包含后缀				
			String fileName = file.getOriginalFilename();
			String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
			fileName=username+"-"+new Date().getTime()+"."+suffix;
			// 存放在这个路径下：该路径是该工程目录下的static文件下：(注：该文件可能需要自己创建)
			// 放在static下的原因是，存放的是静态文件资源，即通过浏览器输入本地服务器地址，加文件名时是可以访问到的
			//String path = ClassUtils.getDefaultClassLoader().getResource("").getPath()+"static/imgs/blog/";
			String path = "static/imgs/blog/";
			try {

				File targetfile = new File(path);		
				if(!targetfile.exists()) {			
					targetfile.mkdirs();		
				}				
				//二进制流写入				
				FileOutputStream out = new FileOutputStream(path+fileName);	    
				out.write(file.getBytes());	  
				out.flush();	   
				out.close();
				return ResponseEntity.ok().body(new Response(true, "处理成功","/imgs/blog/"+fileName));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ResponseEntity.ok().body(new Response(false, "文件为空"));
			}
		}else{
			return ResponseEntity.ok().body(new Response(false, "文件为空"));
		}
	}
	
	//帖子列表
	@GetMapping("/{username}/postManage")
	@PreAuthorize("authentication.name.equals(#username)")
	public ModelAndView userPostManage(@PathVariable("username") String username,
			@RequestParam(value="order",required=false,defaultValue="new") String order,
			@RequestParam(value="keyword",required=false,defaultValue="" ) String keyword,
			@RequestParam(value="async",required=false,defaultValue="false") boolean async,
			@RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
			@RequestParam(value="pageSize",required=false,defaultValue="20") int pageSize,Model model) {
		
		User  user = userService.getUserByUserName(username);
		Page<Post> page = null;
		if(order.equals("hot")){  // 最热
			Sort sort = new Sort(Direction.DESC,"readSize","followSize","spendGold");
			Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
			page=postService.listsByUserAndTitleLikeOrTagLikeAndSort(user, keyword, keyword, pageable);
			
		}else if(order.equals("new")){  // 最新
			Pageable pageable = new PageRequest(pageIndex, pageSize);
			page=postService.listsByUserAndTitleLikeOrTagLike(user, keyword, keyword, pageable);
		}
		List<Post> list = page.getContent();
		model.addAttribute("username", username);
		model.addAttribute("order", order);
		model.addAttribute("postList", list);
		model.addAttribute("user", user);
		model.addAttribute("page", page);
		
		return new ModelAndView(async==true?"creates/postManage :: #mainContainerRepleace":"creates/postManage",
				"model", model);
	}
	
	//增加帖子
	@GetMapping("/{username}/postEdit")
	public ModelAndView createPost(@PathVariable("username") String username,Model model) {
		User user = userService.getUserByUserName(username);
		model.addAttribute("post", new Post(null, null, null));
		model.addAttribute("user", user);
		return new ModelAndView("creates/postedit", "model", model);
	}
	
	//编辑帖子
	@GetMapping("/{username}/postEdit/{id}")
	public ModelAndView editPost(@PathVariable("username") String username, @PathVariable("id") Long id, 
			Model model) {
		// 获取用户分类列表
		User user = userService.getUserByUserName(username);
		model.addAttribute("post", postService.getPostById(id));
		model.addAttribute("user", user);
		return new ModelAndView("creates/postedit", "model", model);
	}
	//保存帖子
	@PostMapping("/{username}/postEdit")
	@PreAuthorize("authentication.name.equals(#username)") 
	public ResponseEntity<Response> savePost(@PathVariable("username") String username, @RequestBody Post post) {
		try {
			
			//  判断是修改还是新增
			if (post.getId()!= null) {
				Post orignalBlog = postService.getPostById(post.getId());
				User user =orignalBlog.getUser();
				user.setGold(user.getGold()+orignalBlog.getSpendGold()-post.getSpendGold());
				userService.saveUser(user);
				orignalBlog.setTitle(post.getTitle());;
				orignalBlog.setContent(post.getContent());
				orignalBlog.setSpendGold(post.getSpendGold());
				orignalBlog.setTags(post.getTags());
				postService.savePost(orignalBlog);
			} else {
				User user = userService.getUserByUserName(username);
				user.setGold(user.getGold()-post.getSpendGold());
				userService.saveUser(user);
				post.setUser(user);
				postService.savePost(post);
			}
		} catch (ConstraintViolationException e)  {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}
			
		String redirectUrl = "/create/" + username; // + "/blogs/" + blog.getId();
		return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
	}
	//删除帖子
	@DeleteMapping("/{username}/postDelete/{id}")
	public ResponseEntity<Response> deletePost(@PathVariable("username") String username,@PathVariable("id") Long id) {
		try {
			Post post=postService.getPostById(id);
			if(!post.getIsSolve()){
				User user=post.getUser();
				user.setGold(user.getGold()+post.getSpendGold());
				userService.saveUser(user);
			}
			postService.removePost(id);
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}
		String redirectUrl = "/create/" + username;
		return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
	}
	
	//保存帖子中的图片
	@PostMapping("/{username}/postPhoto")
	public ResponseEntity<Response> savePostPhoto(@PathVariable("username") String username, @RequestParam("file") MultipartFile file){
			if(!file.isEmpty()) {
				// 获取文件名称,包含后缀			
				String fileName = file.getOriginalFilename();
				String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
				fileName=username+"-"+new Date().getTime()+"."+suffix;
				// 存放在这个路径下：该路径是该工程目录下的static文件下：(注：该文件可能需要自己创建)
				// 放在static下的原因是，存放的是静态文件资源，即通过浏览器输入本地服务器地址，加文件名时是可以访问到的
				//String path = ClassUtils.getDefaultClassLoader().getResource("").getPath()+"static/imgs/post/";
				String path = "static/imgs/post/";
				try {
					File targetfile = new File(path);		
					if(!targetfile.exists()) {			
						targetfile.mkdirs();		
					}				
					//二进制流写入				
					FileOutputStream out = new FileOutputStream(path+fileName);	    
					out.write(file.getBytes());	  
					out.flush();	   
					out.close();
					return ResponseEntity.ok().body(new Response(true, "处理成功","/imgs/post/"+fileName));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return ResponseEntity.ok().body(new Response(false, "文件为空"));
				}
			}else{
				return ResponseEntity.ok().body(new Response(false, "文件为空"));
			}
		}
	//帮助页面
    @GetMapping("/{username}/help")
    public ModelAndView editBlog(@PathVariable("username") String username, Model model) {
		return new ModelAndView("creates/help", "model", model);
	}
    
    
  //后台管理博客
  	@GetMapping("/adminBlog")
  	public ModelAndView adminBlogManage(
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
		
  		return new ModelAndView(async==true?"admins/adminBlog :: #mainContainerRepleace":"admins/adminBlog",
  				"model", model);
  	}
  	
  	// 后台管理帖子
  	@GetMapping("/adminPost")
  	public ModelAndView adminPostManage(
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
		
		return new ModelAndView(async==true? "admins/adminPost :: #mainContainerRepleace":"admins/adminPost","model", model);
  	}
}
