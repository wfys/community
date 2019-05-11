package com.wfy.spring.boot.blog.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wfy.spring.boot.blog.domain.Recharge;
import com.wfy.spring.boot.blog.domain.User;
/**
 * 充值 仓库.
 * @author wfy
 *
 */
public interface RechargeRepository extends JpaRepository<Recharge, Long>{
	/**
	 *通过User查找充值(最新)
	 * @param name
	 * @param pageable
	 * @return
	 */
	Page<Recharge> findByUserOrderByCreateTimeDesc(User user, Pageable pageable);
}
