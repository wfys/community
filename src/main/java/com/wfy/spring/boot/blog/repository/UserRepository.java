package com.wfy.spring.boot.blog.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.wfy.spring.boot.blog.domain.User;
/**
 * 用户仓库
 * @author wfy
 *
 */
public interface UserRepository extends JpaRepository<User, Long>{
	/**
	 * 根据用户名分页查询用户列表
	 * @param name
	 * @param pageable
	 * @return
	 */
	Page<User> findByUsernameLike(String username, Pageable pageable);
	
	User findByUsername(String username);
	/**
	 * 根据名称列表查询
	 * @param usernames
	 * @return
	 */
	List<User> findByUsernameIn(Collection<String> usernames);
}
