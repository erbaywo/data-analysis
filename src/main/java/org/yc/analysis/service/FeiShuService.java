package org.yc.analysis.service;


import com.lark.oapi.Client;
import com.lark.oapi.service.bitable.v1.model.*;

import cn.hutool.core.date.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.yc.analysis.config.FeiShuConfig;
import org.yc.analysis.exception.BusinessException;
import org.yc.analysis.model.AccountData;
import org.yc.analysis.model.VideoData;

import java.nio.charset.StandardCharsets;
import java.util.*;

import com.lark.oapi.core.utils.Jsons;
@Service
@RequiredArgsConstructor
@Slf4j
public class FeiShuService {

    private final FeiShuConfig feishuConfig;

    private Client getClient() {
        return Client.newBuilder(feishuConfig.getAppId(), feishuConfig.getAppSecret()).build();
    }

    /**
     * 批量添加账号数据记录
     */
    public boolean batchCreateAccountRecords(String appToken, String tableId, List<AccountData> accountDataList) {
        if (accountDataList == null || accountDataList.isEmpty()) {
            log.warn("账号数据列表为空，跳过上传");
            return true;
        }

        try {
            log.info("开始批量创建{}条账号记录到飞书", accountDataList.size());
            Client client = getClient();
            List<AppTableRecord> records = buildAccountRecords(accountDataList);

            BatchCreateAppTableRecordReq req = BatchCreateAppTableRecordReq.newBuilder()
                    .appToken(appToken)
                    .tableId(tableId)
                    .batchCreateAppTableRecordReqBody(BatchCreateAppTableRecordReqBody.newBuilder()
                            .records(records.toArray(new AppTableRecord[0]))
                            .build())
                    .build();

            BatchCreateAppTableRecordResp resp = client.bitable().appTableRecord().batchCreate(req);
            return handleFeishuResponse(resp, "账号数据");

        } catch (Exception e) {
            log.error("批量创建账号记录异常", e);
            throw new BusinessException("上传账号数据到飞书失败: " + e.getMessage());
        }
    }

    /**
     * 构建账号记录列表
     */
    private List<AppTableRecord> buildAccountRecords(List<AccountData> accountDataList) {
        List<AppTableRecord> records = new ArrayList<>();

        for (AccountData data : accountDataList) {
            Map<String, Object> fields = buildAccountFields(data);
            records.add(AppTableRecord.newBuilder()
                    .fields(fields)
                    .build());
        }

        return records;
    }

    /**
     * 构建账号数据字段映射
     */
    private Map<String, Object> buildAccountFields(AccountData data) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("作者名称", data.getAuthorName());
        fields.put("作者ID", data.getAuthorId());
        fields.put("关注者", data.getFollowers());
        fields.put("新增关注", data.getNewFollowers());
        fields.put("发表量", data.getPublishCount());
        fields.put("播放量", data.getPlayCount());
        fields.put("推荐", data.getRecommendCount());
        fields.put("评论量", data.getCommentCount());
        fields.put("分享量", data.getShareCount());
        fields.put("喜欢", data.getLikeCount());
        fields.put("员工姓名", "");
        fields.put("所属团队", "");

        long timestamp = DateUtil.parseDateTime(data.getDataDate() + " 00:00:00").getTime();
        fields.put("日期", timestamp);

        return fields;
    }



    /**
     * 批量添加视频数据记录
     */
    public boolean batchCreateVideoRecords(String appToken, String tableId, List<VideoData> videoDataList) {
        if (videoDataList == null || videoDataList.isEmpty()) {
            log.warn("视频数据列表为空，跳过上传");
            return true;
        }

        try {
            log.info("开始批量创建{}条视频记录到飞书", videoDataList.size());
            Client client = getClient();
            List<AppTableRecord> records = buildVideoRecords(videoDataList);

            BatchCreateAppTableRecordReq req = BatchCreateAppTableRecordReq.newBuilder()
                    .appToken(appToken)
                    .tableId(tableId)
                    .batchCreateAppTableRecordReqBody(BatchCreateAppTableRecordReqBody.newBuilder()
                            .records(records.toArray(new AppTableRecord[0]))
                            .build())
                    .build();

            BatchCreateAppTableRecordResp resp = client.bitable().appTableRecord().batchCreate(req);
            return handleFeishuResponse(resp, "视频数据");

        } catch (Exception e) {
            log.error("批量创建视频记录异常", e);
            throw new BusinessException("上传视频数据到飞书失败: " + e.getMessage());
        }
    }

    /**
     * 构建视频记录列表
     */
    private List<AppTableRecord> buildVideoRecords(List<VideoData> videoDataList) {
        List<AppTableRecord> records = new ArrayList<>();

        for (VideoData data : videoDataList) {
            Map<String, Object> fields = buildVideoFields(data);
            records.add(AppTableRecord.newBuilder()
                    .fields(fields)
                    .build());
        }

        return records;
    }

    /**
     * 构建视频数据字段映射
     */
    private Map<String, Object> buildVideoFields(VideoData data) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("视频描述", data.getVideoDescription());
        fields.put("视频ID", data.getVideoId());
        fields.put("作者昵称", data.getAuthorName());

        long timestamp = DateUtil.parseDateTime(data.getPublishTime() + " 00:00:00").getTime();
        fields.put("发布时间", timestamp);

        // 处理完播率
        if(Objects.equals(data.getCompletionRate(), "-")) {
            fields.put("完播率", 0.0);
        } else {
            String completionRateStr = data.getCompletionRate().replace("%", "");
            double completionRate = Double.parseDouble(completionRateStr) / 100.0;
            fields.put("完播率", completionRate);
        }

        // 处理平均播放时长
        if (Objects.equals(data.getAvgPlayDuration(), "-")) {
            fields.put("平均播放时长", 0.0);
        } else {
            String avgPlayDuration = data.getAvgPlayDuration().replace("秒", "");
            double avgPlayDurationInDouble = Double.parseDouble(avgPlayDuration);
            fields.put("平均播放时长", avgPlayDurationInDouble);
        }
        fields.put("播放量", data.getPlayCount());
        fields.put("推荐", data.getRecommendCount());
        fields.put("喜欢", data.getLikeCount());
        fields.put("评论量", data.getCommentCount());
        fields.put("分享量", data.getShareCount());
        fields.put("关注量", data.getFollowCount());

        return fields;
    }

    /**
     * 处理飞书API响应
     */
    private boolean handleFeishuResponse(BatchCreateAppTableRecordResp resp, String dataType) {
        if (!resp.success()) {
            String errorMsg = String.format("code:%s,msg:%s,reqId:%s",
                    resp.getCode(), resp.getMsg(), resp.getRequestId());
            log.error("飞书API调用失败 - {}: {}", dataType, errorMsg);

            if (resp.getRawResponse() != null && resp.getRawResponse().getBody() != null) {
                String responseBody = new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8);
                log.debug("飞书API响应详情: {}", responseBody);
            }
            return false;
        }

        log.info("成功上传{}到飞书", dataType);
        log.debug("飞书API响应数据: {}", Jsons.DEFAULT.toJson(resp.getData()));
        return resp.getCode() == 0;
    }
}
