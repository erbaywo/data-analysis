package org.yc.analysis.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sync_record")
public class SyncRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "sync_type", nullable = false, length = 50)
    private String syncType; // MANUAL-手动同步, AUTO-自动同步
    
    @Column(name = "sync_status", nullable = false)
    private Integer syncStatus; // 0-进行中，1-成功，2-失败
    
    @Column(name = "employee_count")
    private Integer employeeCount = 0;
    
    @Column(name = "success_count")
    private Integer successCount = 0;
    
    @Column(name = "failed_count")
    private Integer failedCount = 0;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;
}
