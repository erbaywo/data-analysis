package org.yc.analysis.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmployeeDTO {
    
    private Long id;
    private String name;
    private String douyinAccount;
    private String douyinOpenId;
    private Integer authorizationStatus;
    private String authorizationStatusText;
    private Long followersCount;
    private Long likesCount;
    private Long commentsCount;
    private Long sharesCount;
    private Long profileViewsCount;
    private Integer videosCount;
    private LocalDateTime lastSyncTime;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 授权凭证信息（仅在需要时返回）
    private String accessToken;
    private String refreshToken;
    private String scope;
    
    /**
     * 获取授权状态文本
     */
    public String getAuthorizationStatusText() {
        if (authorizationStatus == null) {
            return "未知";
        }
        switch (authorizationStatus) {
            case 0:
                return "未授权";
            case 1:
                return "已授权";
            case 2:
                return "授权过期";
            default:
                return "未知";
        }
    }
}
