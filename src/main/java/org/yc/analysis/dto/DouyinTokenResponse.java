package org.yc.analysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 抖音获取 access_token 响应
 */
@Data
public class DouyinTokenResponse {
    
    @JsonProperty("access_token")
    private String accessToken;
    
    @JsonProperty("expires_in")
    private Long expiresIn;
    
    @JsonProperty("refresh_token")
    private String refreshToken;
    
    @JsonProperty("open_id")
    private String openId;
    
    @JsonProperty("scope")
    private String scope;
    
    @JsonProperty("error_code")
    private Integer errorCode;
    
    @JsonProperty("description")
    private String description;
}
