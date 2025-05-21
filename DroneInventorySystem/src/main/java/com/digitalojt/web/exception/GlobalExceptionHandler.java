package com.digitalojt.web.exception;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.digitalojt.web.consts.LogMessage;
import com.digitalojt.web.consts.UrlConsts;

import jakarta.servlet.http.HttpServletRequest;

/**
 * グローバル例外ハンドラー
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 重複登録例外のハンドリング
	 */
	@ExceptionHandler(DuplicateEntryException.class)
	public String handleDuplicateEntryException(DuplicateEntryException ex, RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		return handleException(ex, redirectAttributes, request);
	}

	/**
	 * 共通のエラーハンドリング
	 */
	private String handleException(Exception ex, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		String errorMessage = ex.getMessage();
		redirectAttributes.addFlashAttribute(LogMessage.FLASH_ATTRIBUTE_ERROR, errorMessage);

		// リファラーの取得
		String referer = request.getHeader("Referer");
		referer = referer != null ? referer.replaceAll("^(https?://[^/]+)", "") : "";

		return "redirect:" + referer;
	}
	

	/**
	 * 入力値不正例外のハンドリング
	 */
	@ExceptionHandler(InvalidInputException.class)
	public String handleDatabaseException(InvalidInputException ex, RedirectAttributes redirectAttributes) {
		String errorMessage = ex.getMessage();
		redirectAttributes.addFlashAttribute(LogMessage.FLASH_ATTRIBUTE_ERROR, errorMessage);
		return "redirect:" + UrlConsts.ERROR;
	}

	/**
	 * DBエラーのハンドリング
	 * @return ErrorController class
	 */
	@ExceptionHandler(DataAccessException.class)
	public String handleDatabaseException(DataAccessException ex, RedirectAttributes redirectAttributes) {
		String errorMessage = ex.getMessage();
		redirectAttributes.addFlashAttribute(LogMessage.FLASH_ATTRIBUTE_ERROR, errorMessage);
		return "redirect:" + UrlConsts.ERROR;
	}

	/**
	 * SQLエラーのハンドリング
	 */
	@ExceptionHandler(SQLException.class)
	public String handleSQLException(SQLException ex, RedirectAttributes redirectAttributes) {
		String errorMessage = ex.getMessage();
		redirectAttributes.addFlashAttribute(LogMessage.FLASH_ATTRIBUTE_ERROR, errorMessage);
		return "redirect:" + UrlConsts.ERROR;
	}

	/**
	 * 予期せぬシステムエラーのハンドリング
	 */
	@ExceptionHandler(Exception.class)
	public String handleGeneralException(Exception ex, RedirectAttributes redirectAttributes) {
		String errorMessage = ex.getMessage();
		redirectAttributes.addFlashAttribute(LogMessage.FLASH_ATTRIBUTE_ERROR, errorMessage);
		return "redirect:" + UrlConsts.ERROR;
	}
}
