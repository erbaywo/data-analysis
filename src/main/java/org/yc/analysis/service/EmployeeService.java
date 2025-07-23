package org.yc.analysis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yc.analysis.dto.AddEmployeeRequest;
import org.yc.analysis.dto.EmployeeDTO;
import org.yc.analysis.exception.BusinessException;
import org.yc.analysis.model.DouyinUserInfo;
import org.yc.analysis.model.Employee;
import org.yc.analysis.repository.DouyinUserInfoRepository;
import org.yc.analysis.repository.EmployeeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    private final DouyinUserInfoRepository douyinUserInfoRepository;
    
    /**
     * 获取所有员工列表
     */
    public List<EmployeeDTO> getAllEmployees() {
        List<Employee> employees = employeeRepository.findByStatusOrderByCreateTimeDesc(1);
        return employees.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取员工信息
     */
    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("员工不存在"));
        return convertToDTO(employee);
    }
    
    /**
     * 添加员工
     */
    @Transactional
    public EmployeeDTO addEmployee(AddEmployeeRequest request) {
        log.info("添加员工: {}, 抖音账号: {}", request.getName(), request.getDouyinAccount());
        
        // 检查抖音账号是否已存在
        Optional<Employee> existingEmployee = employeeRepository.findByDouyinAccount(request.getDouyinAccount());
        if (existingEmployee.isPresent()) {
            throw new BusinessException("该抖音账号已被其他员工使用");
        }
        
        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setDouyinAccount(request.getDouyinAccount());
        employee.setAuthorizationStatus(0); // 未授权
        employee.setStatus(1); // 启用
        
        employee = employeeRepository.save(employee);
        log.info("员工添加成功，ID: {}", employee.getId());
        
        return convertToDTO(employee);
    }
    
    /**
     * 更新员工信息
     */
    @Transactional
    public EmployeeDTO updateEmployee(Long id, AddEmployeeRequest request) {
        log.info("更新员工信息，ID: {}", id);
        
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("员工不存在"));
        
        // 检查抖音账号是否被其他员工使用
        Optional<Employee> existingEmployee = employeeRepository.findByDouyinAccount(request.getDouyinAccount());
        if (existingEmployee.isPresent() && !existingEmployee.get().getId().equals(id)) {
            throw new BusinessException("该抖音账号已被其他员工使用");
        }
        
        employee.setName(request.getName());
        employee.setDouyinAccount(request.getDouyinAccount());
        
        employee = employeeRepository.save(employee);
        log.info("员工信息更新成功");
        
        return convertToDTO(employee);
    }
    
    /**
     * 删除员工
     */
    @Transactional
    public void deleteEmployee(Long id) {
        log.info("删除员工，ID: {}", id);
        
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("员工不存在"));
        
        employee.setStatus(0); // 禁用
        employeeRepository.save(employee);
        
        log.info("员工删除成功");
    }
    
    /**
     * 绑定抖音授权
     */
    @Transactional
    public EmployeeDTO bindDouyinAuth(Long employeeId, String openId) {
        log.info("绑定员工抖音授权，员工ID: {}, openId: {}", employeeId, openId);
        
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException("员工不存在"));
        
        // 检查抖音用户是否存在
        DouyinUserInfo douyinUser = douyinUserInfoRepository.findByOpenId(openId)
                .orElseThrow(() -> new BusinessException("抖音用户不存在，请先完成授权"));
        
        employee.setDouyinOpenId(openId);
        employee.setAuthorizationStatus(1); // 已授权
        employee.setLastSyncTime(LocalDateTime.now());
        
        employee = employeeRepository.save(employee);
        log.info("抖音授权绑定成功");
        
        return convertToDTO(employee);
    }
    
    /**
     * 获取员工的授权凭证信息
     */
    public EmployeeDTO getEmployeeAuthCredentials(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException("员工不存在"));
        
        if (employee.getDouyinOpenId() == null) {
            throw new BusinessException("员工未绑定抖音账号");
        }
        
        DouyinUserInfo douyinUser = douyinUserInfoRepository.findByOpenId(employee.getDouyinOpenId())
                .orElseThrow(() -> new BusinessException("抖音授权信息不存在"));
        
        EmployeeDTO dto = convertToDTO(employee);
        dto.setAccessToken(douyinUser.getAccessToken());
        dto.setRefreshToken(douyinUser.getRefreshToken());
        dto.setScope("user_info"); // 根据实际授权范围设置
        
        return dto;
    }
    
    /**
     * 更新员工数据统计
     */
    @Transactional
    public void updateEmployeeStats(Long employeeId, Long followersCount, Long likesCount, 
                                   Long commentsCount, Long sharesCount, Long profileViewsCount, Integer videosCount) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException("员工不存在"));
        
        employee.setFollowersCount(followersCount);
        employee.setLikesCount(likesCount);
        employee.setCommentsCount(commentsCount);
        employee.setSharesCount(sharesCount);
        employee.setProfileViewsCount(profileViewsCount);
        employee.setVideosCount(videosCount);
        employee.setLastSyncTime(LocalDateTime.now());
        
        employeeRepository.save(employee);
    }
    
    /**
     * 获取已授权的员工列表
     */
    public List<Employee> getAuthorizedEmployees() {
        return employeeRepository.findAuthorizedEmployees();
    }
    
    /**
     * 转换为DTO
     */
    private EmployeeDTO convertToDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setDouyinAccount(employee.getDouyinAccount());
        dto.setDouyinOpenId(employee.getDouyinOpenId());
        dto.setAuthorizationStatus(employee.getAuthorizationStatus());
        dto.setFollowersCount(employee.getFollowersCount());
        dto.setLikesCount(employee.getLikesCount());
        dto.setCommentsCount(employee.getCommentsCount());
        dto.setSharesCount(employee.getSharesCount());
        dto.setProfileViewsCount(employee.getProfileViewsCount());
        dto.setVideosCount(employee.getVideosCount());
        dto.setLastSyncTime(employee.getLastSyncTime());
        dto.setStatus(employee.getStatus());
        dto.setCreateTime(employee.getCreateTime());
        dto.setUpdateTime(employee.getUpdateTime());
        return dto;
    }
}
