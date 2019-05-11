package com.wfy.spring.boot.blog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wfy.spring.boot.blog.domain.Blog;
import com.wfy.spring.boot.blog.domain.Catalog;
import com.wfy.spring.boot.blog.domain.User;

public interface BlogRepository extends JpaRepository<Blog, Long>{
    //根据用户名和标题分页查询博客列表（逆时）
	Page<Blog> findByUserAndTitleLikeOrderByCreateTimeDesc(User user, String title, Pageable pageable);
	
	//根据用户名和标题分页查询博客列表）
    Page<Blog> findByUserAndTitleLike(User user, String title, Pageable pageable);
    
    //根据用户名和分类和标题分页查询博客列表（逆时）
	Page<Blog> findByUserAndCatalogAndTitleLikeOrderByCreateTimeDesc(User user,Catalog catalog,String title,Pageable pageable);
    
	//根据用户名和分类和标题分页查询博客列表
	Page<Blog> findByUserAndCatalogAndTitleLike(User user,Catalog catalog,String title,Pageable pageable);
	
	//根据标题或标签分页查询博客列表（逆时）
	Page<Blog> findByTitleLikeOrTagsLikeOrderByCreateTimeDesc(String title,String tags,Pageable pageable);
	
	//根据标题或标签分页查询博客列表
	Page<Blog> findByTitleLikeOrTagsLike(String title,String tags,Pageable pageable);
	
	//统计某天前的搏客量
	@Query(value = "select count(*) from blog where to_days(now())-to_days(create_time)<= ?1", nativeQuery = true)
	Integer findDay(Long day);
	
}
