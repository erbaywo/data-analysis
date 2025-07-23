package org.yc.analysis.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SyncRecordDTO {
    
    private Long id;
    private String syncType;
    private String syncTypeText;
    private Integer syncStatus;
    private String syncStatusText;
    private Integer employeeCount;
    private Integer successCount;
    private Integer failedCount;
    private String errorMessage;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;
    private Long duration; // 同步耗时（秒）
    
    /**
     * 获取同步类型文本
     */
    public String getSyncTypeText() {
        if ("MANUAL".equals(syncType)) {
            return "手动同步";
        } else if ("AUTO".equals(syncType)) {
            return "自动同步";
        }
        return syncType;
    }
    
    /**
     * 获取同步状态文本
     */
    public String getSyncStatusText() {
        if (syncStatus == null) {
            return "未知";
        }
        switch (syncStatus) {
            case 0:
                return "进行中";
            case 1:
                return "成功";
            case 2:
                return "失败";
            default:
                return "未知";
        }
    }
    
    /**
     * 计算同步耗时
     */
    public Long getDuration() {
        if (startTime != null && endTime != null) {
            return java.time.Duration.between(startTime, endTime).getSeconds();
        }
        return null;
    }
}
