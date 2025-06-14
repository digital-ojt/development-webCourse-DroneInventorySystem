package com.digitalojt.web.consts;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作ステータスEnum
 *
 * @author dotlife
 */
public enum OperationStatus {
  SUCCESS(1, "成功"),
  FAILURE(0, "失敗");

  private final Integer statusCode; // ステータスコード
  private final String statusName; // ステータスの表示文言

  OperationStatus(Integer statusCode, String statusName) {
    this.statusCode = statusCode;
    this.statusName = statusName;
  }

  public Integer getStatusCode() {
    return statusCode;
  }

  public String getStatusName() {
    return statusName;
  }

  /**
   * ステータスコードから対応するステータス名を取得
   *
   * @param statusCode 操作ステータスのコード
   * @return 操作ステータス名 (該当しない場合は "不明なステータス" を返す)
   */
  public static String fromStatusCode(Integer statusCode) {
    for (OperationStatus status : values()) {
      if (status.getStatusCode().equals(statusCode)) {
        return status.getStatusName();
      }
    }
    return "不明なステータス"; // デフォルト値
  }

  // 操作ステータスの一覧を取得
  public static List<String> getStatusNames() {
    return Arrays.stream(OperationStatus.values())
        .map(OperationStatus::getStatusName)
        .collect(Collectors.toList());
  }
}
