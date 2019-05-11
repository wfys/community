package com.wfy.spring.boot.blog.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alipay.api.AlipayApiException;
import com.wfy.spring.boot.blog.domain.Recharge;
import com.wfy.spring.boot.blog.domain.Spend;
import com.wfy.spring.boot.blog.domain.User;
import com.wfy.spring.boot.blog.vo.Response;
import com.wfy.spring.boot.blog.service.Alipay;
import com.wfy.spring.boot.blog.service.RechargeService;
import com.wfy.spring.boot.blog.service.SpendService;
import com.wfy.spring.boot.blog.service.UserService;
import com.wfy.spring.boot.blog.util.AlipayBean;

/**
 * 个人中心控制器
 * @author wfy
 */
@Controller
@RequestMapping("/personal")
public class PersonalController {

	@Autowired
	private UserService userService;
	@Autowired
	private RechargeService rechargeService;
	@Autowired
	private SpendService spendService;
	@Autowired
	private Alipay alipay;

	@GetMapping("/{username}")
	public ModelAndView userSpace(Model model) {
		User principal=null;
		boolean isAdmin=false;
		// 判断操作用户是否登录
		if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
			&&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
			principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		}
		if(principal!=null&&principal.getAuthorities().toString().contains("ROLE_ADMIN")){
			isAdmin=true;
		}
		model.addAttribute("headTitle", "个人中心");
		model.addAttribute("isAdmin", isAdmin);
		return new ModelAndView("users/center", "model", model);
	}

	/**
	 * 获取个人基本信息
	 * @param username
	 * @param model
	 * @return
	 */
	@GetMapping("/{username}/info")
	@PreAuthorize("authentication.name.equals(#username)")
	public ModelAndView getInfo(@PathVariable("username") String username, Model model) {
		User user = userService.getUserByUserName(username);
		model.addAttribute("user", user);
		return new ModelAndView("users/center_info", "model", model);
	}

	/**
	 * 获取个人设置界面
	 * @param username
	 * @param model
	 * @return
	 */
	@GetMapping("/{username}/edit")
	@PreAuthorize("authentication.name.equals(#username)")
	public ModelAndView profile(@PathVariable("username") String username, Model model) {
		User user = userService.getUserByUserName(username);
		model.addAttribute("user", user);
		return new ModelAndView("users/center_edit", "model", model);
	}

	/**
	 * 保存个人设置 
	 * @param user
	 * @param result
	 * @param redirect
	 * @return
	 */
	@PostMapping("/{username}/edit")
	@PreAuthorize("authentication.name.equals(#username)")
	public String saveProfile(@PathVariable("username") String username, User user) {
		User originalUser = userService.getUserById(user.getId());
		originalUser.setEmail(user.getEmail());
		originalUser.setName(user.getName());
		// 判断密码是否做了变更
		String rawPassword = originalUser.getPassword();
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodePasswd = encoder.encode(user.getPassword());
		boolean isMatch = encoder.matches(rawPassword, encodePasswd);
		if (!isMatch) {
			originalUser.setEncodePassword(user.getPassword());
		}

		userService.saveUser(originalUser);
		return "redirect:/personal/" + username;
	}

	/**
	 * 编辑头像
	 * @param username
	 * @param model
	 * @return
	 */
	@GetMapping("/{username}/phto")
	@PreAuthorize("authentication.name.equals(#username)")
	public ModelAndView getPhto(@PathVariable("username") String username, Model model) {
		User user = userService.getUserByUserName(username);
		model.addAttribute("user", user);
		return new ModelAndView("users/center_phto", "model", model);
	}

	/**
	 * 获取编辑头像的弹出界面 
	 * @param username
	 * @param model
	 * @return
	 */
	@GetMapping("/{username}/avatar")
	@PreAuthorize("authentication.name.equals(#username)")
	public ModelAndView avatar(@PathVariable("username") String username, Model model) {
		User user = userService.getUserByUserName(username);
		model.addAttribute("user", user);
		return new ModelAndView("users/center_avatar", "model", model);
	}

	/**
	 * 保存头像
	 * @param username
	 * @param model
	 * @return
	 */
	@PostMapping("/{username}/avatar")
	@PreAuthorize("authentication.name.equals(#username)")
	public ResponseEntity<Response> saveAvatar(@PathVariable("username") String username,
			@RequestParam("file") MultipartFile file) {
		if (!file.isEmpty()) {
			// 获取文件名称,包含后缀
			String fileName = file.getOriginalFilename();
			String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
			fileName = username + "." + suffix;
			// 存放在这个路径下：该路径是该工程目录下的static文件下：(注：该文件可能需要自己创建)
			// 放在static下的原因是，存放的是静态文件资源，即通过浏览器输入本地服务器地址，加文件名时是可以访问到的
			//String path = ClassUtils.getDefaultClassLoader().getResource("").getPath() + "static/imgs/avatar/";
			String path = "static/imgs/avatar/";
			try {

				File targetfile = new File(path);
				if (!targetfile.exists()) {
					targetfile.mkdirs();
				}
				// 二进制流写入
				FileOutputStream out = new FileOutputStream(path + fileName);
				out.write(file.getBytes());
				out.flush();
				out.close();
				User user = userService.getUserByUserName(username);
				user.setAvater("/imgs/avatar/" + fileName);
				userService.saveUser(user);

				return ResponseEntity.ok().body(new Response(true, "图片上传成功", user.getAvater()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ResponseEntity.ok().body(new Response(false, "图片上传失败"));
			}
		} else {
			return ResponseEntity.ok().body(new Response(false, "图片上传失败"));
		}
	}
	
	/**
	 * 充值金币
	 * @param model
	 * @return
	 */
	@GetMapping("/{username}/alipay")
	public ModelAndView alipayPage(@PathVariable("username") String username, Model model) {
		User user = userService.getUserByUserName(username);
		model.addAttribute("user", user);
		return new ModelAndView("users/center_alipay", "model", model);
	}
	/**
	 * 调用支付宝API
	 * @param username
	 * @param total_amount
	 * @return
	 * @throws AlipayApiException
	 */
	@PostMapping("/{username}/alipay")
	@ResponseBody
	// String outTradeNo, String subject, String totalAmount, String body
	public String alipay(@PathVariable("username") String username, @RequestParam("total_amount") String total_amount
			) throws AlipayApiException {
		User user = userService.getUserByUserName(username);
		Recharge recharge=new Recharge(total_amount,false);
		recharge.setUser(user);
		rechargeService.saveRecharge(recharge);
		String outTradeNo=new Date().getTime()+"_"+recharge.getId();
		AlipayBean alipayBean=new AlipayBean(outTradeNo, "乐乐社区充值", total_amount, "支付宝充值金币");
		String str= alipay.pay(alipayBean);
		return str;
	}
	
	/**
	 * 保存充值结果
	 * @param username
	 * @param model
	 * @return
	 */
	@GetMapping("/alipayResult")
	public ModelAndView alipayResult(@RequestParam("total_amount") String total_amount,
			@RequestParam("out_trade_no") String out_trade_no,Model model) {
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String message="";
		out_trade_no=out_trade_no.split("_")[1];
		if(out_trade_no==null||total_amount==null){
			total_amount="0.00";
			out_trade_no="";
			message="订单不存在";
		}else{
			Long id=Long.parseLong(out_trade_no);
			Recharge recharge=rechargeService.getRechargeById(id);
			if(recharge!=null&&!recharge.getPay()){
				Integer newGold=user.getGold()+(int)(10*Double.parseDouble(total_amount));
				user.setGold(newGold);
				userService.saveUser(user);
				recharge.setPay(true);
				rechargeService.saveRecharge(recharge);
				message="支付成功";
			}else{
				message="订单已支付";
			}
		}
		model.addAttribute("headTitle", "充值结果");
		model.addAttribute("total_amount", total_amount);
		model.addAttribute("out_trade_no", out_trade_no);
		model.addAttribute("message", message);
		model.addAttribute("user", user);
		return new ModelAndView("users/center_result", "model", model);
	}
	
	/**
	 * 获取花费记录
	 * @param username
	 * @param model
	 * @return
	 */
	@GetMapping("/{username}/recharge")
	@PreAuthorize("authentication.name.equals(#username)")
	public ModelAndView getRecharge(@PathVariable("username") String username, 
			@RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
			@RequestParam(value="pageSize",required=false,defaultValue="20") int pageSize,
			Model model) {
		User user = userService.getUserByUserName(username);
		Pageable pageable =new PageRequest(pageIndex, pageSize);
		Page<Recharge> page=rechargeService.listRechargesByUser(user, pageable);
		List<Recharge> list = page.getContent();	// 当前所在页面数据列表
		model.addAttribute("page", page);
		model.addAttribute("rechargeList", list);
		model.addAttribute("urlInfo", "/personal/" +username+"/recharge");
		return new ModelAndView("users/center_recharge", "model", model);
	}
	
	/**
	 * 获取消费记录
	 * @param username
	 * @param model
	 * @return
	 */
	@GetMapping("/{username}/spend")
	@PreAuthorize("authentication.name.equals(#username)")
	public ModelAndView getSpend(@PathVariable("username") String username, 
			@RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
			@RequestParam(value="pageSize",required=false,defaultValue="20") int pageSize,
			Model model) {
		User consumer = userService.getUserByUserName(username);
		Pageable pageable =new PageRequest(pageIndex, pageSize);
		Page<Spend> page=spendService.listSpendsByConsumer(consumer, pageable);
		List<Spend> list = page.getContent();	// 当前所在页面数据列表
		model.addAttribute("page", page);
		model.addAttribute("spendList", list);
		model.addAttribute("urlInfo", "/personal/" +username+"/spend");
		return new ModelAndView("users/center_spend", "model", model);
	}
	
	/**
	 * 获取收入记录
	 * @param username
	 * @param model
	 * @return
	 */
	@GetMapping("/{username}/income")
	@PreAuthorize("authentication.name.equals(#username)")
	public ModelAndView getIncome(@PathVariable("username") String username, 
			@RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
			@RequestParam(value="pageSize",required=false,defaultValue="20") int pageSize,
			Model model) {
		User user = userService.getUserByUserName(username);
		Pageable pageable =new PageRequest(pageIndex, pageSize);
		Page<Spend> page=spendService.listSpendsByUser(user, pageable);
		List<Spend> list = page.getContent();	// 当前所在页面数据列表
		model.addAttribute("page", page);
		model.addAttribute("spendList", list);
		model.addAttribute("urlInfo", "/personal/" +username+"/income");
		return new ModelAndView("users/center_income", "model", model);
	}
}
