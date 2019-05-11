package com.wfy.spring.boot.blog.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wfy.spring.boot.blog.domain.Spend;
import com.wfy.spring.boot.blog.domain.User;
import com.wfy.spring.boot.blog.repository.SpendRepository;

/**
 * Spend 服务实现.
 * @author wfy
 *
 */
@Service
public class SpendServiceImpl implements SpendService{
	
	@Autowired
	private SpendRepository spendRepository;
	
	@Transactional
	@Override
	public Spend saveSpend(Spend spend) {
		return spendRepository.save(spend);
	}

	@Transactional
	@Override
	public void removeSpend(Long id) {
		spendRepository.deleteById(id);
		
	}

	@Override
	public Spend getSpendById(Long id) {
		return spendRepository.getOne(id);
	}

	@Override
	public List<Spend> listSpends() {
		return spendRepository.findAll();
	}

	@Override
	public Page<Spend> listSpendsByConsumer(User consumer, Pageable pageable) {
		Page<Spend> spends= spendRepository.findByConsumerOrderByCreateTimeDesc(consumer, pageable);
		return spends;
	}

	@Override
	public Page<Spend> listSpendsByUser(User user, Pageable pageable) {
		Page<Spend> spends= spendRepository.findByUserOrderByCreateTimeDesc(user, pageable);
		return spends;
	}
	
	
	


}
