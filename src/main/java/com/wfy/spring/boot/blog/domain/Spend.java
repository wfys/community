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

@Entity  // 花费 实体
@Table(name = "spend")
public class Spend implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id //主键
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
	private Long id; //花费的唯一标识
	
	@Column(name="gold",nullable = false)
	private Integer gold;  // 花费金额
	
	@OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
	@JoinColumn(name="consumer_id")
	private User consumer;  //花费人
	
	@OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;  //收入者
	
	@Column(name="websiteInfo")
	private String websiteInfo;  // 信息
	
	@Column(name="website")
	private String website;  // 地址ַ
	
	@Column(nullable = false) // 映射为字段，值不能为空
	@org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
	private Timestamp createTime;
	
	protected Spend() {  
	}

	public Spend(Integer gold,User consumer){
		this.gold=gold;
		this.consumer=consumer;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getGold() {
		return gold;
	}

	public void setGold(Integer gold) {
		this.gold = gold;
	}

	public User getConsumer() {
		return consumer;
	}

	public void setConsumer(User consumer) {
		this.consumer = consumer;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getWebsiteInfo() {
		return websiteInfo;
	}

	public void setWebsiteInfo(String websiteInfo) {
		this.websiteInfo = websiteInfo;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}
}
