package com.digitalojt.web.form;

import com.digitalojt.web.validation.OperationLogFormValidator;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 操作履歴画面のフォームクラス
 *
 * @author dotlife
 */
@Data
@OperationLogFormValidator
public class OperationLogForm {

  /** 管理者名 */
  private String userId;

  /** 操作の種類 (1:作成, 2:更新, 3:削除) */
  private String operateTypeStr;

  /** 操作のステータス (0:失敗, 1:成功) */
  private String statusStr;

  /** 操作時刻（開始） */
  private LocalDateTime createDate;

  /** 操作時刻（終了） */
  private LocalDateTime updateDate;
}
