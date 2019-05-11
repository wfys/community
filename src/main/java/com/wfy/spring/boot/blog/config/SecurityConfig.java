package com.wfy.spring.boot.blog.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;


/**
 * Spring Security 配置类.(安全配置类)
 * @author wfy
 *
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // 启用方法安全设置
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String KEY = "wfy.com";
	
	@Autowired
	private UserDetailsService userDetailsService;

	//记住我
	@Autowired    
	private DataSource dataSource;     
	@Bean    
	public PersistentTokenRepository persistentTokenRepository() {        
		JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();       
		tokenRepository.setDataSource(dataSource);       
		return tokenRepository;
	}
	
	// 密码加密方式
	@Bean  
	public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();   // 使用 BCrypt 加密
    }  
	
	@Bean  
    public AuthenticationProvider authenticationProvider() {  
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setPasswordEncoder(passwordEncoder()); // 设置密码加密方式
		authenticationProvider.setUserDetailsService(userDetailsService);
        return authenticationProvider;  
    }  
 
	/**
	 * 自定义配置
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/css/**", "/js/**", "/fonts/**", "/index").permitAll() // 都可以访问
				.antMatchers("/h2-console/**").permitAll()  // 都可以访问
				.antMatchers("/admins/**").hasRole("ADMIN")  // 需要相应的角色才能访问
				.and()
				.formLogin()
				.loginPage("/login") //基于 Form 表单登录验证
		        .loginProcessingUrl("/login")
		        .defaultSuccessUrl("/index").failureUrl("/login-error") // 自定义登录界面
				.and()
				.rememberMe().tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(24*60*60)  // 过期秒数                     
                .userDetailsService(userDetailsService)
				.and().
				exceptionHandling().accessDeniedPage("/login");  // 处理异常，拒绝访问就重定向到 403 页面
		http.csrf().ignoringAntMatchers("/h2-console/**"); // 禁用 H2 控制台的 CSRF 防护
		http.headers().frameOptions().sameOrigin(); // 允许来自同一来源的H2 控制台的请求
		
	}
	
	/**
	 * 认证信息管理
	 * @param auth
	 * @throws Exception
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		//auth.userDetailsService(userDetailsService);
		auth.authenticationProvider(authenticationProvider());
	}

}
