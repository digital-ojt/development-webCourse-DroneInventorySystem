package com.digitalojt.web.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;

import com.digitalojt.web.consts.ErrorMessage;

@Documented
@Constraint(validatedBy = PartsCategoryFormValidatorImpl.class)  // ★実装クラス指定してる？
@Target({ ElementType.TYPE })  // ★クラスに対して使うなら TYPE
@Retention(RetentionPolicy.RUNTIME)
public @interface PartsCategoryFormValidator {
	/**
	 * 部品カテゴリー管理画面のバリデーションチェック インターフェース
	 * 
	 * @author dotlife
	 */
	
    String message() default ErrorMessage.ALL_FIELDS_EMPTY_ERROR_MESSAGE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
	
	@Constraint(validatedBy = PartsCategoryFormValidatorImpl.class)
	@Target({ ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	@ReportAsSingleViolation
	public @interface CenterInfoFormValidator {

		String message() default ErrorMessage.ALL_FIELDS_EMPTY_ERROR_MESSAGE;
	    Class<?>[] groups() default {};
	    Class<? extends Payload>[] payload() default {};
	}
}