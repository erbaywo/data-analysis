package com.zy.analysis.service;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.zy.analysis.model.AccountData;
import com.zy.analysis.model.VideoData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
//@RequiredArgsConstructor
public class CsvService {

    /**
     * 解析账号数据CSV文件
     */
    public List<AccountData> parseAccountCsv(MultipartFile file, String dataDate) throws IOException, CsvException {
        List<AccountData> accountDataList = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            List<String[]> allRows = reader.readAll();

            if (allRows.size() < 4) {
                throw new RuntimeException("CSV文件格式不正确，行数不足");
            }

            // 跳过前两行说明，第三行是标题行，从第四行开始是数据
            for (int i = 3; i < allRows.size(); i++) {
                String[] row = allRows.get(i);
                if (row.length < 11) {
                    log.warn("第{}行数据不完整，跳过", i + 1);
                    continue;
                }

                AccountData accountData = new AccountData();
                accountData.setAuthorName(row[0]); // 作者名称
                accountData.setAuthorId(row[1]); // 作者ID
                accountData.setAdmin(row[2]); // 管理员
                accountData.setFollowers(parseInteger(row[3])); // 关注者
                accountData.setNewFollowers(parseInteger(row[4])); // 新增关注
                accountData.setPublishCount(parseInteger(row[5])); // 发表量
                accountData.setPlayCount(parseLong(row[6])); // 播放量
                accountData.setRecommendCount(parseLong(row[7])); // 推荐
                accountData.setCommentCount(parseInteger(row[8])); // 评论量
                accountData.setShareCount(parseInteger(row[9])); // 分享量
                accountData.setLikeCount(parseInteger(row[10])); // 喜欢
                accountData.setDataDate(dataDate);

                accountDataList.add(accountData);
            }
        }

        return accountDataList;
    }

    /**
     * 解析视频数据CSV文件
     */
    public List<VideoData> parseVideoCsv(MultipartFile file) throws IOException, CsvException {
        List<VideoData> videoDataList = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            List<String[]> allRows = reader.readAll();

            if (allRows.size() < 2) {
                throw new RuntimeException("CSV文件格式不正确，行数不足");
            }

            // 第一行是标题行，从第二行开始是数据
            for (int i = 1; i < allRows.size(); i++) {
                String[] row = allRows.get(i);
                if (row.length < 16) {
                    log.warn("第{}行数据不完整，跳过", i + 1);
                    continue;
                }

                VideoData videoData = new VideoData();
                videoData.setVideoDescription(row[0]); // 视频描述
                videoData.setVideoId(row[1]); // 视频ID
                videoData.setAuthorName(row[2]); // 作者昵称
                videoData.setPublishTime(row[3]); // 发布时间
                videoData.setCompletionRate(row[4]); // 完播率
                videoData.setAvgPlayDuration(row[5]); // 平均播放时长
                videoData.setPlayCount(parseLong(row[6])); // 播放量
                videoData.setRecommendCount(parseLong(row[7])); // 推荐
                videoData.setLikeCount(parseInteger(row[8])); // 喜欢
                videoData.setCommentCount(parseInteger(row[9])); // 评论量
                videoData.setShareCount(parseInteger(row[10])); // 分享量
                videoData.setFollowCount(parseInteger(row[11])); // 关注量
                videoData.setChatShareCount(parseInteger(row[12])); // 转发聊天和朋友圈
                videoData.setRingtoneCount(parseInteger(row[13])); // 设为铃声
                videoData.setStatusCount(parseInteger(row[14])); // 设为状态
                videoData.setCoverCount(parseInteger(row[15])); // 设为朋友圈封面

                videoDataList.add(videoData);
            }
        }

        return videoDataList;
    }

    private Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty() || "-".equals(value.trim())) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            log.warn("解析整数失败: {}", value);
            return 0;
        }
    }

    private Long parseLong(String value) {
        if (value == null || value.trim().isEmpty() || "-".equals(value.trim())) {
            return 0L;
        }
        try {
            return Long.parseLong(value.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            log.warn("解析长整数失败: {}", value);
            return 0L;
        }
    }

    private Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty() || "-".equals(value.trim())) {
            return 0.0;
        }
        try {
            String cleanValue = value.trim().replace("%", "").replace(",", "");
            return Double.parseDouble(cleanValue);
        } catch (NumberFormatException e) {
            log.warn("解析小数失败: {}", value);
            return 0.0;
        }
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.trim().isEmpty() || "-".equals(value.trim())) {
            return null;
        }
        try {
            // 尝试多种日期时间格式
            DateTimeFormatter[] formatters = {
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"),
                    DateTimeFormatter.ofPattern("yyyy/MM/dd")
            };

            for (DateTimeFormatter formatter : formatters) {
                try {
                    return LocalDateTime.parse(value.trim(), formatter);
                } catch (Exception ignored) {
                    // 继续尝试下一个格式
                }
            }
            log.warn("解析日期时间失败: {}", value);
            return null;
        } catch (Exception e) {
            log.warn("解析日期时间异常: {}", value, e);
            return null;
        }
    }
}