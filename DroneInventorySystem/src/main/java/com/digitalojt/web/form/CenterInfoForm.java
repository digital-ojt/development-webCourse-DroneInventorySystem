package com.digitalojt.web.form;

import com.digitalojt.web.consts.ModelAttributeContents;
import com.digitalojt.web.validation.CenterInfoFormValidator;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 在庫センター情報画面のフォームクラス
 * 
 * @author dotlife
 *
 */
@Data
@CenterInfoFormValidator
public class CenterInfoForm {

	/**センター名*/
    @Size(max = ModelAttributeContents.MAX_CENTER_NAME_LENGTH, message = "{centerName.length.wrongInput}")
	private String centerName;

	/**都道府県*/
	private String region;

	/**
	 * 容量(From)
	 */
	private Integer storageCapacityFrom;

	/**
	 * 容量(To)
	 */
	private Integer storageCapacityTo;

	/**
	 * 容量(From)のデフォルト値を設定
	 * @return
	 */
	@PostConstruct
	public Integer init() {

		if (storageCapacityTo != null && storageCapacityFrom == null) {
			return ModelAttributeContents.INITIAL_CAPACITY_FROM;
		}
		return storageCapacityFrom;
	}
}
