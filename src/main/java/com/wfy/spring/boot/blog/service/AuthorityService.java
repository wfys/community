package com.wfy.spring.boot.blog.service;

import com.wfy.spring.boot.blog.domain.Authority;
/**
 * Authority 服务接口
 * @author wfy
 *
 */
public interface AuthorityService {
	/**
	 * 通过Id查找Authority
	 * @param Authority
	 * @return
	 */
	Authority getAuthorityById(Long id);
}
