package org.yc.analysis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yc.analysis.dto.SyncRecordDTO;
import org.yc.analysis.exception.BusinessException;
import org.yc.analysis.model.Employee;
import org.yc.analysis.model.SyncRecord;
import org.yc.analysis.repository.SyncRecordRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataSyncService {
    
    private final SyncRecordRepository syncRecordRepository;
    private final EmployeeService employeeService;
    
    /**
     * 手动同步所有员工数据
     */
    @Async
    @Transactional
    public CompletableFuture<SyncRecordDTO> manualSyncAllEmployees() {
        log.info("开始手动同步所有员工数据");
        
        // 检查是否有正在进行的同步任务
        List<SyncRecord> runningSyncs = syncRecordRepository.findBySyncStatus(0);
        if (!runningSyncs.isEmpty()) {
            throw new BusinessException("已有同步任务正在进行中，请稍后再试");
        }
        
        // 创建同步记录
        SyncRecord syncRecord = new SyncRecord();
        syncRecord.setSyncType("MANUAL");
        syncRecord.setSyncStatus(0); // 进行中
        syncRecord.setStartTime(LocalDateTime.now());
        syncRecord = syncRecordRepository.save(syncRecord);
        
        try {
            // 获取已授权的员工列表
            List<Employee> authorizedEmployees = employeeService.getAuthorizedEmployees();
            syncRecord.setEmployeeCount(authorizedEmployees.size());
            syncRecord = syncRecordRepository.save(syncRecord);
            
            int successCount = 0;
            int failedCount = 0;
            StringBuilder errorMessages = new StringBuilder();
            
            // 同步每个员工的数据
            for (Employee employee : authorizedEmployees) {
                try {
                    syncEmployeeData(employee);
                    successCount++;
                    log.info("员工 {} 数据同步成功", employee.getName());
                } catch (Exception e) {
                    failedCount++;
                    String errorMsg = String.format("员工 %s 同步失败: %s", employee.getName(), e.getMessage());
                    errorMessages.append(errorMsg).append("; ");
                    log.error(errorMsg, e);
                }
            }
            
            // 更新同步记录
            syncRecord.setSuccessCount(successCount);
            syncRecord.setFailedCount(failedCount);
            syncRecord.setSyncStatus(failedCount == 0 ? 1 : 2); // 全部成功为1，有失败为2
            syncRecord.setEndTime(LocalDateTime.now());
            
            if (errorMessages.length() > 0) {
                syncRecord.setErrorMessage(errorMessages.toString());
            }
            
            syncRecord = syncRecordRepository.save(syncRecord);
            
            log.info("手动同步完成，成功: {}, 失败: {}", successCount, failedCount);
            
        } catch (Exception e) {
            // 同步过程中发生异常
            syncRecord.setSyncStatus(2); // 失败
            syncRecord.setEndTime(LocalDateTime.now());
            syncRecord.setErrorMessage("同步过程中发生异常: " + e.getMessage());
            syncRecord = syncRecordRepository.save(syncRecord);
            
            log.error("手动同步失败", e);
            throw new BusinessException("同步失败: " + e.getMessage());
        }
        
        return CompletableFuture.completedFuture(convertToDTO(syncRecord));
    }
    
    /**
     * 同步单个员工数据
     */
    @Transactional
    public void syncEmployeeData(Employee employee) {
        log.info("开始同步员工 {} 的数据", employee.getName());
        
        // 这里应该调用抖音API获取用户数据
        // 由于抖音API的复杂性，这里使用模拟数据
        // 实际实现时需要根据抖音API文档进行开发
        
        try {
            // 模拟API调用延迟
            Thread.sleep(1000);
            
            // 模拟获取到的数据（实际应该从抖音API获取）
            Long followersCount = employee.getFollowersCount() + (long)(Math.random() * 100);
            Long likesCount = employee.getLikesCount() + (long)(Math.random() * 1000);
            Long commentsCount = employee.getCommentsCount() + (long)(Math.random() * 100);
            Long sharesCount = employee.getSharesCount() + (long)(Math.random() * 50);
            Long profileViewsCount = employee.getProfileViewsCount() + (long)(Math.random() * 500);
            Integer videosCount = employee.getVideosCount() + (int)(Math.random() * 5);
            
            // 更新员工统计数据
            employeeService.updateEmployeeStats(employee.getId(), followersCount, likesCount, 
                    commentsCount, sharesCount, profileViewsCount, videosCount);
            
            log.info("员工 {} 数据同步完成", employee.getName());
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("同步被中断");
        } catch (Exception e) {
            log.error("同步员工 {} 数据失败", employee.getName(), e);
            throw new BusinessException("同步失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取同步记录列表
     */
    public List<SyncRecordDTO> getSyncRecords() {
        List<SyncRecord> records = syncRecordRepository.findAll();
        return records.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取最近的同步记录
     */
    public SyncRecordDTO getLatestSyncRecord() {
        return syncRecordRepository.findTopByOrderByCreateTimeDesc()
                .map(this::convertToDTO)
                .orElse(null);
    }
    
    /**
     * 获取今日同步次数
     */
    public Long getTodaySyncCount() {
        return syncRecordRepository.countTodaySync();
    }
    
    /**
     * 转换为DTO
     */
    private SyncRecordDTO convertToDTO(SyncRecord syncRecord) {
        SyncRecordDTO dto = new SyncRecordDTO();
        dto.setId(syncRecord.getId());
        dto.setSyncType(syncRecord.getSyncType());
        dto.setSyncStatus(syncRecord.getSyncStatus());
        dto.setEmployeeCount(syncRecord.getEmployeeCount());
        dto.setSuccessCount(syncRecord.getSuccessCount());
        dto.setFailedCount(syncRecord.getFailedCount());
        dto.setErrorMessage(syncRecord.getErrorMessage());
        dto.setStartTime(syncRecord.getStartTime());
        dto.setEndTime(syncRecord.getEndTime());
        dto.setCreateTime(syncRecord.getCreateTime());
        return dto;
    }
}
