package com.wfy.spring.boot.blog.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wfy.spring.boot.blog.domain.Spend;
import com.wfy.spring.boot.blog.domain.User;
/**
 * 花费 仓库.
 * @author wfy
 *
 */
public interface SpendRepository extends JpaRepository<Spend, Long>{
	/**
	 * 通过消费者查找记录（最新）
	 * @param name
	 * @param pageable
	 * @return
	 */
	Page<Spend> findByConsumerOrderByCreateTimeDesc(User consumer, Pageable pageable);
	/**
	 * 通过收入者查找记录（最新）
	 * @param user
	 * @param pageable
	 * @return
	 */
	Page<Spend> findByUserOrderByCreateTimeDesc(User user, Pageable pageable);
}
