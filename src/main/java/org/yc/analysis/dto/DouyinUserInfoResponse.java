package org.yc.analysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 抖音获取用户信息响应
 * {
 *   "err_msg": "",
 *   "log_id": "2025032716200991A1xxxxx30A01C4DF",
 *   "data": {
 *     "open_id": "qYm3LVbtDQ",
 *     "nickname": "李伟",
 *     "description": "",
 *     "e_account_role": "",
 *     "error_code": "0",
 *     "avatar": "https://xxx.com/xxx.jpeg",
 *     "client_key": "XQSXAWkY9f",
 *     "log_id": "2025032716200991A181xxxxx0A01C4DF",
 *     "union_id": "eVBXUMlnek"
 *   },
 *   "err_no": 0
 * }
 */
@Data
public class DouyinUserInfoResponse {
    
    @JsonProperty("err_no")
    private Integer errorCode;

    @JsonProperty("err_msg")
    private String errorMsg;
    @JsonProperty("log_id")
    private String logId;

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

        @JsonProperty("description")
        private String description;

        @JsonProperty("e_account_role")
        private String eAccountRole;

        @JsonProperty("client_key")
        private String clientKey;



    }
}
