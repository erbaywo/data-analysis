package org.yc.analysis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.yc.analysis.dto.DouyinDataResponse;
import org.yc.analysis.dto.DouyinTokenResponse;
import org.yc.analysis.dto.DouyinUserInfoResponse;
import org.yc.analysis.exception.BusinessException;
import org.yc.analysis.model.DouyinUserInfo;
import org.yc.analysis.repository.DouyinUserInfoRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DouyinService {

    private final DouyinUserInfoRepository userInfoRepository;
    private final WebClient webClient = WebClient.builder().build();

    @Value("${douyin.client-id}")
    private String clientId;

    @Value("${douyin.client-secret}")
    private String clientSecret;

    @Value("${douyin.redirect-uri}")
    private String redirectUri;

    // 抖音 API 地址
    private static final String DOUYIN_TOKEN_URL = "https://open.douyin.com/oauth/access_token/";
    private static final String DOUYIN_USER_INFO_URL = "https://open.douyin.com/oauth/userinfo/";

    // 抖音数据 API 地址
    private static final String DOUYIN_FANS_DATA_URL = "https://open.douyin.com/api/douyin/v1/data/external/user/fans/";
    private static final String DOUYIN_VIDEO_DATA_URL = "https://open.douyin.com/api/douyin/v1/data/external/user/item/";
    private static final String DOUYIN_LIKE_DATA_URL = "https://open.douyin.com/api/douyin/v1/data/external/user/like/";
    private static final String DOUYIN_COMMENT_DATA_URL = "https://open.douyin.com/api/douyin/v1/data/external/user/comment/";
    private static final String DOUYIN_SHARE_DATA_URL = "https://open.douyin.com/api/douyin/v1/data/external/user/share/";
    private static final String DOUYIN_PROFILE_DATA_URL = "https://open.douyin.com/api/douyin/v1/data/external/user/profile/";

    /**
     * 构建授权URL
     */
    public String buildAuthUrl() {
        try {
            String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
            String authUrl = "https://open.douyin.com/platform/oauth/connect/" +
                    "?client_key=" + clientId +
                    "&response_type=code" +
                    "&scope=user_info" +
                    "&redirect_uri=" + encodedRedirectUri;

            log.info("构建抖音授权URL: {}", authUrl);
            return authUrl;
        } catch (Exception e) {
            log.error("构建授权URL失败", e);
            throw new BusinessException("构建授权URL失败");
        }
    }

    /**
     * 处理回调逻辑
     */
    public DouyinUserInfo handleCallback(String code) {
        try {
            log.info("开始处理抖音授权回调，code: {}", code);

            // 1. 用code换access_token
            DouyinTokenResponse tokenResponse = getAccessToken(code);
            if (tokenResponse.getErrorCode() != null && tokenResponse.getErrorCode() != 0) {
                throw new BusinessException("获取访问令牌失败: " + tokenResponse.getDescription());
            }

            // 2. 获取用户信息
            DouyinUserInfoResponse userInfoResponse = getUserInfo(tokenResponse.getAccessToken(), tokenResponse.getOpenId());
            if (userInfoResponse.getErrorCode() != null && userInfoResponse.getErrorCode() != 0) {
                throw new BusinessException("获取用户信息失败: " + userInfoResponse.getData().getDescription());
            }

            // 3. 保存用户信息到数据库
            DouyinUserInfo userInfo = saveUserInfo(tokenResponse, userInfoResponse);

            log.info("抖音授权处理完成，用户ID: {}", userInfo.getOpenId());
            return userInfo;

        } catch (Exception e) {
            log.error("处理抖音授权回调失败", e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("授权处理失败: " + e.getMessage());
        }
    }

    /**
     * 获取访问令牌
     */
    private DouyinTokenResponse getAccessToken(String code) {
        try {
            log.info("开始获取访问令牌，code: {}", code);

            DouyinTokenResponse response = webClient.post()
                    .uri(DOUYIN_TOKEN_URL)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .bodyValue("client_key=" + clientId +
                            "&client_secret=" + clientSecret +
                            "&code=" + code +
                            "&grant_type=authorization_code")
                    .retrieve()
                    .bodyToMono(DouyinTokenResponse.class)
                    .block();
            log.info("获取访问令牌响应: {}", response);
            return response;

        } catch (Exception e) {
            log.error("获取访问令牌失败", e);
            throw new BusinessException("获取访问令牌失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户信息
     */
    private DouyinUserInfoResponse getUserInfo(String accessToken, String openId) {
        try {
            log.info("开始获取用户信息，openId: {}", openId);

            DouyinUserInfoResponse response = webClient.get()
                    .uri(DOUYIN_USER_INFO_URL + "?access_token=" + accessToken + "&open_id=" + openId)
                    .retrieve()
                    .bodyToMono(DouyinUserInfoResponse.class)
                    .block();

            log.info("获取用户信息响应: {}", response);
            return response;

        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            throw new BusinessException("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 保存用户信息到数据库
     */
    private DouyinUserInfo saveUserInfo(DouyinTokenResponse tokenResponse, DouyinUserInfoResponse userInfoResponse) {
        try {
            String openId = tokenResponse.getOpenId();
            DouyinUserInfoResponse.UserData userData = userInfoResponse.getData();

            log.info("开始保存用户信息，openId: {}", openId);

            // 查找是否已存在该用户
            Optional<DouyinUserInfo> existingUser = userInfoRepository.findByOpenId(openId);

            DouyinUserInfo userInfo;
            if (existingUser.isPresent()) {
                // 更新现有用户信息
                userInfo = existingUser.get();
                log.info("更新现有用户信息，openId: {}", openId);
            } else {
                // 创建新用户
                userInfo = new DouyinUserInfo();
                userInfo.setOpenId(openId);
                log.info("创建新用户，openId: {}", openId);
            }

            // 更新用户基本信息
            userInfo.setUnionId(userData.getUnionId());
            userInfo.setNickname(userData.getNickname());
            userInfo.setAvatar(userData.getAvatar());

            // 更新令牌信息
            userInfo.setAccessToken(tokenResponse.getAccessToken());
            userInfo.setRefreshToken(tokenResponse.getRefreshToken());
            userInfo.setExpiresIn(tokenResponse.getExpiresIn());
            userInfo.setTokenExpireTime(LocalDateTime.now().plusSeconds(tokenResponse.getExpiresIn()));
            userInfo.setStatus(1);

            // 保存到数据库
            userInfo = userInfoRepository.save(userInfo);

            log.info("用户信息保存成功，ID: {}, openId: {}", userInfo.getId(), userInfo.getOpenId());
            return userInfo;

        } catch (Exception e) {
            log.error("保存用户信息失败", e);
            throw new BusinessException("保存用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 根据 openId 查询用户信息
     */
    public DouyinUserInfo getUserByOpenId(String openId) {
        log.info("查询用户信息，openId: {}", openId);
        return userInfoRepository.findByOpenId(openId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
    }

    /**
     * 获取粉丝数据
     */
    public DouyinDataResponse getFansData(String accessToken, String openId, String beginDate, String endDate) {
        return getUserDataByType(DOUYIN_FANS_DATA_URL, accessToken, openId, beginDate, endDate, "粉丝");
    }

    /**
     * 获取视频数据
     */
    public DouyinDataResponse getVideoData(String accessToken, String openId, String beginDate, String endDate) {
        return getUserDataByType(DOUYIN_VIDEO_DATA_URL, accessToken, openId, beginDate, endDate, "视频");
    }

    /**
     * 获取点赞数据
     */
    public DouyinDataResponse getLikeData(String accessToken, String openId, String beginDate, String endDate) {
        return getUserDataByType(DOUYIN_LIKE_DATA_URL, accessToken, openId, beginDate, endDate, "点赞");
    }

    /**
     * 获取评论数据
     */
    public DouyinDataResponse getCommentData(String accessToken, String openId, String beginDate, String endDate) {
        return getUserDataByType(DOUYIN_COMMENT_DATA_URL, accessToken, openId, beginDate, endDate, "评论");
    }

    /**
     * 获取分享数据
     */
    public DouyinDataResponse getShareData(String accessToken, String openId, String beginDate, String endDate) {
        return getUserDataByType(DOUYIN_SHARE_DATA_URL, accessToken, openId, beginDate, endDate, "分享");
    }

    /**
     * 获取主页访问数据
     */
    public DouyinDataResponse getProfileData(String accessToken, String openId, String beginDate, String endDate) {
        return getUserDataByType(DOUYIN_PROFILE_DATA_URL, accessToken, openId, beginDate, endDate, "主页访问");
    }

    /**
     * 通用数据获取方法
     */
    private DouyinDataResponse getUserDataByType(String url, String accessToken, String openId,
                                                String beginDate, String endDate, String dataType) {
        try {
            log.info("开始获取{}数据，openId: {}, 时间范围: {} - {}", dataType, openId, beginDate, endDate);

            DouyinDataResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(url)
                            .queryParam("access_token", accessToken)
                            .queryParam("open_id", openId)
                            .queryParam("date_type", "7")
                            .queryParam("begin_date", beginDate)
                            .queryParam("end_date", endDate)
                            .build())
                    .retrieve()
                    .bodyToMono(DouyinDataResponse.class)
                    .block();

            log.info("获取{}数据响应: {}", dataType, response);
            return response;

        } catch (Exception e) {
            log.error("获取{}数据失败", dataType, e);
            throw new BusinessException("获取" + dataType + "数据失败: " + e.getMessage());
        }
    }
}
