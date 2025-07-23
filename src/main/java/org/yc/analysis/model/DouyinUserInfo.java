package org.yc.analysis.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "douyin_user_info")
public class DouyinUserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "open_id", nullable = false, unique = true, length = 100)
    private String openId;

    @Column(name = "union_id", length = 100)
    private String unionId;

    @Column(name = "nickname", length = 100)
    private String nickname;

    @Column(name = "avatar", length = 500)
    private String avatar;

    @Column(name = "gender")
    private Integer gender;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "province", length = 50)
    private String province;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "access_token", length = 500)
    private String accessToken;

    @Column(name = "refresh_token", length = 500)
    private String refreshToken;

    @Column(name = "expires_in")
    private Long expiresIn;

    @Column(name = "token_expire_time")
    private LocalDateTime tokenExpireTime;

    @Column(name = "status")
    private Integer status = 1;

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;
}
