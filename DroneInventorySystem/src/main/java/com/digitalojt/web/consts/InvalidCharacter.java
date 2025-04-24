package com.digitalojt.web.consts;

/**
 * 不正文字を管理するEnumクラス
 * 
 * @author dotlife
 */
public enum InvalidCharacter {

    CURLY_BRACE_OPEN('{'),
    CURLY_BRACE_CLOSE('}'),
    PARENTHESIS_OPEN('('),
    PARENTHESIS_CLOSE(')'),
    EQUAL_SIGN('='),
    AMPERSAND('&'),
    SEMICOLON(';'),
    DOLLAR_SIGN('$'),
    QUESTION_MARK('?'),
    ASTERISK('*'),
    DABLE_COTE('"'),
    SHINGLE_COTE('\''),
    ZENBRANK('　'),
	BRANK(' ');

    private final char character;

    InvalidCharacter(char character) {
        this.character = character;
    }

    public char getCharacter() {
        return character;
    }
}
