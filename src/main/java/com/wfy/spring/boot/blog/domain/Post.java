package com.wfy.spring.boot.blog.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.github.rjeschke.txtmark.Processor;

/**
 * Post 帖子
 */
@Entity // 实体
public class Post implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id // 主键
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
	private Long id; // 帖子的唯一标识
	
	@NotEmpty(message = "标题不能为空")
	@Size(min=2, max=50)
	@Column(nullable = false, length = 50) // 映射为字段，值不能为空
	private String title;

	@Lob  // 大对象，映射 MySQL 的 Long Text 类型
	@Basic(fetch=FetchType.LAZY) // 懒加载
	@NotEmpty(message = "内容不能为空")
	@Size(min=2)
	@Column(nullable = false) // 映射为字段，值不能为空
	private String content;
	
	@Lob  // 大对象，映射 MySQL 的 Long Text 类型
	@Basic(fetch=FetchType.LAZY) // 懒加载
	@NotEmpty(message = "内容不能为空")
	@Size(min=2)
	@Column(nullable = false) // 映射为字段，值不能为空
	private String htmlContent; // 将 md 转为 html

	@OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;
	
	@Column(nullable = false) // 映射为字段，值不能为空
	@org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
	private Timestamp createTime;

	@Column(name="readSize")
	private Integer readSize = 0; // 访问量、阅读量
	 
	@Column(name="followSize")
	private Integer followSize = 0;  // 跟帖量
	
	@Column(name="spendGold")
	private Integer spendGold = 0;  // 悬赏值
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "post_follow", joinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id"), 
		inverseJoinColumns = @JoinColumn(name = "follow_id", referencedColumnName = "id"))
	private List<Follow> follows;  //跟帖
	
	@Column(name="isSolve") 
	private Boolean isSolve=false;  // 是否被解决
	
	@Column(name="tags", length = 100) 
	private String tags;  // 标签
	
	protected Post() {
		// TODO Auto-generated constructor stub
	}
	public Post(String title,String content,Integer spendGold) {
		this.title = title;
		this.content = content;
		this.spendGold=spendGold;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
		this.htmlContent = Processor.process(content);
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
	
	public String getHtmlContent() {
		return htmlContent;
	}
 
	public Integer getReadSize() {
		return readSize;
	}
	
	public void setReadSize(Integer readSize) {
		this.readSize = readSize;
	}
	
	public Integer getFollowSize() {
		return followSize;
	}
	
	public void setFollowSize(Integer followSize) {
		this.followSize = followSize;
	}
	
	public Integer getSpendGold() {
		return spendGold;
	}
	
	public void setSpendGold(Integer spendGold) {
		this.spendGold = spendGold;
	}
	
	public List<Follow> getFollows() {
		return follows;
	}
	
	public void setFollows(List<Follow> follows) {
		this.follows = follows;
		this.followSize = this.follows.size();
	}
	/**
	 * 添加跟帖
	 * @param comment
	 */
	public void addFollow(Follow follow) {
		this.follows.add(follow);
		this.followSize = this.follows.size();
	}
	/**
	 * 删除跟帖
	 * @param comment
	 */
	public void removeFollow(Long followId) {
		for (int index=0; index < this.follows.size(); index ++ ) {
			if (follows.get(index).getId() == followId) {
				this.follows.remove(index);
				break;
			}
		}
		this.followSize = this.follows.size();
	}

	public Boolean getIsSolve() {
		return isSolve;
	}
	
	public void setIsSolve(Boolean isSolve) {
		this.isSolve = isSolve;
	}
	
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
 
}
