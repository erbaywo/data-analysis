package org.yc.analysis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.yc.analysis.common.response.ResponseResult;
import org.yc.analysis.dto.DouyinDataResponse;
import org.yc.analysis.model.DouyinUserInfo;
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
    @GetMapping("/callback")
    @Operation(summary = "抖音授权回调", description = "处理抖音授权回调，获取用户信息并保存")
    public ResponseResult<DouyinUserInfo> callback(
            @Parameter(description = "抖音授权码", required = true)
            @RequestParam String code) {
        try {
            log.info("收到抖音授权回调，code: {}", code);
            DouyinUserInfo userInfo = douyinService.handleCallback(code);
            log.info("抖音授权处理成功，用户: {}", userInfo.getNickname());
            return ResponseResult.success(userInfo);
        } catch (Exception e) {
            log.error("抖音授权回调处理失败", e);
            throw e;
        }
    }

    /**
     * 根据 openId 查询用户信息
     */
    @GetMapping("/user/{openId}")
    @Operation(summary = "查询用户信息", description = "根据openId查询抖音用户信息")
    public ResponseResult<DouyinUserInfo> getUserByOpenId(
            @Parameter(description = "抖音用户openId", required = true)
            @PathVariable String openId) {
        try {
            log.info("查询用户信息，openId: {}", openId);
            DouyinUserInfo userInfo = douyinService.getUserByOpenId(openId);
            return ResponseResult.success(userInfo);
        } catch (Exception e) {
            log.error("查询用户信息失败", e);
            throw e;
        }
    }

    /**
     * 获取粉丝数据
     */
    @GetMapping("/data/fans/{openId}")
    @Operation(summary = "获取粉丝数据", description = "获取用户粉丝增长和取消关注数据")
    public ResponseResult<DouyinDataResponse> getFansData(
            @PathVariable String openId,
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        DouyinUserInfo userInfo = douyinService.getUserByOpenId(openId);
        DouyinDataResponse data = douyinService.getFansData(userInfo.getAccessToken(), openId, beginDate, endDate);
        return ResponseResult.success(data);
    }

    /**
     * 获取视频数据
     */
    @GetMapping("/data/video/{openId}")
    @Operation(summary = "获取视频数据", description = "获取用户视频播放数据")
    public ResponseResult<DouyinDataResponse> getVideoData(
            @PathVariable String openId,
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        DouyinUserInfo userInfo = douyinService.getUserByOpenId(openId);
        DouyinDataResponse data = douyinService.getVideoData(userInfo.getAccessToken(), openId, beginDate, endDate);
        return ResponseResult.success(data);
    }

    /**
     * 获取点赞数据
     */
    @GetMapping("/data/like/{openId}")
    @Operation(summary = "获取点赞数据", description = "获取用户获得点赞数据")
    public ResponseResult<DouyinDataResponse> getLikeData(
            @PathVariable String openId,
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        DouyinUserInfo userInfo = douyinService.getUserByOpenId(openId);
        DouyinDataResponse data = douyinService.getLikeData(userInfo.getAccessToken(), openId, beginDate, endDate);
        return ResponseResult.success(data);
    }

    /**
     * 获取评论数据
     */
    @GetMapping("/data/comment/{openId}")
    @Operation(summary = "获取评论数据", description = "获取用户获得评论数据")
    public ResponseResult<DouyinDataResponse> getCommentData(
            @PathVariable String openId,
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        DouyinUserInfo userInfo = douyinService.getUserByOpenId(openId);
        DouyinDataResponse data = douyinService.getCommentData(userInfo.getAccessToken(), openId, beginDate, endDate);
        return ResponseResult.success(data);
    }

    /**
     * 获取分享数据
     */
    @GetMapping("/data/share/{openId}")
    @Operation(summary = "获取分享数据", description = "获取用户获得分享数据")
    public ResponseResult<DouyinDataResponse> getShareData(
            @PathVariable String openId,
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        DouyinUserInfo userInfo = douyinService.getUserByOpenId(openId);
        DouyinDataResponse data = douyinService.getShareData(userInfo.getAccessToken(), openId, beginDate, endDate);
        return ResponseResult.success(data);
    }

    /**
     * 获取主页访问数据
     */
    @GetMapping("/data/profile/{openId}")
    @Operation(summary = "获取主页访问数据", description = "获取用户主页访问数据")
    public ResponseResult<DouyinDataResponse> getProfileData(
            @PathVariable String openId,
            @RequestParam String beginDate,
            @RequestParam String endDate) {
        DouyinUserInfo userInfo = douyinService.getUserByOpenId(openId);
        DouyinDataResponse data = douyinService.getProfileData(userInfo.getAccessToken(), openId, beginDate, endDate);
        return ResponseResult.success(data);
    }
}