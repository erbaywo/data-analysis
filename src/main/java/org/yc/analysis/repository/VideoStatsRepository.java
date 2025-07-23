package org.yc.analysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yc.analysis.model.VideoStats;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VideoStatsRepository extends JpaRepository<VideoStats, Long> {
    
    /**
     * 根据员工ID和日期查找统计记录
     */
    Optional<VideoStats> findByEmployeeIdAndStatsDate(Long employeeId, LocalDate statsDate);
    
    /**
     * 根据员工ID查找统计记录，按日期倒序
     */
    List<VideoStats> findByEmployeeIdOrderByStatsDateDesc(Long employeeId);
    
    /**
     * 根据员工ID和日期范围查找统计记录
     */
    @Query("SELECT vs FROM VideoStats vs WHERE vs.employeeId = :employeeId " +
           "AND vs.statsDate BETWEEN :startDate AND :endDate ORDER BY vs.statsDate DESC")
    List<VideoStats> findByEmployeeIdAndDateRange(@Param("employeeId") Long employeeId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);
    
    /**
     * 获取员工最近N天的统计数据
     */
    @Query("SELECT vs FROM VideoStats vs WHERE vs.employeeId = :employeeId " +
           "AND vs.statsDate >= :startDate ORDER BY vs.statsDate DESC")
    List<VideoStats> findRecentStatsByEmployeeId(@Param("employeeId") Long employeeId,
                                                 @Param("startDate") LocalDate startDate);
    
    /**
     * 删除员工的统计数据
     */
    void deleteByEmployeeId(Long employeeId);
}
