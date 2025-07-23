package org.yc.analysis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yc.analysis.common.response.ResponseResult;
import org.yc.analysis.service.WechatFileService;

@Controller
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Tag(name = "文件上传", description = "CSV文件上传相关接口")
public class WechatFileController {

    private final WechatFileService wechatFileService;

    /**
     * 首页
     */
    @GetMapping("/")
    @Operation(summary = "首页", description = "返回首页")
    public String index() {
        return "index";
    }

    /**
     * 上传账号数据
     */
    @PostMapping("/api/upload/account")
    @ResponseBody
    @Operation(summary = "上传账号数据", description = "上传账号数据CSV文件")
    public ResponseResult<String> uploadAccountData(
            @Parameter(description = "CSV文件", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "数据统计日期", required = true)
            @RequestParam("date") String dateStr) {

        log.info("开始上传账号数据，文件名: {}, 日期: {}", file.getOriginalFilename(), dateStr);

        // 验证文件
        validateCsvFile(file);

        // 处理上传
        wechatFileService.uploadAccountData(file, dateStr);

        log.info("账号数据上传成功");
        return ResponseResult.success("账号数据上传成功");
    }

    /**
     * 上传视频数据
     */
    @PostMapping("/api/upload/video")
    @ResponseBody
    @Operation(summary = "上传视频数据", description = "上传视频数据CSV文件")
    public ResponseResult<String> uploadVideoData(
            @Parameter(description = "CSV文件", required = true)
            @RequestParam("file") MultipartFile file) {

        log.info("开始上传视频数据，文件名: {}", file.getOriginalFilename());

        // 验证文件
        validateCsvFile(file);

        // 处理上传
        wechatFileService.uploadVideoData(file);

        log.info("视频数据上传成功");
        return ResponseResult.success("视频数据上传成功");
    }

    /**
     * 验证CSV文件
     */
    private void validateCsvFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("请选择CSV文件");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("只支持CSV格式文件");
        }
    }
}