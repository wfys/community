package com.wfy.spring.boot.blog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wfy.spring.boot.blog.domain.Contact;
import com.wfy.spring.boot.blog.domain.User;

/**
 * 关注仓库
 * @author wfy
 *
 */
public interface ContactRepository extends JpaRepository<Contact, Long> {
	
	//查找关注
	Page<Contact> findByNoticerOrderByCreateTimeDesc(User noticer, Pageable pageable);
	
	//查找粉丝
	Page<Contact> findByUserOrderByCreateTimeDesc(User user, Pageable pageable);
	
	//查找是否关注
	Contact findByNoticerAndUser(User noticer,User user);
}
