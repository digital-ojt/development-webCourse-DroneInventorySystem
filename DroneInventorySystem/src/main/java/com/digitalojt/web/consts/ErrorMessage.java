package com.digitalojt.web.consts;

/**
 * エラーメッセージ定数クラス
 * 
 * @author dotlife
 *
 */
public class ErrorMessage {
	
	// ログイン情報の入力に誤りがあった場合に、出力するエラーメッセージのID
	public static final String  LOGIN_WRONG_INPUT = "login.wrongInput";

	// データが空の場合のエラーメッセージ
	public static final String DATA_EMPTY_ERROR_MESSAGE = "data.empty";
	
	// すべての項目が空の場合のエラーメッセージ
	public static final String ALL_FIELDS_EMPTY_ERROR_MESSAGE = "allField.empty";

	// 空文字検索に関するエラーメッセージ
	public static final String UNEXPECTED_INPUT_ERROR_MESSAGE = "unexpected.input";

	// 不正な文字列を使用した検索に関するエラーメッセージ
	public static final String INVALID_INPUT_ERROR_MESSAGE = "invalid.input";

	// 文字超過に関するエラーメッセージ
	public static final String CENTER_NAME_LENGTH_ERROR_MESSAGE = "centerName.length.wrongInput";
	
	// 空欄の場合のエラーメッセージキー
	public static final String CATEGORY_NAME_REQUIRED = "category.name.required";
	
	// 禁止文字チェック（{ } ; = $ & ）が含まれている場合のエラーメッセージキー
	public static final String CATEGORY_NAME_FORBIDDEN = "category.name.forbidden";
	
	// 文字数が制限を超えた場合のエラーメッセージキー
	public static final String CATEGORY_NAME_INVALID_LENGTH = "category.name.length";
	
	// 操作履歴画面の操作時刻に関するエラーメッセージ
	public static final String OPERATION_DATE_FIELD_ERROR_MESSAGE = "operationLog.operationDateField.empty";
}
