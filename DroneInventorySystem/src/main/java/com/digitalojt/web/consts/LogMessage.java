package com.digitalojt.web.consts;

/**
 * ログメッセージ定数クラス
 * 
 * @author dotlife
 * 
 */
public class LogMessage {

	//IPアドレス
	public static final String IP_ADDRESS_KEY = "ipAddress";

	//ユーザーID
	public static final String USER_ID_KEY = "userId";

	//アクセスログ
	//ACCESSだけ表示される
	public static final String ACCESS_LOG = "ACCESS: %s";

	//エラーログ
	//(GETかPOST)-(処理対象の関数名)-(エラー内容)
	public static final String ERROR_LOG = "ERROR: %s - %s - %s";

	//アプリケーションログ
	//(GETかPOST)-(処理対象の関数名)-(開始か終了)
	public static final String APP_LOG = "APP: %s - %s - %s";
	
	public static final String PROCESS_START = "START";
	public static final String PROCESS_END = "END";
	public static final String FLASH_ATTRIBUTE_ERROR = "errorMsg";
    
	
	//HTTPメソッド
    public static final String HTTP_GET = "GET";
    public static final String HTTP_POST = "POST";
}
