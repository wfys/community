package com.wfy.spring.boot.blog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wfy.spring.boot.blog.domain.Post;
import com.wfy.spring.boot.blog.domain.User;

/**
 * Post 服务接口.
 * @author wfy
 *
 */
public interface PostService {
	
	//保存跟帖
	Post savePost(Post post);
	
	//删除跟帖
	void removePost(Long id);
	
	//更新跟帖
	Post updatePost(Post post);
	
	//获取Post
	Post getPostById(Long id);
	
	//阅读量递增
	void readingIncrease(Long id);
	
	//添加跟帖
	Post createFollow(Long postId, String followContent);
	
	//删除跟帖
	void removeFollow(Long postId, Long followId);
	
	//根据用户名和标题或标签进行分页模糊查询（最新）
	Page<Post> listsByUserAndTitleLikeOrTagLike(User user, String title,String tags, Pageable pageable);
	
	//根据用户名和标题或标签进行分页模糊查询（最热）
	Page<Post> listsByUserAndTitleLikeOrTagLikeAndSort(User user, String title,String tags, Pageable pageable);
	
	//根据标题或标签进行分页模糊查询（最新）
	Page<Post> listsByTitleLikeOrTagLike(String title, String tags, Pageable pageable);
	
	//根据标题或标签进行分页模糊查询（最热）
	Page<Post> listsByTitleLikeOrTagLikeAndSort(String title, String tags, Pageable pageable);
	
	//统计某天前的帖子量
	Integer findDay(Long day);
	
}
