package com.digitalojt.web.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 管理者情報Entity
 *
 * @author dotlife
 */
@Data
@Entity
@Table(name = "admin_info")
public class AdminInfo {

  /** 管理者ID */
  @Id
  @Column(name = "admin_id", length = 100, nullable = false)
  private String adminId;

  /** 管理者名 */
  @Column(name = "admin_name", length = 100, nullable = false)
  private String adminName;

  /** メールアドレス */
  @Column(name = "mail", length = 320, nullable = false)
  private String mail;

  /** 電話番号 */
  @Column(name = "phone_number", length = 20, nullable = false)
  private String phoneNumber;

  /** パスワード */
  @Column(name = "password", length = 255, nullable = false)
  private String password;

  /** 削除フラグ (0:未削除, 1:削除済) */
  @Column(name = "delete_flag", nullable = false)
  private Integer deleteFlag;

  /** 作成日付 */
  @Column(name = "create_date", nullable = false)
  private LocalDateTime createDate;

  /** 更新日付 */
  @Column(name = "update_date", nullable = false)
  private LocalDateTime updateDate;
}
