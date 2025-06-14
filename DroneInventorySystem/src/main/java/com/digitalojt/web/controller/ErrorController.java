package com.digitalojt.web.controller;

import com.digitalojt.web.consts.LogMessage;
import com.digitalojt.web.consts.UrlConsts;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * エラーコントローラー
 *
 * @author dotlife
 */
@Controller
public class ErrorController extends AbstractController {

  /**
   * エラーコントローラー
   *
   * @param エラーメッセージ
   * @return エラーページ（error.html）
   */
  @GetMapping(UrlConsts.ERROR)
  public String handleError(@ModelAttribute("errorMsg") String errorMsg) {

    logException(LogMessage.HTTP_GET, errorMsg != null ? errorMsg : "不明なエラー");

    return UrlConsts.ERROR_VIEW;
  }
}
