package com.wfy.spring.boot.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wfy.spring.boot.blog.domain.Authority;
import com.wfy.spring.boot.blog.repository.AuthorityRepository;
/**
 * Authority 服务实现
 * @author wfy
 *
 */
@Service
public class AuthorityServiceImpl implements AuthorityService {

	@Autowired
	private AuthorityRepository authorityRepository;
	
	@Override
	public Authority getAuthorityById(Long id) {
		return authorityRepository.getOne(id);
	}

}
