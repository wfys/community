package com.wfy.spring.boot.blog.service;
import com.wfy.spring.boot.blog.domain.Follow;

/**
 * follow 服务接口.
 * @author wfy
 *
 */
public interface FollowService {
	//保存Follow
	Follow saveFollow(Follow follow);
	/**
	 * 获取 Follow
	 * @param id
	 * @return
	 */
	Follow getFollowById(Long id);
	/**
	 * 删除 Follow
	 * @param id
	 * @return
	 */
	void removeFollow(Long id);
	/**
	 * 点赞
	 * @param blogId
	 * @return
	 */
	Follow createVote(Long followId);
	/**
	 * 取消点赞
	 * @param blogId
	 * @param voteId
	 * @return
	 */
	void removeVote(Long followId, Long voteId);
}
