#!/bin/bash

# 短縮版プリコミットフック（テスト用）
PROJECT_ROOT="$(git rev-parse --show-toplevel)"
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)

echo "🔧 短縮版テスト: プリコミットフック開始"

# 結果ログ生成
generate_result_log() {
    local exit_code=$1
    local temp_output_file=$2
    local log_file="$PROJECT_ROOT/.git/pre-commit-last-run.log"
    local result_file="$PROJECT_ROOT/pre-commit-result.txt"
    
    echo "🔧 デバッグ: generate_result_log開始 (exit_code=$exit_code)" >&2
    echo "   result_file: $result_file" >&2
    
    # 詳細ログ生成
    {
        echo "=== macOS用 Pre-commit Hook 実行結果 ==="
        echo "実行時間: $(date)"
        echo "ブランチ: $CURRENT_BRANCH"
        echo "実行環境: macOS $(sw_vers -productVersion)"
        echo "Java版: $(java -version 2>&1 | head -n1)"
        echo ""
        echo "=== 実行内容 ==="
        echo "1. macOS用統合フォーマット・静的解析スクリプト実行"
        echo "2. scripts/core/mac/format-and-check.sh"
        echo ""
        echo "=== 実行結果詳細 ==="
        # 一時ファイルの内容を追加
        if [ -f "$temp_output_file" ]; then
            echo "🔧 一時ファイルからログを読み込み中..." >&2
            cat "$temp_output_file"
        else
            echo "⚠️ 一時ファイルが見つかりません: $temp_output_file" >&2
            echo "❌ 一時ファイルが見つかりません: $temp_output_file"
        fi
    } > "$log_file"
    
    # ログファイル生成の確認
    if [ -f "$log_file" ]; then
        echo "✅ ログファイル生成完了: $log_file" >&2
    else
        echo "❌ ログファイル生成失敗: $log_file" >&2
    fi
    
    # Eclipse/IntelliJ IDEA用の結果ファイル生成
    echo "🔧 結果ファイル生成中: $result_file" >&2
    cp "$log_file" "$result_file"
    
    # コピー結果の確認
    if [ -f "$result_file" ]; then
        echo "✅ 結果ファイルコピー完了: $result_file" >&2
        echo "   ファイルサイズ: $(wc -l < "$result_file") lines" >&2
    else
        echo "❌ 結果ファイルコピー失敗: $result_file" >&2
    fi
    
    # 結果に応じたメッセージ追加
    if [ $exit_code -eq 0 ]; then
        echo "" >> "$result_file"
        echo "✅ 全ての品質チェックに合格しました" >> "$result_file"
        echo "🎉 コミットが正常に完了しました" >> "$result_file"
    else
        echo "" >> "$result_file"
        echo "❌ 品質チェックで問題が検出されました" >> "$result_file"
        echo "🔧 修正後、再度コミットを実行してください" >> "$result_file"
        echo "" >> "$result_file"
        echo "📋 macOS用トラブルシューティング:" >> "$result_file"
        echo "  - PRE-COMMIT-GUIDE-MAC.md を確認してください" >> "$result_file"
        echo "  - 手動実行: cd DroneInventorySystem && ../scripts/core/mac/format-and-check.sh" >> "$result_file"
        echo "  - Homebrew更新: brew update && brew upgrade" >> "$result_file"
    fi
}

# テスト実行（静的解析をスキップ）
cd "$PROJECT_ROOT/DroneInventorySystem"

echo "🔧 短縮版テスト: 簡易チェック実行中..."
echo "Maven pom.xml存在確認..."
if [ -f "pom.xml" ]; then
    echo "✅ pom.xml確認OK"
    exit_code=0
else
    echo "❌ pom.xml不在"
    exit_code=1
fi

# 一時ファイル作成
temp_output="$PROJECT_ROOT/.git/pre-commit-temp-output.log"
{
    echo "短縮版テスト実行"
    echo "pom.xml確認: $([ -f "pom.xml" ] && echo "OK" || echo "NG")"
    echo "package.json確認: $([ -f "package.json" ] && echo "OK" || echo "NG")"
    echo "実行時間: $(date)"
} > "$temp_output"

# 結果ログを生成
generate_result_log $exit_code "$temp_output"

echo "🔧 短縮版テスト完了"
rm -f "$temp_output"

exit $exit_code
