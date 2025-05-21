package com.digitalojt.web.consts;

/**
 * 在庫の分類情報に関するEnum
 * 
 * @author dotlife
 *
 */
public enum Category {

	FRAME("フレーム"),
	PROPELLER("プロペラ"),
	ELECTRIC_MOTOR("電動モーター"),
	ELECTRONIC_SPEED_CONTROLLER("電子速度調整器"),
	BATTERY("バッテリー"),
	FLIGHT_CONTROLLER("フライトコントローラー"),
	REMOTE_CONTROLLER("リモートコントローラー"),
	RECEIVER("受信機"),
	GPS_MODULE("GPSモジュール"),
	CAMERA_SENSOR("カメラ／センサー");

	private final String categoryName;

	Category(String name) {
		this.categoryName = name;
	}

	public String getName() {
		return categoryName;
	}
}
