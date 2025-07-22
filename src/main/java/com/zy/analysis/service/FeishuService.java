package com.zy.analysis.service;


import com.lark.oapi.Client;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.core.response.error.Error;
import com.lark.oapi.core.response.error.ErrorDetail;
import com.lark.oapi.service.bitable.v1.model.*;
import com.lark.oapi.service.search.v2.model.CreateAppReqBody;
import com.zy.analysis.config.FeishuConfig;
import com.zy.analysis.model.AccountData;
import com.zy.analysis.model.VideoData;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import com.google.gson.JsonParser;
import com.lark.oapi.core.utils.Jsons;
import java.util.HashMap;
@Service
//@RequiredArgsConstructor
@Slf4j
public class FeishuService {

    @Resource
    private FeishuConfig feishuConfig;

    private Client getClient() {
        return Client.newBuilder(feishuConfig.getAppId(), feishuConfig.getAppSecret()).build();
    }

    /**
     * 创建多维表格
     */
    public App createBitable(String name) {
        try {
            Client client = getClient();
            CreateAppReq req = CreateAppReq.newBuilder()
                    .reqApp(ReqApp.newBuilder()
                            .name(name)
                            .build())
                    .build();

            CreateAppResp resp = client.bitable().app().create(req);
            if (resp.getCode() == 0) {
                return resp.getData().getApp();
            }
            log.error("创建多维表格失败: {}", resp.getMsg());
            return null;
        } catch (Exception e) {
            log.error("创建多维表格异常", e);
            return null;
        }
    }

    /**
     * 创建数据表
     */
    public String createTable(String appToken, String tableName, List<AppTableCreateHeader> fields) {
        try {
            Client client = getClient();
            CreateAppTableReq req = CreateAppTableReq.newBuilder()
                    .appToken(appToken)
                    .createAppTableReqBody(CreateAppTableReqBody.newBuilder()
                            .table(ReqTable.newBuilder()
                                    .name(tableName)
                                    .fields(fields.toArray(new AppTableCreateHeader[0]))
                                    .build())
                            .build())
                    .build();

            CreateAppTableResp resp = client.bitable().appTable().create(req);
            if (resp.getCode() == 0) {
                return resp.getData().getTableId();
            }
            log.error("创建数据表失败: {}", resp.getMsg());
            return null;
        } catch (Exception e) {
            log.error("创建数据表异常", e);
            return null;
        }
    }

    /**
     * 创建账号数据表字段
     */
    public List<AppTableCreateHeader> createAccountFields() {
        List<AppTableCreateHeader> fields = new ArrayList<>();

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("作者名称")
                .type(1) // 文本
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("作者ID")
                .type(1) // 文本
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("管理员")
                .type(1) // 文本
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("关注者")
                .type(2) // 数字
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("新增关注")
                .type(2) // 数字
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("发表量")
                .type(2) // 数字
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("播放量")
                .type(2) // 数字
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("推荐")
                .type(2) // 数字
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("评论量")
                .type(2) // 数字
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("分享量")
                .type(2) // 数字
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("喜欢")
                .type(2) // 数字
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("数据日期")
                .type(1) // 文本
                .build());

        return fields;
    }

    /**
     * 创建视频数据表字段
     */
    public List<AppTableCreateHeader> createVideoFields() {
        List<AppTableCreateHeader> fields = new ArrayList<>();

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("视频描述")
                .type(1) // 文本
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("视频ID")
                .type(1) // 文本
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("作者昵称")
                .type(1) // 文本
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("发布时间")
                .type(1) // 日期时间
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("完播率")
                .type(1)
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("平均播放时长")
                .type(1) // 数字
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("播放量")
                .type(2) // 数字
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("推荐")
                .type(2) // 数字
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("喜欢")
                .type(2) // 数字
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("评论量")
                .type(2) // 数字
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("分享量")
                .type(2) // 数字
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("关注量")
                .type(2) // 数字
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("转发聊天和朋友圈")
                .type(2) // 数字
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("设为铃声")
                .type(2) // 数字
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("设为状态")
                .type(2) // 数字
                .build());

        fields.add(AppTableCreateHeader.newBuilder()
                .fieldName("设为朋友圈封面")
                .type(2) // 数字
                .build());

        return fields;
    }

    /**
     * 批量添加账号数据记录
     */
    public boolean batchCreateAccountRecords(String appToken, String tableId, List<AccountData> accountDataList) {
        try {
            Client client = getClient();
            List<AppTableRecord> records = new ArrayList<>();

            for (AccountData data : accountDataList) {
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

                records.add(AppTableRecord.newBuilder()
                        .fields(fields)
                        .build());
            }

            BatchCreateAppTableRecordReq req = BatchCreateAppTableRecordReq.newBuilder()
                    .appToken(appToken)
                    .tableId(tableId)
                    .batchCreateAppTableRecordReqBody(BatchCreateAppTableRecordReqBody.newBuilder()
                            .records(records.toArray(new AppTableRecord[0]))
                            .build())
                    .build();

            BatchCreateAppTableRecordResp resp = client.bitable().appTableRecord().batchCreate(req);
            // 处理服务端错误
        if (!resp.success()) {
                System.out.println(String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                        resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.createGSON(true, false).
                        toJson(JsonParser.parseString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8)))));
                return false;
        }

                // 业务数据处理
        System.out.println(Jsons.DEFAULT.toJson(resp.getData()));
        
            return resp.getCode() == 0;
        } catch (Exception e) {
            log.error("批量创建账号记录异常", e);
            return false;
        }
    }

    /**
     * 批量添加视频数据记录
     */
    public boolean batchCreateVideoRecords(String appToken, String tableId, List<VideoData> videoDataList) {
        try {
            Client client = getClient();
            List<AppTableRecord> records = new ArrayList<>();

            for (VideoData data : videoDataList) {
                Map<String, Object> fields = new HashMap<>();
                fields.put("视频描述", data.getVideoDescription());
                fields.put("视频ID", data.getVideoId());
                fields.put("作者昵称", data.getAuthorName());
                long timestamp = DateUtil.parseDateTime(data.getPublishTime() + " 00:00:00").getTime();
                fields.put("发布时间", timestamp);
                String completionRateStr = data.getCompletionRate().replace("%", "");
                double completionRate = Double.parseDouble(completionRateStr) / 100.0;
                fields.put("完播率", completionRate);
                String avgPlayDuration = data.getAvgPlayDuration().replace("秒", "");
                double avgPlayDurationInDouble = Double.parseDouble(avgPlayDuration);
                fields.put("平均播放时长", avgPlayDurationInDouble);
                fields.put("播放量", data.getPlayCount());
                fields.put("推荐", data.getRecommendCount());
                fields.put("喜欢", data.getLikeCount());
                fields.put("评论量", data.getCommentCount());
                fields.put("分享量", data.getShareCount());
                fields.put("关注量", data.getFollowCount());

                records.add(AppTableRecord.newBuilder()
                        .fields(fields)
                        .build());
            }

            BatchCreateAppTableRecordReq req = BatchCreateAppTableRecordReq.newBuilder()
                    .appToken(appToken)
                    .tableId(tableId)
                    .batchCreateAppTableRecordReqBody(BatchCreateAppTableRecordReqBody.newBuilder()
                            .records(records.toArray(new AppTableRecord[0]))
                            .build())
                    .build();

            BatchCreateAppTableRecordResp resp = client.bitable().appTableRecord().batchCreate(req);
            if (!resp.success()) {
                System.out.println(String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                        resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.createGSON(true, false).
                        toJson(JsonParser.parseString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8)))));
                return false;
        }

        System.out.println(Jsons.DEFAULT.toJson(resp.getData()));
            return resp.getCode() == 0;
        } catch (Exception e) {
            log.error("批量创建视频记录异常", e);
            return false;
        }
    }
}
