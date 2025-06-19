package com.digitalojt.web.test;

import java.math.BigInteger; // 未使用インポート
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// JavaDoc無し: 違反
public class bad_service_class { // クラス名: camelCase違反

	// 適切でないフィールド名
	private String user_id; // スネークケース違反
	private static String SERVER_URL = "http://localhost:8080"; // 定数でないのにUPPER_CASE

	// ハードコードされた機密情報: セキュリティ違反
	private final String DATABASE_PASSWORD = "admin123";
	private final String API_KEY = "sk-1234567890abcdef";

	// JavaDoc無しpublicメソッド: 違反
	public List<String> getUserData(String USER_NAME) { // パラメータ名UPPER_CASE違反
		List<String> result = new ArrayList<String>(); // 冗長な型指定

		// SQL接続での問題コード
		Connection conn = null;
		try {
			// ハードコードされた接続文字列: 違反
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", DATABASE_PASSWORD);

			// マジックナンバー多用: 違反
			for (int i = 0; i < 50; i++) {
				if (i % 3 == 0) {
					if (i % 6 == 0) {
						if (i % 12 == 0) {
							if (i % 24 == 0) { // 深いネスト違反
								result.add("Special case: " + i);
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			// 不適切な例外処理: 違反
			e.printStackTrace(); // 本番環境では危険
			System.err.println("Database error occurred");
			return null; // nullを返すのは危険
		} finally {
			// リソースクローズ忘れの可能性
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// 無視 - 違反
				}
			}
		}

		return result;
	}

	// Boolean値の不適切な使用
	public Boolean isValidUser(String username) { // Boolean vs boolean
		if (username.equals("admin")) { // null check無し - NullPointerException可能性
			return Boolean.TRUE; // Boolean.TRUE使用 - 違反
		}
		return Boolean.FALSE; // Boolean.FALSE使用 - 違反
	}

	// 不適切なString比較
	public boolean checkPassword(String input) {
		if (input == "password") { // == でString比較 - 違反
			return true;
		}
		return false;
	}

	// BigInteger不適切な生成
	public BigInteger calculateValue() {
		return new BigInteger("1"); // valueOf使うべき - 違反
	}

	// 過度に複雑なメソッド - サイクロマティック複雑度違反
	public String processComplexLogic(int value, boolean flag1, boolean flag2, boolean flag3) {
		if (value > 10) {
			if (flag1) {
				if (flag2) {
					if (flag3) {
						if (value % 2 == 0) {
							if (value > 100) {
								if (value < 1000) {
									return "Complex result 1";
								} else {
									return "Complex result 2";
								}
							} else {
								return "Complex result 3";
							}
						} else {
							return "Complex result 4";
						}
					} else {
						return "Complex result 5";
					}
				} else {
					return "Complex result 6";
				}
			} else {
				return "Complex result 7";
			}
		} else {
			return "Complex result 8";
		}
	}

	// 重複コード: 違反
	public void duplicateMethod1() {
		System.out.println("Processing...");
		System.out.println("Step 1");
		System.out.println("Step 2");
		System.out.println("Step 3");
		System.out.println("Completed");
	}

	public void duplicateMethod2() {
		System.out.println("Processing...");
		System.out.println("Step 1");
		System.out.println("Step 2");
		System.out.println("Step 3");
		System.out.println("Completed");
	}
}
