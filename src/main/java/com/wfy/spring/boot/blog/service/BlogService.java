package com.wfy.spring.boot.blog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wfy.spring.boot.blog.domain.Blog;
import com.wfy.spring.boot.blog.domain.Catalog;
import com.wfy.spring.boot.blog.domain.User;

/**
 * Blog 服务接口
 */
public interface BlogService {
	/**
	 * 保存Blog
	 * @param Blog
	 * @return
	 */
	Blog saveBlog(Blog blog);
	
	/**
	 * 删除Blog
	 * @param id
	 * @return
	 */
	void removeBlog(Long id);
	
	/**
	 * 更新Blog
	 * @param Blog
	 * @return
	 */
	Blog updateBlog(Blog blog);
	
	/**
	 * 根据id获取Blog
	 * @param id
	 * @return
	 */
	Blog getBlogById(Long id);
	
	/**
	 * 阅读量递增
	 * @param id
	 */
	void readingIncrease(Long id);
	
	/**
	 * 发表评论
	 * @param blogId
	 * @param commentContent
	 * @return
	 */
	Blog createComment(Long blogId, String commentContent);
	
	/**
	 * 删除评论
	 * @param blogId
	 * @param commentId
	 * @return
	 */
	void removeComment(Long blogId, Long commentId);
	/**
	 * 点赞
	 * @param blogId
	 * @return
	 */
	Blog createVote(Long blogId);
	/**
	 * 取消点赞
	 * @param blogId
	 * @param voteId
	 * @return
	 */
	void removeVote(Long blogId, Long voteId);
	//打赏
	Blog createSpend(Long blogId, Integer gold);
	//取消打赏
	void removeSpend(Long blogId, Long spendId);
	/**
	 * 根据用户名和标题进行分页模糊查询（最新）
	 * @param user
	 * @return
	 */
	Page<Blog> listsByUserAndTitleLike(User user, String title, Pageable pageable);
 
	/**
	 * 根据用户名和标题进行分页模糊查询（最热）
	 * @param user
	 * @return
	 */
	Page<Blog> listsByUserAndTitleLikeAndSort(User user, String title, Pageable pageable);
	/**
	 * 根据用户名和分类和标题进行分页模糊查询（最新）
	 * @param user
	 * @return
	 */
	Page<Blog> listsByUserAndCatalogAndTitleLike(User user, Catalog catalog, String title, Pageable pageable);
 
	/**
	 * 根据用户名和分类和标题进行分页模糊查询（最热）
	 * @param user
	 * @return
	 */
	Page<Blog> listsByUserAndCatalogAndTitleLikeAndSort(User user, Catalog catalog, String title, Pageable pageable);
	/**
	 * 根据标题和标签进行分页模糊查询（最新）
	 * @param user
	 * @return
	 */
	Page<Blog> listsByTitleLikeOrTagLike(String title, String tags, Pageable pageable);
 
	/**
	 * 根据标题和标签进行分页模糊查询（最热）
	 * @param user
	 * @return
	 */
	Page<Blog> listsByTitleLikeOrTagLikeAndSort(String title, String tags, Pageable pageable);
	
	//统计某天前的搏客量
	Integer findDay(Long day);
}
