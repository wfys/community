package com.wfy.spring.boot.blog.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.wfy.spring.boot.blog.domain.Comment;
import com.wfy.spring.boot.blog.domain.Follow;
import com.wfy.spring.boot.blog.domain.User;
import com.wfy.spring.boot.blog.domain.Vote;
import com.wfy.spring.boot.blog.repository.CommentRepository;

/**
 * Comment 服务实现.
 * @author wfy
 *
 */
@Service
public class CommentServiceImpl implements CommentService {

	
	@Autowired
	private CommentRepository commentRepository;
	
	@Override
	@Transactional
	public void removeComment(Long id) {
		commentRepository.deleteById(id);
	}
	
	@Override
	public Comment getCommentById(Long id) {
		return commentRepository.getOne(id);
	}
	
	@Override
	@Transactional
	public Comment saveCommment(Comment comment) {
		return commentRepository.save(comment);
	}
	
	@Override
	public Comment createVote(Long commentId) {
		Comment originalComment=commentRepository.getOne(commentId);
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		Vote vote = new Vote(user);
		boolean isExist=originalComment.addVote(vote);
		if (isExist) {
			throw new IllegalArgumentException("该用户已经点过赞了");
		}
		return commentRepository.save(originalComment);
	}
	
	@Override
	public void removeVote(Long commentId, Long voteId) {
		Comment originalComment=commentRepository.getOne(commentId);
		originalComment.removeVote(voteId);
		commentRepository.save(originalComment);
	}

}
