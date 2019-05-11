package com.wfy.spring.boot.blog.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wfy.spring.boot.blog.domain.Recharge;
import com.wfy.spring.boot.blog.domain.User;

/**
 * Recharge  服务接口.
 * @author wfy
 *
 */
public interface RechargeService {
	/**
	 * 保存充值
	 * @param recharge
	 * @return
	 */
	Recharge saveRecharge(Recharge recharge);
	
	/**
	 * 删除充值
	 * @param id
	 * @return
	 */
	void removeRecharge(Long id);
	
	
	/**
	 * ����id��ȡ��ֵ��¼
	 * @param user
	 * @return
	 */
	Recharge getRechargeById(Long id);
	
	/**
	 * ��ȡ��ֵ��¼�б�
	 * @return
	 */
	List<Recharge> listRecharges();
	
	/**
	 * �����û������з�ҳģ����ѯ
	 * @param user
	 * @return
	 */
	Page<Recharge> listRechargesByUser(User user, Pageable pageable);

}
