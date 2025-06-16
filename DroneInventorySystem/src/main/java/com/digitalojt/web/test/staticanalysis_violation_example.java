// 意図的に問題のあるコード - 静的解析エラー検出テスト用
// ファイル名: 違反 - クラス名と一致しない
package com.digitalojt.web.test;

import java.io.*;
import java.sql.*;
// 未使用インポート
import java.util.*;
import javax.swing.*; // 未使用インポート

// クラス名: 違反 - camelCaseになっている（PascalCaseであるべき）
public class staticanalysis_violation_example {

  // フィールド名: 違反 - PascalCaseになっている（camelCaseであるべき）
  private String UserName;
  private String PASSWORD = "12345"; // 違反 - ハードコードされたパスワード
  private String NEW_VIOLATION_FIELD = "test"; // 新しい違反追加
  private String ANOTHER_BAD_FIELD = "more violations"; // さらに違反追加

  // 定数名: 違反 - camelCaseになっている（UPPER_CASEであるべき）
  public static final int maxRetryCount = 5;

  // JavaDoc無し: 違反 - publicメソッドにはJavaDocが必要
  public void processUserData(String user_name, int AGE) { // 違反 - パラメータ名規則違反
    // マジックナンバー: 違反
    if (AGE > 18) {
      System.out.println("Adult user");
    }

    // メソッド長違反のための意図的に長いメソッド
    String result = "";
    for (int i = 0; i < 100; i++) { // マジックナンバー違反
      if (i % 2 == 0) {
        if (i % 4 == 0) {
          if (i % 8 == 0) { // 過度なネスト違反
            if (i % 16 == 0) {
              result += "Deep nested logic " + i;
            }
          }
        }
      }
    }

    // 空のキャッチブロック: 違反
    try {
      int division = 10 / 0;
    } catch (Exception e) {
      // 何もしない - PMD違反
    }

    // 未使用変数: 違反
    String unusedVariable = "This is not used";

    // Boolean式の複雑化: 違反
    boolean complexCondition = true;
    if (complexCondition == true) { // 簡略化可能
      System.out.println("True condition");
    }

    // ログレベル不適切使用
    System.err.println("Debug info"); // 違反 - System.errをデバッグに使用
  }

  // メソッド名: 違反 - PascalCaseになっている
  public void ProcessOrder() {
    // 波かっこ違反のための意図的な配置
    if (true) { // 違反 - 波かっこが次の行にある
      System.out.println("Bad brace style");
    }

    // 無限ループの可能性: SpotBugs違反
    while (true) {
      break; // 意図的に即座にbreak
    }
  }

  // equals()をオーバーライドしているがhashCode()をオーバーライドしていない: 違反
  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  // デッドコード: 違反
  private void deadCode() {
    return;
    System.out.println("This will never execute"); // unreachable code
  }

  // 長すぎるメソッド（30行超過）: 違反
  public void veryLongMethod() {
    System.out.println("Line 1");
    System.out.println("Line 2");
    System.out.println("Line 3");
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
    System.out.println("Line 31"); // 30行を超過
    System.out.println("Line 32");
  }
}
