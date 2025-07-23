package org.yc.analysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yc.analysis.model.Employee;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    /**
     * 根据抖音账号查找员工
     */
    Optional<Employee> findByDouyinAccount(String douyinAccount);
    
    /**
     * 根据抖音 openId 查找员工
     */
    Optional<Employee> findByDouyinOpenId(String douyinOpenId);
    
    /**
     * 查找所有启用的员工
     */
    List<Employee> findByStatusOrderByCreateTimeDesc(Integer status);
    
    /**
     * 查找已授权的员工
     */
    @Query("SELECT e FROM Employee e WHERE e.status = 1 AND e.authorizationStatus = 1")
    List<Employee> findAuthorizedEmployees();
    
    /**
     * 根据姓名模糊查询
     */
    List<Employee> findByNameContainingAndStatusOrderByCreateTimeDesc(String name, Integer status);
    
    /**
     * 统计员工数量
     */
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.status = :status")
    Long countByStatus(@Param("status") Integer status);
    
    /**
     * 统计已授权员工数量
     */
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.status = 1 AND e.authorizationStatus = 1")
    Long countAuthorizedEmployees();
}
