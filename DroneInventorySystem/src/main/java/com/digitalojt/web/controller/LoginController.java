package com.digitalojt.web.controller;

import com.digitalojt.web.consts.LogMessage;
import com.digitalojt.web.consts.ModelAttributeContents;
import com.digitalojt.web.consts.UrlConsts;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ログイン画面のコントローラークラス
 *
 * @author dotlife
 */
@Controller
@RequiredArgsConstructor
public class LoginController extends AbstractController {

	/**
	 * 初期表示
	 *
	 * @param model モデル
	 * @param session HTTPセッション
	 * @return ビュー名
	 */
	@GetMapping(UrlConsts.LOGIN)
	public String view(Model model, HttpSession session) {
		logStart(LogMessage.HTTP_GET);
		logEnd(LogMessage.HTTP_GET);

		return UrlConsts.LOGIN_INDEX;
	}

	/**
	 * ログインエラー画面表示
	 *
	 * @param model モデル
	 * @param session HTTPセッション
	 * @return ビュー名
	 */
	@GetMapping(value = UrlConsts.LOGIN, params = "error")
	public String error(Model model, HttpSession session) {
		logStart(LogMessage.HTTP_GET);

		Exception errorInfo = (Exception) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		logException(LogMessage.HTTP_GET, errorInfo.getMessage());
		model.addAttribute(ModelAttributeContents.ERROR_MSG, errorInfo.getMessage());

		logEnd(LogMessage.HTTP_GET);
		return UrlConsts.LOGIN_INDEX;
	}
}
