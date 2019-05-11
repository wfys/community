package com.wfy.spring.boot.blog.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wfy.spring.boot.blog.domain.Contact;
import com.wfy.spring.boot.blog.domain.User;
import com.wfy.spring.boot.blog.repository.ContactRepository;

/**
 *  Contact 服务实现
 * @author wfy
 *
 */
@Service
public class ContactServiceImpl implements ContactService {

	@Autowired
	private ContactRepository contactRepository;
	
	@Override
	@Transactional
	public Contact saveContact(Contact contact) {
		return contactRepository.save(contact);
	}

	@Override
	@Transactional
	public void removeContact(Long id) {
		contactRepository.deleteById(id);
	}

	@Override
	public Contact getContactById(Long id) {
		return contactRepository.getOne(id);
	}

	@Override
	public List<Contact> listContacts() {
		return contactRepository.findAll();
	}
	@Override
	public Contact findByNoticerAndUser(User noticer,User user){
		return contactRepository.findByNoticerAndUser(noticer, user);
	}

	@Override
	public Page<Contact> listContactsByNoticer(User noticer, Pageable pageable) {
		Page<Contact> page=contactRepository.findByNoticerOrderByCreateTimeDesc(noticer, pageable);
		return page;
	}

	@Override
	public Page<Contact> listContactsByUser(User user, Pageable pageable) {
		Page<Contact> page=contactRepository.findByUserOrderByCreateTimeDesc(user, pageable);
		return page;
	}

}
