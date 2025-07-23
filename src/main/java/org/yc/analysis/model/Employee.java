package org.yc.analysis.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "employee")
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "douyin_account", length = 100)
    private String douyinAccount;
    
    @Column(name = "douyin_open_id", length = 100)
    private String douyinOpenId;
    
    @Column(name = "authorization_status")
    private Integer authorizationStatus = 0; // 0-未授权，1-已授权，2-授权过期
    
    @Column(name = "followers_count")
    private Long followersCount = 0L;
    
    @Column(name = "likes_count")
    private Long likesCount = 0L;
    
    @Column(name = "comments_count")
    private Long commentsCount = 0L;
    
    @Column(name = "shares_count")
    private Long sharesCount = 0L;
    
    @Column(name = "profile_views_count")
    private Long profileViewsCount = 0L;
    
    @Column(name = "videos_count")
    private Integer videosCount = 0;
    
    @Column(name = "last_sync_time")
    private LocalDateTime lastSyncTime;
    
    @Column(name = "status")
    private Integer status = 1; // 0-禁用，1-启用
    
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    @UpdateTimestamp
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;
}
