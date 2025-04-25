package com.digitalojt.web.util;

import java.util.Locale;

import org.springframework.context.MessageSource;

/**
 * メッセージ管理 共通クラス
 * 
 * @author dotlife
 */
public class MessageManager {

	/**
	 * メッセージIDからメッセージを取得
	 * 
	 * @param messageSource
	 * @param key
	 * @param params
	 * @return 
	 */
	public static String getMessage(MessageSource messageSource, String key, Object... params) {
		return messageSource.getMessage(key,  params, Locale.JAPAN);
	}
}
