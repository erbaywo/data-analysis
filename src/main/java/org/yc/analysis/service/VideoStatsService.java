package org.yc.analysis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yc.analysis.dto.VideoStatsDTO;
import org.yc.analysis.exception.BusinessException;
import org.yc.analysis.model.Employee;
import org.yc.analysis.model.VideoStats;
import org.yc.analysis.repository.EmployeeRepository;
import org.yc.analysis.repository.VideoStatsRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoStatsService {
    
    private final VideoStatsRepository videoStatsRepository;
    private final EmployeeRepository employeeRepository;
    
    /**
     * 获取员工的视频统计数据
     */
    public List<VideoStatsDTO> getEmployeeVideoStats(Long employeeId, Integer days) {
        log.info("获取员工 {} 最近 {} 天的视频统计", employeeId, days);
        
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException("员工不存在"));
        
        LocalDate startDate = LocalDate.now().minusDays(days - 1);
        List<VideoStats> statsList = videoStatsRepository.findRecentStatsByEmployeeId(employeeId, startDate);
        
        return statsList.stream()
                .map(stats -> convertToDTO(stats, employee.getName()))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取所有员工今日视频统计
     */
    public List<VideoStatsDTO> getTodayVideoStats() {
        log.info("获取所有员工今日视频统计");
        
        LocalDate today = LocalDate.now();
        List<Employee> employees = employeeRepository.findByStatusOrderByCreateTimeDesc(1);
        
        return employees.stream()
                .map(employee -> {
                    Optional<VideoStats> statsOpt = videoStatsRepository.findByEmployeeIdAndStatsDate(employee.getId(), today);
                    if (statsOpt.isPresent()) {
                        return convertToDTO(statsOpt.get(), employee.getName());
                    } else {
                        // 如果没有今日数据，创建一个空的统计记录
                        VideoStatsDTO dto = new VideoStatsDTO();
                        dto.setEmployeeId(employee.getId());
                        dto.setEmployeeName(employee.getName());
                        dto.setStatsDate(today);
                        dto.setDailyPublishedCount(0);
                        dto.setDailyNewViews(0L);
                        dto.setTotalPublishedCount(employee.getVideosCount());
                        return dto;
                    }
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 更新员工视频统计
     */
    @Transactional
    public VideoStatsDTO updateVideoStats(Long employeeId, LocalDate statsDate, 
                                         Integer dailyPublishedCount, Long dailyNewViews, Integer totalPublishedCount) {
        log.info("更新员工 {} 在 {} 的视频统计", employeeId, statsDate);
        
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException("员工不存在"));
        
        Optional<VideoStats> existingStats = videoStatsRepository.findByEmployeeIdAndStatsDate(employeeId, statsDate);
        
        VideoStats videoStats;
        if (existingStats.isPresent()) {
            videoStats = existingStats.get();
        } else {
            videoStats = new VideoStats();
            videoStats.setEmployeeId(employeeId);
            videoStats.setStatsDate(statsDate);
        }
        
        videoStats.setDailyPublishedCount(dailyPublishedCount);
        videoStats.setDailyNewViews(dailyNewViews);
        videoStats.setTotalPublishedCount(totalPublishedCount);
        
        videoStats = videoStatsRepository.save(videoStats);
        
        log.info("视频统计更新成功");
        return convertToDTO(videoStats, employee.getName());
    }
    
    /**
     * 生成模拟的视频统计数据（用于测试）
     */
    @Transactional
    public void generateMockVideoStats(Long employeeId, Integer days) {
        log.info("为员工 {} 生成最近 {} 天的模拟视频统计数据", employeeId, days);
        
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException("员工不存在"));
        
        LocalDate startDate = LocalDate.now().minusDays(days - 1);
        
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            
            // 检查是否已存在该日期的统计数据
            Optional<VideoStats> existingStats = videoStatsRepository.findByEmployeeIdAndStatsDate(employeeId, date);
            if (existingStats.isPresent()) {
                continue; // 跳过已存在的数据
            }
            
            // 生成模拟数据
            Integer dailyPublished = (int)(Math.random() * 5); // 0-4个视频
            Long dailyViews = (long)(Math.random() * 10000); // 0-10000观看量
            Integer totalPublished = employee.getVideosCount() + dailyPublished;
            
            VideoStats videoStats = new VideoStats();
            videoStats.setEmployeeId(employeeId);
            videoStats.setStatsDate(date);
            videoStats.setDailyPublishedCount(dailyPublished);
            videoStats.setDailyNewViews(dailyViews);
            videoStats.setTotalPublishedCount(totalPublished);
            
            videoStatsRepository.save(videoStats);
        }
        
        log.info("模拟视频统计数据生成完成");
    }
    
    /**
     * 删除员工的视频统计数据
     */
    @Transactional
    public void deleteEmployeeVideoStats(Long employeeId) {
        log.info("删除员工 {} 的视频统计数据", employeeId);
        videoStatsRepository.deleteByEmployeeId(employeeId);
        log.info("视频统计数据删除完成");
    }
    
    /**
     * 转换为DTO
     */
    private VideoStatsDTO convertToDTO(VideoStats videoStats, String employeeName) {
        VideoStatsDTO dto = new VideoStatsDTO();
        dto.setId(videoStats.getId());
        dto.setEmployeeId(videoStats.getEmployeeId());
        dto.setEmployeeName(employeeName);
        dto.setStatsDate(videoStats.getStatsDate());
        dto.setDailyPublishedCount(videoStats.getDailyPublishedCount());
        dto.setDailyNewViews(videoStats.getDailyNewViews());
        dto.setTotalPublishedCount(videoStats.getTotalPublishedCount());
        return dto;
    }
}
