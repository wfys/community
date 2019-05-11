package com.wfy.spring.boot.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wfy.spring.boot.blog.domain.Vote;

/**
 * 点赞仓库
 * @author wfy
 *
 */
public interface VoteRepository extends JpaRepository<Vote, Long>{
	 
}

