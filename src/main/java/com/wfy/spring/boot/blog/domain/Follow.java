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
 * 跟帖
 * @author wfy
 *
 */
@Entity // 实体
public class Follow implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id // 主键
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
	private Long id; // 用户的唯一标识

	@Lob  
	@Basic(fetch=FetchType.LAZY) 
	@NotEmpty(message = "内容不能为空")
	@Size(min=2)
	@Column(nullable = false) 
	private String content;
	
	@Lob  
	@Basic(fetch=FetchType.LAZY) 
	@NotEmpty(message = "内容不能为空")
	@Size(min=2)
	@Column(nullable = false) 
	private String htmlContent; 
 
	@OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;
	
	@Column(name="likeSize")
	private Integer likeSize = 0;  
	
	@Column(name="isAccept")
	private Boolean isAccept = false;  // 是否被采纳
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "follow_vote", joinColumns = @JoinColumn(name = "follow_id", referencedColumnName = "id"), 
		inverseJoinColumns = @JoinColumn(name = "vote_id", referencedColumnName = "id"))
	private List<Vote> votes;  
	
	@Column(nullable = false) 
	@org.hibernate.annotations.CreationTimestamp  
	private Timestamp createTime;
 
	protected Follow() {
		// TODO Auto-generated constructor stub
	}
	public Follow(User user, String content) {
		this.content = content;
		this.user = user;
		this.htmlContent = Processor.process(content);
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
		this.htmlContent = Processor.process(content);
	}
	
	public String getHtmlContent() {
		return htmlContent;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public Integer getLikeSize() {
		return likeSize;
	}
	
	public void setIsAccept(Boolean isAccept) {
		this.isAccept = isAccept;
	}
	
	public Boolean getIsAccept() {
		return isAccept;
	}
	
	public void setLikeSize(Integer likeSize) {
		this.likeSize = likeSize;
	}
	
	public List<Vote> getVotes() {
		return votes;
	}
	
	public void setVotes(List<Vote> votes) {
		this.votes = votes;
		this.likeSize = this.votes.size();
	}
	/**
	 * 点赞
	 * @param vote
	 * @return
	 */
	public boolean addVote(Vote vote) {
		boolean isExist = false;
		// 判断重复
		for (int index=0; index < this.votes.size(); index ++ ) {
			if (this.votes.get(index).getUser().getId() == vote.getUser().getId()) {
				isExist = true;
				break;
			}
		}
		
		if (!isExist) {
			this.votes.add(vote);
			this.likeSize = this.votes.size();
		}

		return isExist;
	}
	/**
	 * 取消点赞
	 * @param voteId
	 */
	public void removeVote(Long voteId) {
		for (int index=0; index < this.votes.size(); index ++ ) {
			if (this.votes.get(index).getId() == voteId) {
				this.votes.remove(index);
				break;
			}
		}
		this.likeSize = this.votes.size();
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
}
