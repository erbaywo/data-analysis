package org.yc.analysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 抖音获取 access_token 响应
 */
@Data
public class DouyinTokenResponse {

    @JsonProperty("data")
    private TokenData data;

    @JsonProperty("message")
    private String message;

    @Data
    public static class TokenData {
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

        @JsonProperty("refresh_expires_in")
        private Long refreshExpiresIn;

        @JsonProperty("log_id")
        private String logId;
    }

    // 便捷方法
    public String getAccessToken() {
        return data != null ? data.getAccessToken() : null;
    }

    public String getOpenId() {
        return data != null ? data.getOpenId() : null;
    }

    public Long getExpiresIn() {
        return data != null ? data.getExpiresIn() : null;
    }

    public String getRefreshToken() {
        return data != null ? data.getRefreshToken() : null;
    }

    public Integer getErrorCode() {
        return data != null ? data.getErrorCode() : null;
    }

    public String getDescription() {
        return data != null ? data.getDescription() : null;
    }
}