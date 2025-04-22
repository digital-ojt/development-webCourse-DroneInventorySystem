package com.digitalojt.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.digitalojt.web.consts.ErrorMessage;
import com.digitalojt.web.consts.InvalidCharacter;
import com.digitalojt.web.exception.ErrorMessageHelper;
import com.digitalojt.web.form.PartsCategoryForm;

public class PartsCategoryFormValidatorImpl implements ConstraintValidator<PartsCategoryFormValidator, PartsCategoryForm> {

    /**
     * フォームデータのバリデーション処理を行う
     * @param form バリデーション対象のフォームデータ
     * @param context バリデーションコンテキスト
     * @return フォームが有効かどうか（有効ならtrue、無効ならfalse）
     */
    @Override
    public boolean isValid(PartsCategoryForm form, ConstraintValidatorContext context) {
   
    	// 部品カテゴリー名が不正文字に含まれる場合にエラー処理
        if (form.getCategoryName() != null && isValidCategoryName(form.getCategoryName())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
            		ErrorMessageHelper.getMessage(ErrorMessage.ALL_FIELDS_EMPTY_ERROR_MESSAGE))
                   .addConstraintViolation();
            return false;
        }
        
        // バリデーションが成功した場合はtrueを返す
        return true;

    }
    
    private boolean isValidCategoryName(String input) {
    	
    	if(input == null) {
    		
    		return false;

    	}
    	
    	
    	//文字列の各文字を1つずつチェック
    	for(char c : input.toCharArray()) {
    		// 不正文字が含まれているか確認
    		if(isInvalidCharacter(c)) {

    			return true;
    			
    		}
    	}
    	
    	return false;

    }
    
    
    private static boolean isInvalidCharacter(char character) {
    	
    	for(InvalidCharacter invalidChar : InvalidCharacter.values()) {
    		if(invalidChar.getCharacter() == character) {
    			//不正文字が見つかった
    			return true;
    		}
    	}
    	
    	//不正文字ではない
    	return false;
    }
        
}
