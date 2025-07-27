package org.yc.analysis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.yc.analysis.dto.BatchUploadResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchUploadService {

    /**
     * 通用分批异步上传方法
     * 
     * @param dataList 要上传的数据列表
     * @param batchSize 每批数据量
     * @param uploadFunction 上传函数，接收一批数据，返回是否成功
     * @param dataType 数据类型名称（用于日志）
     * @param delayMs 批次间延迟毫秒数
     * @return 上传结果
     */
    @Async("taskExecutor")
    public <T> CompletableFuture<BatchUploadResult> batchUploadAsync(
            List<T> dataList,
            int batchSize,
            Function<List<T>, Boolean> uploadFunction,
            String dataType,
            long delayMs) {

        log.info("开始分批异步上传{}数据，总计: {}条", dataType, dataList.size());

        try {
            if (dataList.isEmpty()) {
                return CompletableFuture.completedFuture(
                    new BatchUploadResult(0, 0, 0, dataType + "数据列表为空"));
            }

            int totalBatches = (int) Math.ceil((double) dataList.size() / batchSize);
            int successCount = 0;
            int failedCount = 0;
            StringBuilder errorMessages = new StringBuilder();

            for (int i = 0; i < totalBatches; i++) {
                int startIndex = i * batchSize;
                int endIndex = Math.min(startIndex + batchSize, dataList.size());
                List<T> batch = dataList.subList(startIndex, endIndex);

                log.info("正在上传{}第 {}/{} 批，数据量: {}", dataType, i + 1, totalBatches, batch.size());

                try {
                    boolean success = uploadFunction.apply(batch);
                    
                    if (success) {
                        successCount += batch.size();
                        log.info("{}第 {}/{} 批上传成功，累计成功: {}", dataType, i + 1, totalBatches, successCount);
                    } else {
                        failedCount += batch.size();
                        String errorMsg = String.format("%s第 %d/%d 批上传失败", dataType, i + 1, totalBatches);
                        errorMessages.append(errorMsg).append("; ");
                        log.error(errorMsg);
                    }

                    // 批次间延迟，避免API限流
                    if (delayMs > 0 && i < totalBatches - 1) {
                        Thread.sleep(delayMs);
                    }

                } catch (Exception e) {
                    failedCount += batch.size();
                    String errorMsg = String.format("%s第 %d/%d 批上传异常: %s", dataType, i + 1, totalBatches, e.getMessage());
                    errorMessages.append(errorMsg).append("; ");
                    log.error(errorMsg, e);
                }
            }

            BatchUploadResult result = new BatchUploadResult(
                dataList.size(), 
                successCount, 
                failedCount,
                    !errorMessages.isEmpty() ? errorMessages.toString() : null
            );

            log.info("{}数据分批上传完成！{}", dataType, result.getSummary());
            return CompletableFuture.completedFuture(result);

        } catch (Exception e) {
            log.error("{}数据分批上传失败", dataType, e);
            return CompletableFuture.completedFuture(
                new BatchUploadResult(dataList.size(), 0, dataList.size(), "上传失败: " + e.getMessage()));
        }
    }
}