package org.yc.analysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 抖音获取用户信息响应
 */
@Data
public class DouyinUserInfoResponse {
    
    @JsonProperty("error_code")
    private Integer errorCode;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("data")
    private UserData data;
    
    @Data
    public static class UserData {
        
        @JsonProperty("open_id")
        private String openId;
        
        @JsonProperty("union_id")
        private String unionId;
        
        @JsonProperty("nickname")
        private String nickname;
        
        @JsonProperty("avatar")
        private String avatar;
        
        @JsonProperty("gender")
        private Integer gender;
        
        @JsonProperty("country")
        private String country;
        
        @JsonProperty("province")
        private String province;
        
        @JsonProperty("city")
        private String city;
    }
}
