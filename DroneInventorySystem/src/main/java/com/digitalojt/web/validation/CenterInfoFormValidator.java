package com.digitalojt.web.validation;

import com.digitalojt.web.consts.ErrorMessage;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 部品カテゴリー管理画面のバリデーションチェック インターフェース
 *
 * @author dotlife
 */
@Constraint(validatedBy = CenterInfoFormValidatorImpl.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@ReportAsSingleViolation
public @interface CenterInfoFormValidator {
	String message() default ErrorMessage.ALL_FIELDS_EMPTY_ERROR_MESSAGE;

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
