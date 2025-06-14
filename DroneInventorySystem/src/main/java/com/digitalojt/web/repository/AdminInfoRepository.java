package com.digitalojt.web.repository;

import com.digitalojt.web.entity.AdminInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 管理者情報テーブルリポジトリー
 *
 * @author dotlife
 */
@Repository
public interface AdminInfoRepository extends JpaRepository<AdminInfo, String> {}
