package com.digitalojt.web.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.digitalojt.web.consts.LogMessage;
import com.digitalojt.web.consts.ModelAttributeContents;
import com.digitalojt.web.consts.UrlConsts;

import lombok.RequiredArgsConstructor;

/**
 * ログイン画面のコントローラークラス
 * 
 * @author dotlife
 *
 */
@Controller
@RequiredArgsConstructor
public class LoginController extends AbstractController {
	
	/** セッション情報 */
	private final HttpSession session;
	
	/**
	 * 初期表示
	 * 
	 * @param model
	 * @return 
	 */
	@GetMapping(UrlConsts.LOGIN)
	public String index() {
        logStart(LogMessage.HTTP_GET);
        logEnd(LogMessage.HTTP_GET);

		return UrlConsts.LOGIN_INDEX;
	}
	
	/**
	 * ログインエラー画面表示
	 * 
	 * @param model
	 * @return 
	 */
	@GetMapping(value = UrlConsts.LOGIN, params = "error")
	public String error(Model model) {
		logStart(LogMessage.HTTP_GET);

		Exception errorInfo = (Exception) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		logException(LogMessage.HTTP_GET, errorInfo.getMessage());
		model.addAttribute(ModelAttributeContents.ERROR_MSG, errorInfo.getMessage());
		
		logEnd(LogMessage.HTTP_GET);
		return UrlConsts.LOGIN_INDEX;
	}
}
