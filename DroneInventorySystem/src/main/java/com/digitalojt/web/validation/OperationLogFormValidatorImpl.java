package com.digitalojt.web.validation;

import com.digitalojt.web.consts.ErrorMessage;
import com.digitalojt.web.consts.InvalidCharacter;
import com.digitalojt.web.consts.OperationStatus;
import com.digitalojt.web.consts.OperationType;
import com.digitalojt.web.exception.ErrorMessageHelper;
import com.digitalojt.web.form.OperationLogForm;
import com.digitalojt.web.util.InputValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/** 操作履歴画面のバリデーション処理実装 OperationLogForm のフィールドに対してバリデーションを行うクラスです。 */
public class OperationLogFormValidatorImpl implements ConstraintValidator<OperationLogFormValidator, OperationLogForm> {

	/**
	 * フォームデータのバリデーション処理を行う
	 *
	 * @param form バリデーション対象のフォームデータ
	 * @param context バリデーションコンテキスト
	 * @return フォームが有効かどうか（有効ならtrue、無効ならfalse）
	 */
	@Override
	public boolean isValid(OperationLogForm form, ConstraintValidatorContext context) {
		// フィールドがすべて空である場合にエラー処理
		if (isAllFieldsEmpty(form)) {
			context.disableDefaultConstraintViolation();
			context
				.buildConstraintViolationWithTemplate(
					ErrorMessageHelper.getMessage(ErrorMessage.ALL_FIELDS_EMPTY_ERROR_MESSAGE)
				)
				.addConstraintViolation();
			return false;
		}

		// 管理者名が不正文字に含まれる場合にエラー処理
		if (form.getUserId() != null && isValidCenterName(form.getUserId())) {
			context.disableDefaultConstraintViolation();
			context
				.buildConstraintViolationWithTemplate(
					ErrorMessageHelper.getMessage(ErrorMessage.INVALID_INPUT_ERROR_MESSAGE)
				)
				.addConstraintViolation();
			return false;
		}

		// 操作が不正文字に含まれる場合にエラー処理
		if (form.getOperateTypeStr() != null && isValidOperateType(form.getOperateTypeStr())) {
			context.disableDefaultConstraintViolation();
			context
				.buildConstraintViolationWithTemplate(
					ErrorMessageHelper.getMessage(ErrorMessage.INVALID_INPUT_ERROR_MESSAGE)
				)
				.addConstraintViolation();
			return false;
		}

		// ステータスが不正文字に含まれる場合にエラー処理
		if (form.getStatusStr() != null && isValidStatus(form.getStatusStr())) {
			context.disableDefaultConstraintViolation();
			context
				.buildConstraintViolationWithTemplate(
					ErrorMessageHelper.getMessage(ErrorMessage.INVALID_INPUT_ERROR_MESSAGE)
				)
				.addConstraintViolation();
			return false;
		}

		// 操作時刻（開始・終了）のどちらかのみが入力されている場合はエラー処理
		if (
			(form.getCreateDate() == null && form.getUpdateDate() != null) ||
			(form.getCreateDate() != null && form.getUpdateDate() == null)
		) {
			context.disableDefaultConstraintViolation();
			context
				.buildConstraintViolationWithTemplate(
					ErrorMessageHelper.getMessage(ErrorMessage.OPERATION_DATE_FIELD_ERROR_MESSAGE)
				)
				.addConstraintViolation();
			return false;
		}

		// バリデーションが成功した場合はtrueを返す
		return true;
	}

	/**
	 * 文字列の不正文字チェックを実施する
	 *
	 * @param input
	 * @return
	 */
	private boolean isValidCenterName(String input) {
		if (input == null) {
			return false; // `null` の場合は不正文字チェック不要
		}

		// 文字列の各文字を1つずつチェック
		for (char c : input.toCharArray()) {
			// 不正文字が含まれているか確認
			if (isInvalidCharacter(c)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 文字が不正文字かをチェックするメソッド
	 *
	 * @param character チェックする文字
	 * @return 不正文字なら true, それ以外は false
	 */
	private static boolean isInvalidCharacter(char character) {
		for (InvalidCharacter invalidChar : InvalidCharacter.values()) {
			if (invalidChar.getCharacter() == character) {
				// 不正文字が見つかった
				return true;
			}
		}
		// 不正文字ではない
		return false;
	}

	/**
	 * 操作の種類が有効かどうかを確認
	 *
	 * @param operateType 操作の種類
	 * @return 操作の種類が有効かどうか
	 */
	private boolean isValidOperateType(String operateType) {
		if (!InputValidator.isValid(operateType)) {
			return false;
		}
		for (OperationType type : OperationType.values()) {
			if (type.getTypeName().equals(operateType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 操作ステータスが有効かどうかを確認
	 *
	 * @param operationStatus 操作ステータス
	 * @return 操作ステータスが有効かどうか
	 */
	private boolean isValidStatus(String operationStatus) {
		if (!InputValidator.isValid(operationStatus)) {
			return false;
		}
		for (OperationStatus status : OperationStatus.values()) {
			if (status.getStatusName().equals(operationStatus)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * フォームの全てのフィールドが空かどうかを確認
	 *
	 * @param form フォームデータ
	 * @return すべてのフィールドがnullまたは空の場合はtrue、それ以外はfalse
	 */
	private boolean isAllFieldsEmpty(OperationLogForm form) {
		return (
			(form.getUserId() == null || form.getUserId().trim().isEmpty()) &&
			(form.getOperateTypeStr() == null || form.getOperateTypeStr().trim().isEmpty()) &&
			(form.getStatusStr() == null || form.getStatusStr().trim().isEmpty()) &&
			form.getCreateDate() == null &&
			form.getUpdateDate() == null
		);
	}
}
