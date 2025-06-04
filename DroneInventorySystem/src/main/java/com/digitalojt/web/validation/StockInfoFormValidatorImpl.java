package com.digitalojt.web.validation;

import com.digitalojt.web.consts.ErrorMessage;
import com.digitalojt.web.consts.InvalidCharacter;
import com.digitalojt.web.exception.ErrorMessageHelper;
import com.digitalojt.web.form.StockInfoForm;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 部品在庫一覧画面のバリデーション処理実装
 * StockInfoForm のフィールドに対してバリデーションを行うクラスです。
 * 
 * @author lin
 * 
 */
public class StockInfoFormValidatorImpl implements ConstraintValidator<StockInfoFormValidator, StockInfoForm> {

	/**
	 * フォームデータのバリデーション処理を行う
	 * @param form バリデーション対象のフォームデータ
	 * @param context バリデーションコンテキスト
	 * @return フォームが有効かどうか（有効ならtrue、無効ならfalse）
	 */
	@Override
	public boolean isValid(StockInfoForm form, ConstraintValidatorContext context) {

		// 名称が不正文字に含まれる場合にエラー処理
		if (form.getName() != null && isValidName(form.getName())) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(ErrorMessageHelper.getMessage(
					ErrorMessage.STOCK_NAME_FORBIDDEN_ERROR_MESSAGE))
			.addPropertyNode("name")
			.addConstraintViolation();
			return false;
		}
		
		// 個数が0未満10000以上の場合にエラー処理
		if (form.getAmount() != null && isValidAmount(form.getAmount(), 0, 10000)) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(ErrorMessageHelper.getMessage(
					ErrorMessage.STOCK_AMOUNT_RANGE_ERROR_MESSAGE))
			.addPropertyNode("amount")
			.addConstraintViolation();
			return false;
		}
		
		// 説明が不正文字に含まれる場合にエラー処理
		if (form.getDescription() != null && isValidName(form.getDescription())) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(ErrorMessageHelper.getMessage(
					ErrorMessage.STOCK_NAME_FORBIDDEN_ERROR_MESSAGE))
			.addPropertyNode("description")
			.addConstraintViolation();
			return false;
		}
		
		// バリデーションが成功した場合はtrueを返す
		return true;
	}

	/**
	 * 文字列の不正文字チェックを実施する
	 * @param input
	 * @return
	 */
	private boolean isValidName(String input) {
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
	 * 数値の範囲チェックを実施する
	 * @param input
	 * @param min
	 * @param max
	 * @return
	 */
	private boolean isValidAmount(int input, int min, int max) {
		if (input > max || input < min) {
			// 数値の範囲エラー
			return true;
		}

		return false;
	}
}