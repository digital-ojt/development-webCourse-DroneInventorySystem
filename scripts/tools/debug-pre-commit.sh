#!/bin/bash

# デバッグ用プリコミットフック
PROJECT_ROOT="$(git rev-parse --show-toplevel)"
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)

echo "🔧 デバッグ: プリコミットフック開始"
echo "📍 プロジェクトルート: $PROJECT_ROOT"
echo "📍 ブランチ: $CURRENT_BRANCH"

# 結果ログ生成のテスト
generate_result_log() {
    local exit_code=$1
    local temp_output_file=$2
    local log_file="$PROJECT_ROOT/.git/pre-commit-last-run.log"
    local result_file="$PROJECT_ROOT/pre-commit-result.txt"
    
    echo "🔧 デバッグ: generate_result_log開始"
    echo "   exit_code: $exit_code"
    echo "   temp_output_file: $temp_output_file"
    echo "   log_file: $log_file"
    echo "   result_file: $result_file"
    
    # 詳細ログ生成
    {
        echo "=== デバッグ用 Pre-commit Hook 実行結果 ==="
        echo "実行時間: $(date)"
        echo "ブランチ: $CURRENT_BRANCH"
        echo "プロジェクトルート: $PROJECT_ROOT"
        echo ""
        echo "=== テスト結果 ==="
        echo "Exit Code: $exit_code"
        if [ -f "$temp_output_file" ]; then
            echo "一時ファイル存在: YES"
            echo "一時ファイルサイズ: $(wc -l < "$temp_output_file") lines"
        else
            echo "一時ファイル存在: NO"
        fi
    } > "$log_file"
    
    echo "🔧 デバッグ: ログファイル生成完了"
    
    # Eclipse/IntelliJ IDEA用の結果ファイル生成
    echo "🔧 デバッグ: 結果ファイル生成中: $result_file"
    cp "$log_file" "$result_file"
    
    # コピー結果の確認
    if [ -f "$result_file" ]; then
        echo "✅ デバッグ: 結果ファイルコピー完了: $result_file"
        echo "   ファイルサイズ: $(wc -l < "$result_file") lines"
    else
        echo "❌ デバッグ: 結果ファイルコピー失敗: $result_file"
    fi
    
    # 結果に応じたメッセージ追加
    echo "" >> "$result_file"
    echo "🔧 デバッグテスト完了" >> "$result_file"
    echo "プロジェクトルート: $PROJECT_ROOT" >> "$result_file"
}

# テスト実行
echo "🔧 デバッグ: テスト実行開始"
temp_file="$PROJECT_ROOT/.git/debug-temp.log"
echo "This is a debug test" > "$temp_file"

generate_result_log 1 "$temp_file"

echo "🔧 デバッグ: テスト完了"
echo "結果ファイル確認:"
ls -la "$PROJECT_ROOT/pre-commit-result.txt" 2>/dev/null || echo "❌ 結果ファイルが見つかりません"

# クリーンアップ
rm -f "$temp_file"
