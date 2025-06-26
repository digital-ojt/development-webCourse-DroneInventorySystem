#!/bin/bash

# 静的解析テスト用スクリプト（詳細エラー出力付き）
PROJECT_ROOT="$(git rev-parse --show-toplevel)"
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)

echo "🔧 静的解析テスト開始"
echo "📍 プロジェクトルート: $PROJECT_ROOT"
echo "📍 ブランチ: $CURRENT_BRANCH"

cd "$PROJECT_ROOT/DroneInventorySystem"

# 結果ログ生成関数
generate_detailed_result_log() {
    local exit_code=$1
    local checkstyle_log=$2
    local pmd_log=$3
    local spotbugs_log=$4
    local result_file="$PROJECT_ROOT/pre-commit-result.txt"
    
    echo "🔧 詳細結果ログ生成中: $result_file"
    
    # 詳細ログ生成
    {
        echo "=== macOS用 Pre-commit Hook 実行結果（詳細版） ==="
        echo "実行時間: $(date)"
        echo "ブランチ: $CURRENT_BRANCH"
        echo "実行環境: macOS $(sw_vers -productVersion)"
        echo "Java版: $(java -version 2>&1 | head -n1)"
        echo ""
        echo "=== 実行内容 ==="
        echo "1. Checkstyle チェック"
        echo "2. PMD チェック"
        echo "3. SpotBugs チェック"
        echo ""
        echo "=== 実行結果詳細 ==="
        
        # Checkstyle結果
        echo ""
        echo "◆ Checkstyle 結果:"
        if [ -f "$checkstyle_log" ] && [ -s "$checkstyle_log" ]; then
            echo "  → エラーが検出されました"
            echo "  --- Checkstyle 詳細ログ ---"
            cat "$checkstyle_log"
            echo "  --- Checkstyle ログ終了 ---"
        else
            echo "  → 合格"
        fi
        
        # PMD結果
        echo ""
        echo "◆ PMD 結果:"
        if [ -f "$pmd_log" ] && [ -s "$pmd_log" ]; then
            echo "  → 問題が検出されました"
            echo "  --- PMD 詳細ログ ---"
            cat "$pmd_log"
            echo "  --- PMD ログ終了 ---"
        else
            echo "  → 合格"
        fi
        
        # SpotBugs結果
        echo ""
        echo "◆ SpotBugs 結果:"
        if [ -f "$spotbugs_log" ] && [ -s "$spotbugs_log" ]; then
            echo "  → 問題が検出されました"
            echo "  --- SpotBugs 詳細ログ ---"
            cat "$spotbugs_log"
            echo "  --- SpotBugs ログ終了 ---"
        else
            echo "  → 合格"
        fi
        
        echo ""
        if [ $exit_code -eq 0 ]; then
            echo "✅ 全ての品質チェックに合格しました"
            echo "🎉 コミットが正常に完了しました"
        else
            echo "❌ 品質チェックで問題が検出されました"
            echo "🔧 修正後、再度コミットを実行してください"
            echo ""
            echo "📋 修正ガイド:"
            echo "  - Checkstyle エラー: コーディング規約違反を修正"
            echo "  - PMD エラー: コード品質問題を修正"
            echo "  - SpotBugs エラー: バグパターンを修正"
        fi
    } > "$result_file"
    
    echo "✅ 詳細結果ログ生成完了: $result_file"
}

# 静的解析実行
echo ""
echo "🔍 静的解析実行開始"

checkstyle_log="/tmp/checkstyle-$$.log"
pmd_log="/tmp/pmd-$$.log"
spotbugs_log="/tmp/spotbugs-$$.log"
exit_code=0

# Checkstyle実行
echo "🔧 Checkstyle実行中..."
if mvn checkstyle:check > "$checkstyle_log" 2>&1; then
    echo "✅ Checkstyle: 合格"
    rm -f "$checkstyle_log"
else
    echo "❌ Checkstyle: 違反が検出されました"
    exit_code=1
fi

# PMD実行
echo "🔧 PMD実行中..."
if mvn pmd:check > "$pmd_log" 2>&1; then
    echo "✅ PMD: 合格"
    rm -f "$pmd_log"
else
    echo "❌ PMD: 問題が検出されました"
    exit_code=1
fi

# SpotBugs実行
echo "🔧 SpotBugs実行中..."
if mvn spotbugs:check > "$spotbugs_log" 2>&1; then
    echo "✅ SpotBugs: 合格"
    rm -f "$spotbugs_log"
else
    echo "❌ SpotBugs: 問題が検出されました"
    exit_code=1
fi

# 詳細結果ログ生成
generate_detailed_result_log $exit_code "$checkstyle_log" "$pmd_log" "$spotbugs_log"

# クリーンアップ
rm -f "$checkstyle_log" "$pmd_log" "$spotbugs_log"

echo ""
if [ $exit_code -eq 0 ]; then
    echo "🎉 静的解析テスト完了: 全て合格"
else
    echo "⚠️ 静的解析テスト完了: 問題が検出されました"
    echo "詳細は pre-commit-result.txt を確認してください"
fi

exit $exit_code
