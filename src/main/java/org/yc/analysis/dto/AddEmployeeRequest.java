package org.yc.analysis.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddEmployeeRequest {
    
    @NotBlank(message = "员工姓名不能为空")
    private String name;
    
    @NotBlank(message = "抖音账号不能为空")
    private String douyinAccount;
}
