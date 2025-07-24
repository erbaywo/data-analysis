package org.yc.analysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

/**
 * 抖音数据统一响应格式
 */
@Data
public class DouyinDataResponse {
    
    @JsonProperty("data")
    private DataInfo data;
    
    @JsonProperty("extra")
    private Extra extra;
    
    @Data
    public static class DataInfo {
        @JsonProperty("error_code")
        private Integer errorCode;
        
        @JsonProperty("description")
        private String description;
        
        @JsonProperty("result_list")
        private List<DataItem> resultList;
    }
    
    @Data
    public static class DataItem {
        @JsonProperty("date")
        private String date;
        
        // 粉丝数据
        @JsonProperty("new_follow")
        private Long newFollow;
        
        @JsonProperty("cancel_follow")
        private Long cancelFollow;
        
        // 视频数据
        @JsonProperty("new_play")
        private Long newPlay;
        
        // 点赞数据
        @JsonProperty("new_like")
        private Long newLike;
        
        // 评论数据
        @JsonProperty("new_comment")
        private Long newComment;
        
        // 分享数据
        @JsonProperty("new_share")
        private Long newShare;
        
        // 主页访问数据
        @JsonProperty("profile_uv")
        private Long profileUv;
    }
    
    @Data
    public static class Extra {
        @JsonProperty("logid")
        private String logId;
        
        @JsonProperty("now")
        private Long now;
    }
}