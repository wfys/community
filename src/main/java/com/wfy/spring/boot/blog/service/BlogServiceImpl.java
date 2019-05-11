package com.wfy.spring.boot.blog.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.wfy.spring.boot.blog.domain.Blog;
import com.wfy.spring.boot.blog.domain.Catalog;
import com.wfy.spring.boot.blog.domain.Comment;
import com.wfy.spring.boot.blog.domain.Spend;
import com.wfy.spring.boot.blog.domain.User;
import com.wfy.spring.boot.blog.domain.Vote;
import com.wfy.spring.boot.blog.repository.BlogRepository;

/**
 * Blog 服务实现.
 */
@Service
public class BlogServiceImpl implements BlogService {
	
	@Autowired
	private BlogRepository blogRepository;
	
	@Autowired
	private UserService userService;
	
	@Transactional
	@Override
	public Blog saveBlog(Blog blog) {
		Blog returnBlog = blogRepository.save(blog);
		return returnBlog;
	}
	
	@Transactional
	@Override
	public void removeBlog(Long id) {
		blogRepository.deleteById(id);
	}
	
	@Transactional
	@Override
	public Blog updateBlog(Blog blog) {
		return blogRepository.save(blog);
	}

	@Override
	public Blog getBlogById(Long id) {
		return blogRepository.getOne(id);
	}
	
	@Override
	public void readingIncrease(Long id) {
		Blog blog = blogRepository.getOne(id);
		blog.setReadSize(blog.getReadSize()+1);
		blogRepository.save(blog);
	}

	@Override
	public Blog createComment(Long blogId, String commentContent) {
		Blog originalBlog = blogRepository.getOne(blogId);
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		Comment comment = new Comment(user, commentContent);
		originalBlog.addComment(comment);
		return blogRepository.save(originalBlog);
	}

	@Override
	public void removeComment(Long blogId, Long commentId) {
		Blog originalBlog = blogRepository.getOne(blogId);
		originalBlog.removeComment(commentId);
		blogRepository.save(originalBlog);
	}

	@Override
	public Blog createVote(Long blogId) {
		Blog originalBlog = blogRepository.getOne(blogId);
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		Vote vote = new Vote(user);
		boolean isExist = originalBlog.addVote(vote);
		if (isExist) {
			throw new IllegalArgumentException("该用户已经点过赞了");
		}
		return blogRepository.save(originalBlog);
	}

	@Override
	public void removeVote(Long blogId, Long voteId) {
		Blog originalBlog = blogRepository.getOne(blogId);
		originalBlog.removeVote(voteId);
		blogRepository.save(originalBlog);
	}

	@Override
	public Blog createSpend(Long blogId, Integer gold) {
		Blog originalBlog = blogRepository.getOne(blogId);
		User consumer = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Spend spend = new Spend(gold,consumer);
		User user=originalBlog.getUser();
		spend.setUser(user);
		spend.setWebsiteInfo("打赏："+originalBlog.getTitle());
		spend.setWebsite("/blog/detail/"+originalBlog.getId());
		originalBlog.addSpend(spend);
		user.setGold(user.getGold()+gold);
		consumer.setGold(consumer.getGold()-gold);
		userService.saveUser(user);
		userService.saveUser(consumer);
		originalBlog=blogRepository.save(originalBlog);
		return originalBlog;
	}

	@Override
	public void removeSpend(Long blogId, Long spendId) {
		Blog originalBlog = blogRepository.getOne(blogId);
		originalBlog.removeSpend(spendId);
		blogRepository.save(originalBlog);
	}

	//根据用户名和标题进行分页模糊查询（最新）
	@Override
	public Page<Blog> listsByUserAndTitleLike(User user, String title, Pageable pageable) {
		title = "%" + title + "%";
		Page<Blog> blogs = blogRepository.findByUserAndTitleLikeOrderByCreateTimeDesc(user, title, pageable);
		return blogs;
	}

    //根据用户名和标题进行分页模糊查询（最热）
	@Override
	public Page<Blog> listsByUserAndTitleLikeAndSort(User user, String title, Pageable pageable) {
		title = "%" + title + "%";
		Page<Blog> blogs = blogRepository.findByUserAndTitleLike(user, title, pageable);
		return blogs;
	}


	@Override
	public Page<Blog> listsByUserAndCatalogAndTitleLike(User user, Catalog catalog, String title, 
			Pageable pageable) {
		title = "%" + title + "%";
		Page<Blog> blogs = blogRepository.findByUserAndCatalogAndTitleLikeOrderByCreateTimeDesc(user, catalog, 
		    title, pageable);
		return blogs;
	}


	@Override
	public Page<Blog> listsByUserAndCatalogAndTitleLikeAndSort(User user, Catalog catalog, String title,
			Pageable pageable) {
		title = "%" + title + "%";
		Page<Blog> blogs = blogRepository.findByUserAndCatalogAndTitleLike(user, catalog, title, pageable);
		return blogs;
	}


	@Override
	public Page<Blog> listsByTitleLikeOrTagLike(String title, String tags, Pageable pageable) {
		title = "%" + title + "%";
		tags = "%" + tags + "%";
		Page<Blog> blogs = blogRepository.findByTitleLikeOrTagsLikeOrderByCreateTimeDesc(title, tags, 
				pageable);
		return blogs;
	}


	@Override
	public Page<Blog> listsByTitleLikeOrTagLikeAndSort(String title, String tags, Pageable pageable) {
		title = "%" + title + "%";
		tags = "%" + tags + "%";
		Page<Blog> blogs = blogRepository.findByTitleLikeOrTagsLike(title, tags, pageable);
		return blogs;
	}

	@Override
	public Integer findDay(Long day) {
		return blogRepository.findDay(day);
	}

	

}
