package org.yc.analysis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yc.analysis.service.DouyinService;

import java.io.IOException;

@RestController
@RequestMapping("/api/douyin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "抖音授权", description = "抖音用户授权相关接口")
public class DouyinAuthController {

    private final DouyinService douyinService;

    /**
     * 获取抖音授权链接
     * 前端请求授权链接，后端直接重定向到抖音授权页面
     * 1、用户在抖音授权页面，做账密或者授权操作
     * 2、用户授权成功，抖音会重定向到配置的回调地址，并携带code参数
     * 3、后端拿到code，请求抖音获取access_token
     */
    @GetMapping("/auth")
    @Operation(summary = "获取抖音授权链接", description = "重定向到抖音授权页面")
    public void auth(HttpServletResponse response) throws IOException {
        try {
            log.info("用户请求抖音授权");
            String authUrl = douyinService.buildAuthUrl();
            log.info("重定向到抖音授权页面: {}", authUrl);
            response.sendRedirect(authUrl);
        } catch (Exception e) {
            log.error("抖音授权重定向失败", e);
            throw e;
        }
    }

    /**
     * 抖音授权回调接口
     * 抖音授权成功后会回调此接口，携带授权码code
     */
//    @GetMapping("/callback")
//    @Operation(summary = "抖音授权回调", description = "处理抖音授权回调，获取用户信息并保存")
//    public ResponseResult<DouyinUserInfo> callback(
//            @Parameter(description = "抖音授权码", required = true)
//            @RequestParam String code) {
//        try {
//            log.info("收到抖音授权回调，code: {}", code);
//            DouyinUserInfo userInfo = douyinService.handleCallback(code);
//            log.info("抖音授权处理成功，用户: {}", userInfo.getNickname());
//            return ResponseResult.success(userInfo);
//        } catch (Exception e) {
//            log.error("抖音授权回调处理失败", e);
//            throw e;
//        }
//    }
//
//    /**
//     * 根据 openId 查询用户信息
//     */
//    @GetMapping("/user/{openId}")
//    @Operation(summary = "查询用户信息", description = "根据openId查询抖音用户信息")
//    public ResponseResult<DouyinUserInfo> getUserByOpenId(
//            @Parameter(description = "抖音用户openId", required = true)
//            @PathVariable String openId) {
//        try {
//            log.info("查询用户信息，openId: {}", openId);
//            DouyinUserInfo userInfo = douyinService.getUserByOpenId(openId);
//            return ResponseResult.success(userInfo);
//        } catch (Exception e) {
//            log.error("查询用户信息失败", e);
//            throw e;
//        }
//    }
}