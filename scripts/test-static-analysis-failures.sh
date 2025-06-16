#!/bin/bash

# 静的解析失敗テスト実行スクリプト
# 各ツールを段階的に実行して、期待される失敗を確認

echo "🧪 静的解析失敗テスト開始"
echo "========================================"

cd "$(dirname "$0")/DroneInventorySystem"

# カウンター初期化
failed_tests=0
total_tests=0

# テスト結果記録関数
record_test_result() {
    local test_name="$1"
    local expected_result="$2"
    local actual_result="$3"
    
    total_tests=$((total_tests + 1))
    
    if [ "$expected_result" = "$actual_result" ]; then
        echo "✅ $test_name: 期待通り${expected_result}しました"
    else
        echo "❌ $test_name: 期待は${expected_result}でしたが、実際は${actual_result}でした"
        failed_tests=$((failed_tests + 1))
    fi
}

# Phase 1: フォーマットチェック
echo ""
echo "📝 Phase 1: フォーマットチェック"
echo "----------------------------------------"

echo "Google Java Formatでのフォーマット前の状態確認..."
mvn fmt:check > /dev/null 2>&1
format_check_result=$?

if [ $format_check_result -ne 0 ]; then
    record_test_result "フォーマットチェック" "失敗" "失敗"
else
    record_test_result "フォーマットチェック" "失敗" "成功"
fi

# Phase 2: 基本Checkstyleチェック（警告レベル）
echo ""
echo "📋 Phase 2: 基本Checkstyle（警告レベル）"
echo "----------------------------------------"

mvn checkstyle:check -Dcheckstyle.config.location=checkstyle-simple.xml > /dev/null 2>&1
simple_checkstyle_result=$?

if [ $simple_checkstyle_result -ne 0 ]; then
    record_test_result "基本Checkstyle" "失敗" "失敗"
else
    record_test_result "基本Checkstyle" "失敗" "成功"
fi

# Phase 3: 厳格Checkstyleチェック（エラーレベル）
echo ""
echo "🔍 Phase 3: 厳格Checkstyle（エラーレベル）"
echo "----------------------------------------"

mvn checkstyle:check -Dcheckstyle.config.location=checkstyle-strict.xml > /dev/null 2>&1
strict_checkstyle_result=$?

if [ $strict_checkstyle_result -ne 0 ]; then
    record_test_result "厳格Checkstyle" "失敗" "失敗"
else
    record_test_result "厳格Checkstyle" "失敗" "成功"
fi

# Phase 4: PMDチェック
echo ""
echo "🎯 Phase 4: PMD品質チェック"
echo "----------------------------------------"

mvn pmd:check > /dev/null 2>&1
pmd_result=$?

if [ $pmd_result -ne 0 ]; then
    record_test_result "PMDチェック" "失敗" "失敗"
else
    record_test_result "PMDチェック" "失敗" "成功"
fi

# Phase 5: SpotBugsチェック
echo ""
echo "🐛 Phase 5: SpotBugsチェック"
echo "----------------------------------------"

mvn compile spotbugs:check > /dev/null 2>&1
spotbugs_result=$?

if [ $spotbugs_result -ne 0 ]; then
    record_test_result "SpotBugsチェック" "失敗" "失敗"
else
    record_test_result "SpotBugsチェック" "失敗" "成功"
fi

# Phase 6: Pre-commitフック失敗テスト
echo ""
echo "🔗 Phase 6: Pre-commitフック動作確認"
echo "----------------------------------------"

# Pre-commitフックが存在するかチェック
if [ -f "../.git/hooks/pre-commit" ]; then
    echo "Pre-commitフックが設定されています"
    
    # フックを一時的に実行（実際のコミットはしない）
    echo "Pre-commitフックのテスト実行..."
    
    # ファイルをステージングエリアに追加
    git add . > /dev/null 2>&1
    
    # Pre-commitフックを手動実行
    bash ../.git/hooks/pre-commit > /dev/null 2>&1
    precommit_result=$?
    
    if [ $precommit_result -ne 0 ]; then
        record_test_result "Pre-commitフック" "警告あり" "警告あり"
    else
        record_test_result "Pre-commitフック" "警告あり" "成功"
    fi
else
    echo "⚠️  Pre-commitフックが設定されていません"
    record_test_result "Pre-commitフック" "設定済み" "未設定"
fi

# 結果サマリー
echo ""
echo "📊 テスト結果サマリー"
echo "========================================"
echo "総テスト数: $total_tests"
echo "失敗テスト数: $failed_tests"
echo "成功率: $(( (total_tests - failed_tests) * 100 / total_tests ))%"

if [ $failed_tests -eq 0 ]; then
    echo "🎉 すべてのテストが期待通りの結果となりました！"
    echo "   静的解析ツールが正しく問題を検出しています。"
else
    echo "⚠️  $failed_tests 個のテストが期待と異なる結果となりました。"
    echo "   設定の見直しが必要な可能性があります。"
fi

echo ""
echo "📋 詳細レポート生成コマンド:"
echo "mvn checkstyle:checkstyle pmd:pmd spotbugs:spotbugs"
echo ""
echo "📁 レポート確認場所:"
echo "- Checkstyle: target/site/checkstyle.html"
echo "- PMD: target/site/pmd.html"
echo "- SpotBugs: target/site/spotbugs.html"

# 詳細レポート生成
echo ""
echo "📊 詳細レポート生成中..."
mvn checkstyle:checkstyle pmd:pmd spotbugs:spotbugs > /dev/null 2>&1

if [ $? -eq 0 ]; then
    echo "✅ 詳細レポートが生成されました"
else
    echo "⚠️  レポート生成中に問題が発生しました"
fi

echo ""
echo "🧪 静的解析失敗テスト完了"
