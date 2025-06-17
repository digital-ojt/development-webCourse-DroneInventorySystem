package com.digitalojt.web.test;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
// 未使用インポート
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

// 意図的にフォーマットを崩したコントローラー
@Controller
public class poorly_formatted_controller { // クラス名: camelCase違反

	// スペースインデント（タブであるべき）
	private String service_name = "BadService"; // フィールド名: スネークケース違反

	// 不規則なインデント
	public static final String error_message = "Error occurred"; // 定数名: camelCase違反

	// メソッド名: PascalCase違反
	@GetMapping("/test")
	public String ShowTestPage(Model model) { // 波かっこ位置違反: 次の行にある
		// 行長違反: 120文字を超える長い行
		String veryLongVariableName =
			"This is an intentionally very long string that exceeds the maximum line length limit of 120 characters to trigger formatting violations in our static analysis tools";

		// 不規則なインデント
		model.addAttribute("message", veryLongVariableName);

		// 不適切な空白使用
		if (true) { // スペース無し
			return "test"; // スペース無し
		}

		return "test";
	}

	// JavaDoc無し + 引数名違反
	@PostMapping("/submit")
	public String ProcessSubmission(String USER_INPUT, Model model) { // メソッド名PascalCase + 引数UPPER_CASE違反
		// マジックナンバー + 深いネスト
		for (int i = 0; i < 10; i++) { // スペース無し
			if (i % 2 == 0) {
				if (i > 5) {
					if (i < 8) { // 深いネスト
						System.out.println("Nested logic: " + i);
					}
				}
			}
		}

		// 不適切なString比較
		if (USER_INPUT == "test") { // == でString比較
			model.addAttribute("result", "success");
		} else {
			model.addAttribute("result", "failure");
		}

		return "result"; // スペース無し
	}

	// 長すぎるメソッド（30行超過） + フォーマット違反
	public void badlyFormattedLongMethod() { // スペース無し
		System.out.println("Line 1"); // インデント無し
		System.out.println("Line 2");
		System.out.println("Line 3"); // 不規則インデント
		System.out.println("Line 4");
		System.out.println("Line 5");
		System.out.println("Line 6");
		System.out.println("Line 7");
		System.out.println("Line 8");
		System.out.println("Line 9");
		System.out.println("Line 10");
		System.out.println("Line 11");
		System.out.println("Line 12");
		System.out.println("Line 13");
		System.out.println("Line 14");
		System.out.println("Line 15");
		System.out.println("Line 16");
		System.out.println("Line 17");
		System.out.println("Line 18");
		System.out.println("Line 19");
		System.out.println("Line 20");
		System.out.println("Line 21");
		System.out.println("Line 22");
		System.out.println("Line 23");
		System.out.println("Line 24");
		System.out.println("Line 25");
		System.out.println("Line 26");
		System.out.println("Line 27");
		System.out.println("Line 28");
		System.out.println("Line 29");
		System.out.println("Line 30");
		System.out.println("Line 31"); // 30行超過
		System.out.println("Line 32");
		System.out.println("Line 33");
	}
}
