package com.wfy.spring.boot.blog.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity  // 关注  实体
@Table(name = "contact")
public class Contact implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id //主键
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
	private Long id; //关注的唯一标识
	
	@OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
	@JoinColumn(name="noticer_id")
	private User noticer;  //关注者

	@OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;  //被关注者
	
	@Column(nullable = false) // 映射为字段，值不能为空
	@org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
	private Timestamp createTime;
	
	protected Contact() {  
	}
	
	public Contact(User noticer,User user){
		this.noticer=noticer;
		this.user=user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public User getNoticer() {
		return noticer;
	}

	public void setNoticer(User noticer) {
		this.noticer = noticer;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}
}
