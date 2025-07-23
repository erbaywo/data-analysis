package org.yc.analysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yc.analysis.model.DouyinUserInfo;

import java.util.Optional;

@Repository
public interface DouyinUserInfoRepository extends JpaRepository<DouyinUserInfo, Long> {

    /**
     * 根据 openId 查找用户
     */
    Optional<DouyinUserInfo> findByOpenId(String openId);

    /**
     * 根据 unionId 查找用户
     */
    Optional<DouyinUserInfo> findByUnionId(String unionId);

    /**
     * 检查 openId 是否存在
     */
    boolean existsByOpenId(String openId);
}
