package com.digitalojt.web.form;

import com.digitalojt.web.validation.StockInfoFormValidator;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 在庫情報管理画面のフォームクラス
 *
 * @author lin
 * 
 * @NotBlank 必須入力（空欄・スペースの禁止）
 * @Size 文字数制限（最大255文字）
 * @DecimalMin 数字最小値制限（最小0）
 * @DecimalMax 数字最大値制限（最大10000）
 * 
 */
@Getter
@Setter
@StockInfoFormValidator
public class StockInfoForm {
	
    // 登録時の必須チェックグループ
    public interface RegisterValidation {}
	
	/**
	 * 分類ID
	 */
    private Integer categoryId;
    
	/**
	 * 名前
	 */
    @NotBlank(message = "{stock.name.required}", groups = RegisterValidation.class)
    @Size(max = 255, message = "{stock.name.invalid.length}")
	private String name;
	
	/**
	 * 数量
	 */
    @DecimalMin(value = "0", message = "{stock.amount.invalid.length}")
    @DecimalMax(value = "10000", message = "{stock.amount.invalid.length}")
    @NotNull(message = "{stock.amount.required}", groups = RegisterValidation.class)
	private Integer amount;
	
	/**
	 * 数量条件
	 */
	private String amountCondition;
    
	/**
	 * 在庫ID
	 */
	private Integer stockId;
	
	/**
	 * 在庫センターID
	 */
    private Integer centerId;
    
	/**
	 * 説明
	 */
    @Size(max = 255, message = "{stock.description.invalid.length}")
	private String description;
    
	/**
	 * 削除フラグ
	 */
    private Boolean deleteFlag;
}