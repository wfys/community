package com.wfy.spring.boot.blog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wfy.spring.boot.blog.domain.Post;
import com.wfy.spring.boot.blog.domain.User;

/**
 * Post 仓库.
 * @author wfy
 *
 */
public interface PostRepository extends JpaRepository<Post, Long> {
	//根据用户名和标题或标签分页查询博客列表（逆时）
	Page<Post> findByUserAndTitleLikeOrUserAndTagsLikeOrderByCreateTimeDesc(User user0, String title,User user1,String tags, Pageable pageable);
		
	//根据用户名和标题或标签分页查询博客列表
	Page<Post> findByUserAndTitleLikeOrUserAndTagsLike(User user0, String title,User user1,String tags, Pageable pageable);
	
	//根据标题或标签分页查询博客列表（逆时）
	Page<Post> findByTitleLikeOrTagsLikeOrderByCreateTimeDesc(String title,String tags,Pageable pageable);
		
	//根据标题或标签分页查询博客列表
	Page<Post> findByTitleLikeOrTagsLike(String title,String tags,Pageable pageable);
	
	//统计某天前的帖子量
	@Query(value = "select count(*) from post where to_days(now())-to_days(create_time)<= ?1", nativeQuery = true)
	Integer findDay(Long day);

}
