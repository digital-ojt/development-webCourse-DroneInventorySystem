package com.digitalojt.web.form;

import com.digitalojt.web.validation.PartsCategoryFormValidator;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 部品カテゴリー管理画面のフォームクラス
 *
 * @author dotlife
 * 
 * @NotBlank 必須入力（空欄・スペースの禁止）
 * @Size 文字数制限（最大20文字）
 * 
 */
@Getter
@Setter
@PartsCategoryFormValidator
public class PartsCategoryForm {

	/**
	 * 部品カテゴリーID
	 */
	private Integer categoryId;
	
	// 部品カテゴリー名
	@NotBlank(message = "{category.name.required}")
	@Size(max = 20, message = "{category.name.invalid.length}")
	private String categoryName;
    
    // 削除フラグ
    private Boolean deleteFlag;
}