package com.wfy.spring.boot.blog.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.wfy.spring.boot.blog.domain.Follow;
import com.wfy.spring.boot.blog.domain.Post;
import com.wfy.spring.boot.blog.domain.User;
import com.wfy.spring.boot.blog.repository.PostRepository;

/**
 * Post 服务实现.
 * @author wfy
 *
 */
@Service
public class PostServiceImpl implements PostService {
	
	@Autowired
	private PostRepository postRepository;

	@Override
	@Transactional
	public Post savePost(Post post) {
		return postRepository.save(post);
	}

	@Override
	@Transactional
	public void removePost(Long id) {
		postRepository.deleteById(id);
	}

	@Override
	@Transactional
	public Post updatePost(Post post) {
		return postRepository.save(post);
	}

	@Override
	public Post getPostById(Long id) {
		return postRepository.getOne(id);
	}

	@Override
	public void readingIncrease(Long id) {
		Post post=postRepository.getOne(id);
		post.setReadSize(post.getReadSize()+1);
		postRepository.save(post);
	}

	@Override
	public Post createFollow(Long postId, String followContent) {
		Post originalPost=postRepository.getOne(postId);
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		Follow follow=new Follow(user, followContent);
		originalPost.addFollow(follow);
		return postRepository.save(originalPost);
	}

	@Override
	public void removeFollow(Long postId, Long followId) {
		Post originalPost=postRepository.getOne(postId);
		originalPost.removeFollow(followId);
		postRepository.save(originalPost);
	}

	@Override
	public Page<Post> listsByUserAndTitleLikeOrTagLike(User user, String title, String tags, Pageable pageable) {
		title = "%" + title + "%";
		tags = "%" + tags + "%";
		Page<Post> posts=postRepository.findByUserAndTitleLikeOrUserAndTagsLikeOrderByCreateTimeDesc(user, title, user,tags, pageable);
		return posts;
	}

	@Override
	public Page<Post> listsByUserAndTitleLikeOrTagLikeAndSort(User user, String title, String tags, Pageable pageable) {
		title = "%" + title + "%";
		tags = "%" + tags + "%";
		Page<Post> posts=postRepository.findByUserAndTitleLikeOrUserAndTagsLike(user, title, user,tags, pageable);
		return posts;
	}

	@Override
	public Page<Post> listsByTitleLikeOrTagLike(String title, String tags, Pageable pageable) {
		title = "%" + title + "%";
		tags = "%" + tags + "%";
		Page<Post> posts=postRepository.findByTitleLikeOrTagsLikeOrderByCreateTimeDesc(title, tags, pageable);
		return posts;
	}

	@Override
	public Page<Post> listsByTitleLikeOrTagLikeAndSort(String title, String tags, Pageable pageable) {
		title = "%" + title + "%";
		tags = "%" + tags + "%";
		Page<Post> posts=postRepository.findByTitleLikeOrTagsLike(title, tags, pageable);
		return posts;
	}

	@Override
	public Integer findDay(Long day) {
		return postRepository.findDay(day);
	}

}
