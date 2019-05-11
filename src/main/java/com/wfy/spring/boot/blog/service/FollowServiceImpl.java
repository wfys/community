package com.wfy.spring.boot.blog.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.wfy.spring.boot.blog.domain.Follow;
import com.wfy.spring.boot.blog.domain.User;
import com.wfy.spring.boot.blog.domain.Vote;
import com.wfy.spring.boot.blog.repository.FollowRepository;

/**
 * follow 服务实现.
 * @author wfy
 *
 */
@Service
public class FollowServiceImpl implements FollowService {
	
	@Autowired
	private FollowRepository followRepository;

	@Override
	public Follow getFollowById(Long id) {
		return followRepository.getOne(id);
	}

	@Override
	@Transactional
	public void removeFollow(Long id) {
		followRepository.deleteById(id);
	}

	@Override
	@Transactional
	public Follow createVote(Long followId) {
		Follow originalFollow=followRepository.getOne(followId);
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		Vote vote = new Vote(user);
		boolean isExist=originalFollow.addVote(vote);
		if (isExist) {
			throw new IllegalArgumentException("该用户已经点过赞了");
		}
		return followRepository.save(originalFollow);
	}

	@Override
	public void removeVote(Long followId, Long voteId) {
		Follow originalFollow=followRepository.getOne(followId);
		originalFollow.removeVote(voteId);
		followRepository.save(originalFollow);

	}

	@Override
	@Transactional
	public Follow saveFollow(Follow follow) {
		return followRepository.save(follow);
	}

}
