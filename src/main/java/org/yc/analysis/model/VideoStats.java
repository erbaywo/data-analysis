package org.yc.analysis.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "video_stats")
public class VideoStats {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "employee_id", nullable = false)
    private Long employeeId;
    
    @Column(name = "stats_date", nullable = false)
    private LocalDate statsDate;
    
    @Column(name = "daily_published_count")
    private Integer dailyPublishedCount = 0;
    
    @Column(name = "daily_new_views")
    private Long dailyNewViews = 0L;
    
    @Column(name = "total_published_count")
    private Integer totalPublishedCount = 0;
    
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    // 复合索引
    @Table(indexes = {
        @Index(name = "idx_employee_date", columnList = "employee_id, stats_date")
    })
    public static class Indexes {}
}
