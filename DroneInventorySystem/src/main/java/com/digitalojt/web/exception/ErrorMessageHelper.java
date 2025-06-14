package com.digitalojt.web.exception;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * エラーメッセージ取得の共通処理クラス
 *
 * @author dotlife
 */
public class ErrorMessageHelper {

  private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages");

  public static String getMessage(String messageCode) {
    try {
      return MESSAGES.getString(messageCode);
    } catch (MissingResourceException e) {
      return "Unknown error: " + messageCode; // デフォルトメッセージ
    }
  }
}
