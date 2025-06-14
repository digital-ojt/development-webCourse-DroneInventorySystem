package com.digitalojt.web.repository;

import com.digitalojt.web.entity.OperationLog;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 操作履歴テーブルリポジトリー
 *
 * @author dotlife
 */
@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Integer> {

  /**
   * 検索条件：管理者名・操作・ステータス・操作時刻に基づき、操作履歴情報を取得（昇順）
   *
   * @param userId 管理者名（部分一致）
   * @param operateType 操作の種類
   * @param status 操作ステータス
   * @param createDate 操作時刻 開始日
   * @param updateDate 操作時刻 終了日
   * @return 条件に一致する操作履歴情報のリスト
   */
  @Query(
      "SELECT o FROM OperationLog o JOIN FETCH o.adminInfo "
          + "WHERE (:userId IS NULL OR o.adminInfo.adminName LIKE %:userId%) "
          + "AND (:operateType IS NULL OR o.operateType = :operateType) "
          + "AND (:status IS NULL OR o.status = :status) "
          + "AND ((:createDate IS NULL OR :updateDate IS NULL) OR o.createDate BETWEEN :createDate AND :updateDate)"
          + "ORDER BY o.createDate ASC")
  List<OperationLog> findOperationLogs(
      @Param("userId") String userId,
      @Param("operateType") Integer operateType,
      @Param("status") Integer status,
      @Param("createDate") LocalDateTime createDate,
      @Param("updateDate") LocalDateTime updateDate);

  /**
   * 初期表示用：直近1ヶ月分の操作履歴情報を取得（降順）
   *
   * @param createDate 1ヶ月前の日付（開始）
   * @param updateDate 現在日時（終了）
   * @return 操作履歴情報リスト
   */
  @Query(
      "SELECT o FROM OperationLog o JOIN FETCH o.adminInfo a "
          + "WHERE o.createDate BETWEEN :createDate AND :updateDate "
          + "ORDER BY o.createDate DESC")
  List<OperationLog> findOperationLogsForOneMonth(
      @Param("createDate") LocalDateTime createDate, @Param("updateDate") LocalDateTime updateDate);
}
