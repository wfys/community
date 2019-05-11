/**
 * 
 */
package com.wfy.spring.boot.blog.service;

import com.wfy.spring.boot.blog.domain.Vote;
import com.wfy.spring.boot.blog.repository.VoteRepository;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * Vote 服务实现.
 * @author wfy
 *
 */
@Service
public class VoteServiceImpl implements VoteService {

	@Autowired
	private VoteRepository voteRepository;
	
	@Override
	@Transactional
	public void removeVote(Long id) {
		voteRepository.deleteById(id);
	}
	@Override
	public Vote getVoteById(Long id) {
		return voteRepository.getOne(id);
	}
}
