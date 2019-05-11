package com.wfy.spring.boot.blog.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wfy.spring.boot.blog.domain.Recharge;
import com.wfy.spring.boot.blog.domain.User;
import com.wfy.spring.boot.blog.repository.RechargeRepository;
/**
 * Recharge 服务实现.
 * @author wfy
 *
 */
@Service
public class RechargeServiceImpl implements RechargeService{
	
	@Autowired
	private RechargeRepository rechargeRepository;
	@Transactional
	@Override
	public Recharge saveRecharge(Recharge recharge) {
		return rechargeRepository.save(recharge);
	}

	@Transactional
	@Override
	public void removeRecharge(Long id) {
		rechargeRepository.deleteById(id);	
	}

	@Override
	public Recharge getRechargeById(Long id) {
		return rechargeRepository.getOne(id);
	}

	@Override
	public List<Recharge> listRecharges() {
		return rechargeRepository.findAll();
	}

	@Override
	public Page<Recharge> listRechargesByUser(User user, Pageable pageable) {
		Page<Recharge> recharge=rechargeRepository.findByUserOrderByCreateTimeDesc(user, pageable);
		return recharge;
	}

	


}
