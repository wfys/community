package com.wfy.spring.boot.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wfy.spring.boot.blog.domain.Follow;

/**
 * Follow 仓库.
 * @author wfy
 *
 */
public interface FollowRepository extends JpaRepository<Follow, Long> {

}
