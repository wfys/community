package com.wfy.spring.boot.blog.service;

import com.wfy.spring.boot.blog.domain.Comment;

/**
 *  Comment 服务接口.
 * @author wfy
 *
 */
public interface CommentService {
	Comment saveCommment(Comment comment);
	/**
	 * 获取Comment
	 * @param id
	 * @return
	 */
	Comment getCommentById(Long id);
	/**
	 * 删除Comment
	 * @param id
	 * @return
	 */
	void removeComment(Long id);
	
	/**
	 * 点赞
	 * @param blogId
	 * @return
	 */
	Comment createVote(Long commentId);
	/**
	 * 取消点赞
	 * @param blogId
	 * @param voteId
	 * @return
	 */
	void removeVote(Long commentId, Long voteId);
}
