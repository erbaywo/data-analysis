package com.zy.analysis.model;


import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
public class AccountData {
    private Long id;

    private String authorName; // 作者名称

    private String authorId; // 作者ID

    private String admin; // 管理员

    private Integer followers; // 关注者

    private Integer newFollowers; // 新增关注

    private Integer publishCount; // 发表量

    private Long playCount; // 播放量

    private Long recommendCount; // 推荐

    private Integer commentCount; // 评论量

    private Integer shareCount; // 分享量

    private Integer likeCount; // 喜欢

    private String dataDate; // 数据统计日期

    private LocalDateTime createdAt;
}

