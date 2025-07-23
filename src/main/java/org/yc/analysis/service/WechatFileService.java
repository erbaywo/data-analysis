package org.yc.analysis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yc.analysis.config.FeiShuConfig;
import org.yc.analysis.exception.BusinessException;
import org.yc.analysis.model.AccountData;
import org.yc.analysis.model.VideoData;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WechatFileService {

    private final CsvAnalysisService csvService;
    private final FeiShuService feishuService;
    private final FeiShuConfig feishuConfig;


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
        boolean success = feishuService.batchCreateVideoRecords(
                feishuConfig.getAppToken(),
                feishuConfig.getVideoTableId(),
                videoDataList
        );

        if (!success) {
            throw new BusinessException("上传视频数据到飞书失败");
        }

        log.info("成功上传{}条视频数据到飞书", videoDataList.size());
    }
}
