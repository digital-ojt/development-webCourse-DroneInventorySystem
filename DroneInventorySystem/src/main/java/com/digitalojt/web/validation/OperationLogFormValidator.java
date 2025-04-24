package com.digitalojt.web.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;

import com.digitalojt.web.consts.ErrorMessage;

/**
 * 操作履歴画面のバリデーションチェック インターフェース
 * 
 * @author dotlife
 */
@Constraint(validatedBy = OperationLogFormValidatorImpl.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@ReportAsSingleViolation
public @interface OperationLogFormValidator {
	String message() default ErrorMessage.ALL_FIELDS_EMPTY_ERROR_MESSAGE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
