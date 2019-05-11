package com.wfy.spring.boot.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wfy.spring.boot.blog.domain.Authority;
/**
 * Authority 仓库.
 * @author wfy
 *
 */
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

}
