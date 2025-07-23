package org.yc.analysis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.yc.analysis.common.response.ResponseResult;
import org.yc.analysis.dto.*;
import org.yc.analysis.service.DataSyncService;
import org.yc.analysis.service.EmployeeService;
import org.yc.analysis.service.VideoStatsService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/douyin-management")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "抖音管理", description = "抖音员工管理相关接口")
public class DouyinManagementController {
    
    private final EmployeeService employeeService;
    private final DataSyncService dataSyncService;
    private final VideoStatsService videoStatsService;
    
    // ==================== 员工管理 ====================
    
    /**
     * 获取所有员工列表
     */
    @GetMapping("/employees")
    @Operation(summary = "获取员工列表", description = "获取所有员工的基本信息和统计数据")
    public ResponseResult<List<EmployeeDTO>> getAllEmployees() {
        log.info("获取所有员工列表");
        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        return ResponseResult.success(employees);
    }
    
    /**
     * 根据ID获取员工信息
     */
    @GetMapping("/employees/{id}")
    @Operation(summary = "获取员工详情", description = "根据员工ID获取详细信息")
    public ResponseResult<EmployeeDTO> getEmployeeById(
            @Parameter(description = "员工ID", required = true)
            @PathVariable Long id) {
        log.info("获取员工详情，ID: {}", id);
        EmployeeDTO employee = employeeService.getEmployeeById(id);
        return ResponseResult.success(employee);
    }
    
    /**
     * 添加员工
     */
    @PostMapping("/employees")
    @Operation(summary = "添加员工", description = "添加新员工")
    public ResponseResult<EmployeeDTO> addEmployee(
            @Parameter(description = "员工信息", required = true)
            @Valid @RequestBody AddEmployeeRequest request) {
        log.info("添加员工: {}", request.getName());
        EmployeeDTO employee = employeeService.addEmployee(request);
        return ResponseResult.success(employee);
    }
    
    /**
     * 更新员工信息
     */
    @PutMapping("/employees/{id}")
    @Operation(summary = "更新员工信息", description = "更新员工基本信息")
    public ResponseResult<EmployeeDTO> updateEmployee(
            @Parameter(description = "员工ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "员工信息", required = true)
            @Valid @RequestBody AddEmployeeRequest request) {
        log.info("更新员工信息，ID: {}", id);
        EmployeeDTO employee = employeeService.updateEmployee(id, request);
        return ResponseResult.success(employee);
    }
    
    /**
     * 删除员工
     */
    @DeleteMapping("/employees/{id}")
    @Operation(summary = "删除员工", description = "删除员工（软删除）")
    public ResponseResult<Void> deleteEmployee(
            @Parameter(description = "员工ID", required = true)
            @PathVariable Long id) {
        log.info("删除员工，ID: {}", id);
        employeeService.deleteEmployee(id);
        return ResponseResult.success();
    }
    
    // ==================== 授权管理 ====================
    
    /**
     * 绑定抖音授权
     */
    @PostMapping("/employees/{id}/bind-auth")
    @Operation(summary = "绑定抖音授权", description = "将员工与抖音授权账号绑定")
    public ResponseResult<EmployeeDTO> bindDouyinAuth(
            @Parameter(description = "员工ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "抖音用户openId", required = true)
            @RequestParam String openId) {
        log.info("绑定员工抖音授权，员工ID: {}, openId: {}", id, openId);
        EmployeeDTO employee = employeeService.bindDouyinAuth(id, openId);
        return ResponseResult.success(employee);
    }
    
    /**
     * 获取员工授权凭证
     */
    @GetMapping("/employees/{id}/auth-credentials")
    @Operation(summary = "获取授权凭证", description = "获取员工的抖音授权凭证信息")
    public ResponseResult<EmployeeDTO> getEmployeeAuthCredentials(
            @Parameter(description = "员工ID", required = true)
            @PathVariable Long id) {
        log.info("获取员工授权凭证，ID: {}", id);
        EmployeeDTO employee = employeeService.getEmployeeAuthCredentials(id);
        return ResponseResult.success(employee);
    }
    
    // ==================== 数据同步 ====================
    
    /**
     * 手动同步所有员工数据
     */
    @PostMapping("/sync/manual")
    @Operation(summary = "手动同步数据", description = "手动同步所有已授权员工的抖音数据")
    public ResponseResult<String> manualSyncAllEmployees() {
        log.info("开始手动同步所有员工数据");
        
        // 异步执行同步任务
        CompletableFuture<SyncRecordDTO> future = dataSyncService.manualSyncAllEmployees();
        
        return ResponseResult.success("同步任务已启动，请稍后查看同步结果");
    }
    
    /**
     * 获取同步记录
     */
    @GetMapping("/sync/records")
    @Operation(summary = "获取同步记录", description = "获取数据同步历史记录")
    public ResponseResult<List<SyncRecordDTO>> getSyncRecords() {
        log.info("获取同步记录");
        List<SyncRecordDTO> records = dataSyncService.getSyncRecords();
        return ResponseResult.success(records);
    }
    
    /**
     * 获取最新同步记录
     */
    @GetMapping("/sync/latest")
    @Operation(summary = "获取最新同步记录", description = "获取最近一次的同步记录")
    public ResponseResult<SyncRecordDTO> getLatestSyncRecord() {
        log.info("获取最新同步记录");
        SyncRecordDTO record = dataSyncService.getLatestSyncRecord();
        return ResponseResult.success(record);
    }
    
    /**
     * 获取今日同步次数
     */
    @GetMapping("/sync/today-count")
    @Operation(summary = "获取今日同步次数", description = "获取今天已执行的同步次数")
    public ResponseResult<Long> getTodaySyncCount() {
        log.info("获取今日同步次数");
        Long count = dataSyncService.getTodaySyncCount();
        return ResponseResult.success(count);
    }

    // ==================== 视频统计 ====================

    /**
     * 获取员工视频统计
     */
    @GetMapping("/employees/{id}/video-stats")
    @Operation(summary = "获取员工视频统计", description = "获取员工指定天数的视频统计数据")
    public ResponseResult<List<VideoStatsDTO>> getEmployeeVideoStats(
            @Parameter(description = "员工ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "统计天数", required = false)
            @RequestParam(defaultValue = "7") Integer days) {
        log.info("获取员工 {} 最近 {} 天的视频统计", id, days);
        List<VideoStatsDTO> stats = videoStatsService.getEmployeeVideoStats(id, days);
        return ResponseResult.success(stats);
    }

    /**
     * 获取今日所有员工视频统计
     */
    @GetMapping("/video-stats/today")
    @Operation(summary = "获取今日视频统计", description = "获取所有员工今日的视频统计数据")
    public ResponseResult<List<VideoStatsDTO>> getTodayVideoStats() {
        log.info("获取今日所有员工视频统计");
        List<VideoStatsDTO> stats = videoStatsService.getTodayVideoStats();
        return ResponseResult.success(stats);
    }

    /**
     * 生成模拟视频统计数据
     */
    @PostMapping("/employees/{id}/video-stats/mock")
    @Operation(summary = "生成模拟数据", description = "为员工生成指定天数的模拟视频统计数据（测试用）")
    public ResponseResult<Void> generateMockVideoStats(
            @Parameter(description = "员工ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "生成天数", required = false)
            @RequestParam(defaultValue = "30") Integer days) {
        log.info("为员工 {} 生成 {} 天的模拟视频统计数据", id, days);
        videoStatsService.generateMockVideoStats(id, days);
        return ResponseResult.success();
    }
}
