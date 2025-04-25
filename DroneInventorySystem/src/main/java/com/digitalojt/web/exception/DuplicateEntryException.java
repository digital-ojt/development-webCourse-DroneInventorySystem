package com.digitalojt.web.exception;

/**
 * 重複登録例外
 * 
 * @author dotlife
 *
 */
public class DuplicateEntryException extends RuntimeException {

    public DuplicateEntryException(String messageCode) {
        super(ErrorMessageHelper.getMessage(messageCode));
    }
}
