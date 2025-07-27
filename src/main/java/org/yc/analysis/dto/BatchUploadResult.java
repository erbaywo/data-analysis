package org.yc.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分批上传结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchUploadResult {
    private  int totalCount;
    private  int successCount;
    private  int failedCount;
    private  String errorMessage;
    public String getSummary() {
        return String.format("总计: %d条，成功: %d条，失败: %d条", totalCount, successCount, failedCount);
    }
    public boolean isAllSuccess() { return failedCount == 0; }
}
