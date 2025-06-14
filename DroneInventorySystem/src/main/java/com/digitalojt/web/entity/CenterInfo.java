package com.digitalojt.web.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * センター情報Entity
 *
 * @author dotlife
 */
@Data
@Entity
@Table(name = "center_info")
public class CenterInfo {

  /** センターID */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "center_id", nullable = false)
  private int centerId;

  /** センター名 */
  @Column(name = "center_name", length = 20, nullable = false)
  private String centerName;

  /** 郵便番号 */
  @Column(name = "post_code", length = 10, nullable = false)
  private String postCode;

  /** 住所 */
  @Column(name = "address", length = 255, nullable = false)
  private String address;

  /** 電話番号 */
  @Column(name = "phone_number", length = 100, nullable = false)
  private String phoneNumber;

  /** 管理者名 */
  @Column(name = "manager_name", length = 100, nullable = false)
  private String managerName;

  /** 稼働状況ステータス (0:稼働中, 1:稼働停止) */
  @Column(name = "operational_status", columnDefinition = "TINYINT(1)", nullable = false)
  private Integer operationalStatus;

  /** 最大容量 */
  @Column(name = "max_storage_capacity", nullable = false)
  private String maxStorageCapacity;

  /** 現在容量 */
  @Column(name = "current_storage_capacity", nullable = false)
  private String currentStorageCapacity;

  /** 備考 */
  @Column(name = "notes", length = 255)
  private String notes;

  /** 論理削除フラグ (0:未削除, 1:削除済) */
  @Column(name = "delete_flag", nullable = false)
  private Integer deleteFlag;

  /** 作成日付 */
  @Column(name = "create_date", nullable = false)
  private LocalDateTime createDate;

  /** 更新日付 */
  @Column(name = "update_date", nullable = false)
  private LocalDateTime updateDate;
}
