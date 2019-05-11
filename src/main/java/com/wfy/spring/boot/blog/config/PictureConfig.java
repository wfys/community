package com.wfy.spring.boot.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PictureConfig implements WebMvcConfigurer {
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/imgs/avatar/**").addResourceLocations("file:/root/java/static/imgs/avatar/");
	    registry.addResourceHandler("/imgs/blog/**").addResourceLocations("file:/root/java/static/imgs/blog/");
	    registry.addResourceHandler("/imgs/post/**").addResourceLocations("file:/root/java/static/imgs/post/");
	}
}
