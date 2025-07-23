package org.yc.analysis.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VideoStatsDTO {
    
    private Long id;
    private Long employeeId;
    private String employeeName;
    private LocalDate statsDate;
    private String statsDateText;
    private Integer dailyPublishedCount;
    private Long dailyNewViews;
    private Integer totalPublishedCount;
    
    /**
     * 获取日期文本
     */
    public String getStatsDateText() {
        return statsDate != null ? statsDate.toString() : "";
    }
}
