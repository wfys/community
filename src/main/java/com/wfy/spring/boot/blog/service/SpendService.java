package com.wfy.spring.boot.blog.service;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wfy.spring.boot.blog.domain.Spend;
import com.wfy.spring.boot.blog.domain.User;

/**
 * Spend 服务接口.
 * @author wfy
 *
 */
public interface SpendService {
	/**
	 * 保存花费
	 * @param recharge
	 * @return
	 */
	Spend saveSpend(Spend spend);
	
	/**
	 * 删除花费
	 * @param id
	 * @return
	 */
	void removeSpend(Long id);
	
	
	/**
	 * 通过Id获得Spend
	 * @param user
	 * @return
	 */
	Spend getSpendById(Long id);
	
	/**
	 * 获取所有花费
	 * @return
	 */
	List<Spend> listSpends();
	
	/**
	 * 通过消费着查找
	 * @param user
	 * @return
	 */
	Page<Spend> listSpendsByConsumer(User consumer, Pageable pageable);
	
	/**
	 * 通过收入者查找
	 * @param user
	 * @param pageable
	 * @return
	 */
	Page<Spend> listSpendsByUser(User user, Pageable pageable);

}
