package com.digitalojt.web.consts;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作種類Enum
 *
 * @author dotlife
 */
public enum OperationType {
	CREATE(1, "登録"),
	UPDATE(2, "更新"),
	DELETE(3, "削除");

	private final Integer typeCode; // 操作タイプのコード
	private final String typeName; // 操作タイプの表示文言

	OperationType(Integer typeCode, String typeName) {
		this.typeCode = typeCode;
		this.typeName = typeName;
	}

	public Integer getTypeCode() {
		return typeCode;
	}

	public String getTypeName() {
		return typeName;
	}

	/**
	 * 操作タイプのコードから対応する Enum を取得
	 *
	 * @param val 操作タイプのコード
	 * @return 操作タイプ名 (該当しない場合は "不明な操作" を返す)
	 */
	public static String fromTypeCode(Integer val) {
		for (OperationType type : values()) {
			if (type.getTypeCode().equals(val)) {
				return type.getTypeName();
			}
		}
		return "不明な操作"; // デフォルト値
	}

	// 操作種類の一覧を取得
	public static List<String> getTypeNames() {
		return Arrays.stream(OperationType.values()).map(OperationType::getTypeName).collect(Collectors.toList());
	}
}
