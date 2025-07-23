

-- 创建抖音用户信息表
CREATE TABLE IF NOT EXISTS douyin_user_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    open_id VARCHAR(100) NOT NULL UNIQUE COMMENT '抖音用户唯一标识',
    union_id VARCHAR(100) COMMENT '抖音开放平台统一用户标识',
    nickname VARCHAR(100) COMMENT '用户昵称',
    avatar VARCHAR(500) COMMENT '用户头像URL',
    gender TINYINT COMMENT '性别：0-未知，1-男，2-女',
    country VARCHAR(50) COMMENT '国家',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    access_token VARCHAR(500) COMMENT '访问令牌',
    refresh_token VARCHAR(500) COMMENT '刷新令牌',
    expires_in BIGINT COMMENT '访问令牌过期时间（秒）',
    token_expire_time DATETIME COMMENT '令牌过期时间',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_open_id (open_id),
    INDEX idx_union_id (union_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='抖音用户信息表';

-- 创建飞书用户信息表
CREATE TABLE IF NOT EXISTS feishu_user_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    open_id VARCHAR(100) NOT NULL UNIQUE COMMENT '飞书用户唯一标识',
    union_id VARCHAR(100) COMMENT '飞书开放平台统一用户标识',
    user_id VARCHAR(100) COMMENT '飞书用户ID',
    name VARCHAR(100) COMMENT '用户姓名',
    en_name VARCHAR(100) COMMENT '用户英文名',
    nickname VARCHAR(100) COMMENT '用户昵称',
    email VARCHAR(200) COMMENT '用户邮箱',
    mobile VARCHAR(50) COMMENT '用户手机号',
    avatar_url VARCHAR(500) COMMENT '用户头像URL',
    gender TINYINT COMMENT '性别：0-未知，1-男，2-女',
    department_ids VARCHAR(500) COMMENT '部门ID列表，逗号分隔',
    employee_no VARCHAR(100) COMMENT '员工工号',
    employee_type TINYINT COMMENT '员工类型',
    access_token VARCHAR(500) COMMENT '访问令牌',
    refresh_token VARCHAR(500) COMMENT '刷新令牌',
    expires_in BIGINT COMMENT '访问令牌过期时间（秒）',
    token_expire_time DATETIME COMMENT '令牌过期时间',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_open_id (open_id),
    INDEX idx_union_id (union_id),
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='飞书用户信息表';

-- 创建员工表
CREATE TABLE IF NOT EXISTS employee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '员工姓名',
    douyin_account VARCHAR(100) COMMENT '抖音账号',
    douyin_open_id VARCHAR(100) COMMENT '抖音用户openId',
    authorization_status TINYINT DEFAULT 0 COMMENT '授权状态：0-未授权，1-已授权，2-授权过期',
    followers_count BIGINT DEFAULT 0 COMMENT '粉丝数',
    likes_count BIGINT DEFAULT 0 COMMENT '获赞数',
    comments_count BIGINT DEFAULT 0 COMMENT '评论数',
    shares_count BIGINT DEFAULT 0 COMMENT '分享数',
    profile_views_count BIGINT DEFAULT 0 COMMENT '主页浏览量',
    videos_count INT DEFAULT 0 COMMENT '视频数量',
    last_sync_time DATETIME COMMENT '最后同步时间',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_douyin_account (douyin_account),
    INDEX idx_douyin_open_id (douyin_open_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工表';

-- 创建视频统计表
CREATE TABLE IF NOT EXISTS video_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    stats_date DATE NOT NULL COMMENT '统计日期',
    daily_published_count INT DEFAULT 0 COMMENT '当日发布视频数',
    daily_new_views BIGINT DEFAULT 0 COMMENT '当日新增观看量',
    total_published_count INT DEFAULT 0 COMMENT '累计发布视频数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_employee_date (employee_id, stats_date),
    INDEX idx_stats_date (stats_date),
    UNIQUE KEY uk_employee_date (employee_id, stats_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='视频统计表';

-- 创建同步记录表
CREATE TABLE IF NOT EXISTS sync_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    sync_type VARCHAR(50) NOT NULL COMMENT '同步类型：MANUAL-手动，AUTO-自动',
    sync_status TINYINT NOT NULL COMMENT '同步状态：0-进行中，1-成功，2-失败',
    employee_count INT DEFAULT 0 COMMENT '员工总数',
    success_count INT DEFAULT 0 COMMENT '成功数量',
    failed_count INT DEFAULT 0 COMMENT '失败数量',
    error_message TEXT COMMENT '错误信息',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_sync_type (sync_type),
    INDEX idx_sync_status (sync_status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='同步记录表';
