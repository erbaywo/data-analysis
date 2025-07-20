package com.zy.analysis.service;

import com.lark.oapi.service.bitable.v1.model.App;
import com.zy.analysis.config.FeishuConfig;
import com.zy.analysis.model.AccountData;
import com.zy.analysis.model.VideoData;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Data
@Slf4j
//@RequiredArgsConstructor
public class UploadService {

    @Resource
    private  CsvService csvService;
    @Resource
    private FeishuService feishuService;
    @Resource
    private FeishuConfig feishuConfig;


    /**
     * 上传账号数据
     */
    public boolean uploadAccountData(MultipartFile file, String dataDate) {
        try {
            log.info("开始处理账号数据文件: {}", file.getOriginalFilename());

            // 1. 解析CSV文件
            List<AccountData> accountDataList = csvService.parseAccountCsv(file, dataDate);
            if (accountDataList.isEmpty()) {
                log.warn("CSV文件中没有有效数据");
                return false;
            }

            log.info("成功保存{}条账号数据到本地数据库", accountDataList.size());

            // 3. 上传到飞书多维表格
            boolean success = feishuService.batchCreateAccountRecords(
                    feishuConfig.getAppToken(),
                    feishuConfig.getAccountTableId(),
                    accountDataList
            );

            if (success) {
                log.info("成功上传{}条账号数据到飞书", accountDataList.size());
                return true;
            } else {
                log.error("上传账号数据到飞书失败");
                return false;
            }

        } catch (Exception e) {
            log.error("上传账号数据异常", e);
            return false;
        }
    }

    /**
     * 上传视频数据
     */
    public boolean uploadVideoData(MultipartFile file) {
        try {
            log.info("开始处理视频数据文件: {}", file.getOriginalFilename());

            // 1. 解析CSV文件
            List<VideoData> videoDataList = csvService.parseVideoCsv(file);
            if (videoDataList.isEmpty()) {
                log.warn("CSV文件中没有有效数据");
                return false;
            }
            log.info("成功保存{}条视频数据到本地数据库", videoDataList.size());

            // 3. 上传到飞书多维表格
            boolean success = feishuService.batchCreateVideoRecords(
                    feishuConfig.getAppToken(),
                    feishuConfig.getVideoTableId(),
                    videoDataList
            );

            if (success) {
                log.info("成功上传{}条视频数据到飞书", videoDataList.size());
                return true;
            } else {
                log.error("上传视频数据到飞书失败");
                return false;
            }

        } catch (Exception e) {
            log.error("上传视频数据异常", e);
            return false;
        }
    }

    /**
     * 初始化飞书表格（仅在首次使用时调用）
     */
    public Map<String, Object> initializeFeishuTables() {
        Map<String,Object> result = new HashMap<>();
        try {
            //创建多维表格
            App app = feishuService.createBitable("视频监控数据分析表");
            if (app == null) {
                result.put("result", false);
                return result;
            }
            String appToken = app.getAppToken();
            // 创建账号数据表
            String accountTableId = feishuService.createTable(
                    appToken,
                    "微信视频号账号数据",
                    feishuService.createAccountFields()
            );

            // 创建视频数据表
            String videoTableId = feishuService.createTable(
                    appToken,
                    "微信视频号视频数据",
                    feishuService.createVideoFields()
            );

            log.info("账号数据表ID: {}", accountTableId);
            log.info("视频数据表ID: {}", videoTableId);

            feishuConfig.setAccountTableId(accountTableId);
            feishuConfig.setVideoTableId(videoTableId);
            feishuConfig.setAppToken(appToken);

            result.put("result", accountTableId != null && videoTableId != null);
            result.put("accountTableId", accountTableId);
            result.put("videoTableId", videoTableId);
            result.put("appToken", appToken);
            result.put("url", app.getUrl());
            return result;
        } catch (Exception e) {
            log.error("初始化飞书表格异常", e);
            return null;
        }
    }
}
