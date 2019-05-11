package com.wfy.spring.boot.blog.service;

import com.wfy.spring.boot.blog.domain.Vote;

/**
 *  Vote  服务接口.
 * @author wfy
 *
 */
public interface VoteService {
	/**
	 * 保存Vote
	 * @param id
	 * @return
	 */
	Vote getVoteById(Long id);
	/**
	 * 删除Vote
	 * @param id
	 * @return
	 */
	void removeVote(Long id);
}
