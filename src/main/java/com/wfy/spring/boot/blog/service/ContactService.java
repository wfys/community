package com.wfy.spring.boot.blog.service;

import com.wfy.spring.boot.blog.domain.Contact;
import com.wfy.spring.boot.blog.domain.User;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Contact 服务接口
 * @author wfy
 *
 */
public interface ContactService {
	//保存关注
	Contact saveContact(Contact contact);
	
	//删除关注
	void removeContact(Long id);
	
	//通过Id获得关注
	Contact getContactById(Long id);
	
	//获取所有关注
	List<Contact> listContacts();
	
	//查找是否关注
	Contact findByNoticerAndUser(User noticer,User user);
	
	//查找关注
	Page<Contact> listContactsByNoticer(User noticer, Pageable pageable);
	
	//查找粉丝
	Page<Contact> listContactsByUser(User user, Pageable pageable);
}
