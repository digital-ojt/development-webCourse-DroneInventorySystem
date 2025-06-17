#!/bin/bash

# 静的解析失敗テスト実行スクリプト
# 各ツールを段階的に実行して、期待される失敗を確認
# 🎨 2024年6月17日更新: prettier-java + Eclipse統合フォーマット環境対応

echo "🧪 静的解析失敗テスト開始 (統合フォーマット環境対応)"
echo "========================================"

# ディレクトリ確認
if [ ! -d "$(dirname "$0")/../DroneInventorySystem" ]; then
    echo "❌ DroneInventorySystemディレクトリが見つかりません"
    echo "現在のディレクトリ: $(pwd)"
    echo "期待されるパス: $(dirname "$0")/../DroneInventorySystem"
    exit 1
fi

cd "$(dirname "$0")/../DroneInventorySystem"

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

# Phase 0: 統合フォーマット環境テスト 🎨 NEW
echo ""
echo "🎨 Phase 0: 統合フォーマット環境テスト"
echo "----------------------------------------"

# format-and-check.sh の存在確認
if [ -f "./format-and-check.sh" ]; then
    echo "統合フォーマットスクリプトの動作確認..."
    ./format-and-check.sh > /dev/null 2>&1
    integrated_format_result=$?
    
    if [ $integrated_format_result -eq 0 ]; then
        record_test_result "統合フォーマット実行" "成功" "成功"
    else
        record_test_result "統合フォーマット実行" "成功" "失敗"
    fi
else
    echo "⚠️  統合フォーマットスクリプトが見つかりません"
    record_test_result "統合フォーマットスクリプト" "存在" "不存在"
fi

# Prettier環境確認
if [ -f "./package.json" ] && [ -f "./.prettierrc" ]; then
    echo "Prettier Java環境の確認..."
    if command -v npm >/dev/null 2>&1; then
        npm run format > /dev/null 2>&1
        prettier_result=$?
        
        if [ $prettier_result -eq 0 ]; then
            record_test_result "Prettier Java実行" "成功" "成功"
        else
            record_test_result "Prettier Java実行" "成功" "失敗"
        fi
    else
        echo "⚠️  npm コマンドが見つかりません"
        record_test_result "npm環境" "利用可能" "利用不可"
    fi
else
    echo "⚠️  Prettier設定ファイルが見つかりません"
    record_test_result "Prettier設定" "存在" "不存在"
fi

# Eclipse Formatter テスト
echo "Eclipse Code Formatterの確認..."
mvn net.revelc.code.formatter:formatter-maven-plugin:format > /dev/null 2>&1
eclipse_formatter_result=$?

if [ $eclipse_formatter_result -eq 0 ]; then
    record_test_result "Eclipse Formatter実行" "成功" "成功"
else
    record_test_result "Eclipse Formatter実行" "成功" "失敗"
fi

# Phase 1: フォーマットチェック
echo ""
echo "📝 Phase 1: フォーマットチェック"
echo "----------------------------------------"

# 統合フォーマット後の状態チェック
echo "統合フォーマット適用後のGoogle Java Formatチェック..."
mvn fmt:check > /dev/null 2>&1
format_check_result=$?

# 注意: 統合フォーマット環境では、タブインデントによりGoogle Java Formatは失敗することが期待される
if [ $format_check_result -ne 0 ]; then
    record_test_result "Google Java Formatチェック" "失敗(タブ使用のため)" "失敗"
else
    echo "⚠️  予期しない結果: Google Java Formatがタブインデントを受け入れました"
    record_test_result "Google Java Formatチェック" "失敗(タブ使用のため)" "成功"
fi

# Eclipse Formatterによる検証チェック
echo "Eclipse Formatterによる検証チェック..."
mvn net.revelc.code.formatter:formatter-maven-plugin:validate > /dev/null 2>&1
eclipse_validate_result=$?

if [ $eclipse_validate_result -eq 0 ]; then
    record_test_result "Eclipse Formatter検証" "成功" "成功"
else
    record_test_result "Eclipse Formatter検証" "成功" "失敗"
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
echo "📊 テスト結果サマリー (統合フォーマット環境)"
echo "========================================"
echo "総テスト数: $total_tests"
echo "失敗テスト数: $failed_tests"
echo "成功率: $(( (total_tests - failed_tests) * 100 / total_tests ))%"

if [ $failed_tests -eq 0 ]; then
    echo "🎉 すべてのテストが期待通りの結果となりました！"
    echo "   統合フォーマット環境 + 静的解析ツールが正しく動作しています。"
    echo ""
    echo "🎨 統合フォーマット環境の特徴:"
    echo "   ✅ Prettier Java + Eclipse Formatter = タブインデント統一"
    echo "   ✅ Google Java Format = タブ非対応により予期された失敗"
    echo "   ✅ Checkstyle/PMD/SpotBugs = 品質問題を正しく検出"
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
echo ""
echo "🔧 統合フォーマット実行:"
echo "./format-and-check.sh  # 統合フォーマット + 品質チェック"

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
echo "🧪 統合フォーマット環境対応 静的解析失敗テスト完了"
echo "    prettier-java + Eclipse統合環境での品質検証が完了しました"
