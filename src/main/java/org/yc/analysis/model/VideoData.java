package org.yc.analysis.model;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoData {

    private Long id;

    private String videoDescription; // 视频描述

    private String videoId; // 视频ID

    private String authorName; // 作者昵称

    private String publishTime; // 发布时间
    private String completionRate; // 完播率

    private String avgPlayDuration; // 平均播放时长
    private Long playCount; // 播放量

    private Long recommendCount; // 推荐

    private Integer likeCount; // 喜欢

    private Integer commentCount; // 评论量

    private Integer shareCount; // 分享量

    private Integer followCount; // 关注量

    private Integer chatShareCount; // 转发聊天和朋友圈

    private Integer ringtoneCount; // 设为铃声

    private Integer statusCount; // 设为状态

    private Integer coverCount; // 设为朋友圈封面

    private LocalDateTime createdAt;
}
