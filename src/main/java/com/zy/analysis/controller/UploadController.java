package com.zy.analysis.controller;

import com.zy.analysis.service.UploadService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class UploadController {

    @Resource
    private  UploadService uploadService;

    /**
     * 首页
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 上传账号数据
     */
    @PostMapping("/api/upload/account")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadAccountData(
            @RequestParam("file") MultipartFile file,
            @RequestParam("date") String dateStr) {

        Map<String, Object> response = new HashMap<>();

        try {
            // 验证文件
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "请选择CSV文件");
                return ResponseEntity.badRequest().body(response);
            }

            if (!file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
                response.put("success", false);
                response.put("message", "只支持CSV格式文件");
                return ResponseEntity.badRequest().body(response);
            }


            // 处理上传
            boolean success = uploadService.uploadAccountData(file, dateStr);

            if (success) {
                response.put("success", true);
                response.put("message", "账号数据上传成功");
            } else {
                response.put("success", false);
                response.put("message", "账号数据上传失败，请检查文件格式或稍后重试");
            }

        } catch (Exception e) {
//            log.error("上传账号数据异常", e);
            response.put("success", false);
            response.put("message", "上传失败：" + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 上传视频数据
     */
    @PostMapping("/api/upload/video")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadVideoData(
            @RequestParam("file") MultipartFile file) {

        Map<String, Object> response = new HashMap<>();

        try {
            // 验证文件
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "请选择CSV文件");
                return ResponseEntity.badRequest().body(response);
            }

            if (!file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
                response.put("success", false);
                response.put("message", "只支持CSV格式文件");
                return ResponseEntity.badRequest().body(response);
            }

            // 处理上传
            boolean success = uploadService.uploadVideoData(file);

            if (success) {
                response.put("success", true);
                response.put("message", "视频数据上传成功");
            } else {
                response.put("success", false);
                response.put("message", "视频数据上传失败，请检查文件格式或稍后重试");
            }

        } catch (Exception e) {
//            log.error("上传视频数据异常", e);
            response.put("success", false);
            response.put("message", "上传失败：" + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 初始化飞书表格（管理员功能）
     */
    @PostMapping("/api/admin/init-tables")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> initializeTables() {
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> result = uploadService.initializeFeishuTables();
            response.put("data", result);
            response.put("success", true);
            response.put("message", "初始化成功");
        } catch (Exception e) {
//            log.error("初始化飞书表格异常", e);
            response.put("success", false);
            response.put("message", "初始化失败：" + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}