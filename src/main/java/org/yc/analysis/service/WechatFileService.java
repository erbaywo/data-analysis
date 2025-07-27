package org.yc.analysis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yc.analysis.config.FeiShuConfig;
import org.yc.analysis.dto.BatchUploadResult;
import org.yc.analysis.exception.BusinessException;
import org.yc.analysis.model.AccountData;
import org.yc.analysis.model.VideoData;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class WechatFileService {

    private final CsvAnalysisService csvService;
    private final FeiShuService feishuService;
    private final FeiShuConfig feishuConfig;
    private final BatchUploadService batchUploadService;


    /**
     * 上传账号数据
     */
    public void uploadAccountData(MultipartFile file, String dataDate) {
        log.info("开始处理账号数据文件: {}", file.getOriginalFilename());

        // 1. 解析CSV文件
        List<AccountData> accountDataList = csvService.parseAccountCsv(file, dataDate);
        if (accountDataList.isEmpty()) {
            throw new BusinessException("CSV文件中没有有效数据");
        }

        log.info("成功解析{}条账号数据", accountDataList.size());

        // 2. 上传到飞书多维表格
        boolean success = feishuService.batchCreateAccountRecords(
                feishuConfig.getAppToken(),
                feishuConfig.getAccountTableId(),
                accountDataList
        );

        if (!success) {
            throw new BusinessException("上传账号数据到飞书失败");
        }

        log.info("成功上传{}条账号数据到飞书", accountDataList.size());
    }

    /**
     * 上传视频数据
     */
    public void uploadVideoData(MultipartFile file) {
        log.info("开始处理视频数据文件: {}", file.getOriginalFilename());

        // 1. 解析CSV文件
        List<VideoData> videoDataList = csvService.parseVideoCsv(file);
        if (videoDataList.isEmpty()) {
            throw new BusinessException("CSV文件中没有有效数据");
        }

        log.info("成功解析{}条视频数据", videoDataList.size());

        // 2. 上传到飞书多维表格
//        boolean success = feishuService.batchCreateVideoRecords(
//                feishuConfig.getAppToken(),
//                feishuConfig.getVideoTableId(),
//                videoDataList
//        );
        // 2. 使用通用分批上传服务
        try {
            CompletableFuture<BatchUploadResult> uploadFuture =
                    batchUploadService.batchUploadAsync(
                            videoDataList,
                            1000, // 每批1000条
                            batch -> feishuService.batchCreateVideoRecords(
                                    feishuConfig.getAppToken(),
                                    feishuConfig.getVideoTableId(),
                                    batch
                            ),
                            "视频",
                            500L // 批次间延迟500ms
                    );
            BatchUploadResult result = uploadFuture.get();
            if (!result.isAllSuccess()) {
                log.warn("视频数据上传部分失败: {}", result.getErrorMessage());
            }
            String summary = result.getSummary();
            log.info("上传数据到飞书结束，结果:{}", summary);
        } catch (Exception e) {
            log.error("异步上传视频数据失败", e);
            throw new BusinessException("上传失败: " + e.getMessage());
        }
    }
}
