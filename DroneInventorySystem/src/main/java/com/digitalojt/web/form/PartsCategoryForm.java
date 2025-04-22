package com.digitalojt.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.digitalojt.web.consts.ErrorMessage;

import lombok.Data;

/*
	部品カテゴリー管理画面のフォームクラス
*/

@Data
//@PartsCategoryFormValidator
public class PartsCategoryForm {
	/*		
		部品カテゴリーID	
	*/
	private Integer categoryId;
	
	// 部品カテゴリー名
	@NotBlank(message = ErrorMessage.CATEGORY_NAME_REQUIRED)
	@Size(max = 20, message = ErrorMessage.CATEGORY_NAME_INVALID_LENGTH)
	@Pattern(regexp = "^[^{}()'*;$&=]*$", message = ErrorMessage.INVALID_INPUT_ERROR_MESSAGE)	
	private String categoryName;

	//削除フラグ
	private Boolean deleteFlag;
}
