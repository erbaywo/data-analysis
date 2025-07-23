package org.yc.analysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.yc.analysis.model.SyncRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SyncRecordRepository extends JpaRepository<SyncRecord, Long> {
    
    /**
     * 查找最近的同步记录
     */
    Optional<SyncRecord> findTopByOrderByCreateTimeDesc();
    
    /**
     * 查找最近的成功同步记录
     */
    Optional<SyncRecord> findTopBySyncStatusOrderByCreateTimeDesc(Integer syncStatus);
    
    /**
     * 查找指定时间范围内的同步记录
     */
    List<SyncRecord> findByCreateTimeBetweenOrderByCreateTimeDesc(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查找进行中的同步任务
     */
    List<SyncRecord> findBySyncStatus(Integer syncStatus);
    
    /**
     * 统计今日同步次数
     */
    @Query("SELECT COUNT(sr) FROM SyncRecord sr WHERE DATE(sr.createTime) = CURRENT_DATE")
    Long countTodaySync();
}
