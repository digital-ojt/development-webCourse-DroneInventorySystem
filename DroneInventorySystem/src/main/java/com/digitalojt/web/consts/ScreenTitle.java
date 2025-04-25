package com.digitalojt.web.consts;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 画面名Enum
 * 
 * @author dotlife
 *
 */
public enum ScreenTitle {

	LOGIN("login.title", "ログイン画面"), 
	STOCK_LIST("stockList.title", "在庫一覧画面"), 
	CATEGORY_INFO("categoryInfo.title", "部品カテゴリー情報管理画面"), 
	CENTER_INFO("centerInfo.title", "在庫センター情報画面"), 
	OPERATION_LOG("operationLog.title", "操作履歴画面");

	private final String tableKey; // テーブルキー
	private final String screenName; // 画面名

	ScreenTitle(String tableKey, String screenName) {
		this.tableKey = tableKey;
		this.screenName = screenName;
	}

	public String getTableKey() {
		return tableKey;
	}

	public String getScreenName() {
		return screenName;
	}

	/**
	 * Enumからテーブルキーの名前を取得
	 * 
	 * @param tableKey
	 * @return
	 */
	public static String fromTableKey(String tableKey) {
		for (ScreenTitle title : values()) {
			if (title.getTableKey().equals(tableKey)) {
				return title.getScreenName();
			}
		}
		return "不明な画面"; // 定義されていない場合のデフォルト値
	}

	/**
	 * 画面名の一覧を取得
	 * 
	 * @return
	 */
	public static List<String> getScreenNames() {
		return Arrays.stream(ScreenTitle.values())
				.map(ScreenTitle::getScreenName)
				.collect(Collectors.toList());
	}
}
