package com.digitalojt.web.exception;

/**
 * 入力値不正例外
 * 
 * @author dotlife
 *
 */
public class InvalidInputException extends RuntimeException {

    public InvalidInputException(String messageCode) {
        super(ErrorMessageHelper.getMessage(messageCode));
    }
}
