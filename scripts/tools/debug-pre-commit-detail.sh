#!/bin/bash

# =============================================================================
# プリコミットフック デバッグ用テストスクリプト
# =============================================================================

echo "=== デバッグ: プリコミットフック処理開始 ==="
echo "実行時間: $(date)"
echo "現在のディレクトリ: $(pwd)"
echo "PROJECT_ROOT: $PROJECT_ROOT"

# プロジェクトルート取得
PROJECT_ROOT="$(git rev-parse --show-toplevel)"
echo "取得したPROJECT_ROOT: $PROJECT_ROOT"

# 現在のブランチ名を取得
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
echo "現在のブランチ: $CURRENT_BRANCH"

# DroneInventorySystemディレクトリに移動
echo "DroneInventorySystemディレクトリへ移動中..."
if [ ! -d "$PROJECT_ROOT/DroneInventorySystem" ]; then
    echo "❌ DroneInventorySystemディレクトリが見つかりません"
    exit 1
fi

cd "$PROJECT_ROOT/DroneInventorySystem"
echo "移動完了: $(pwd)"

# 統合フォーマット・静的解析スクリプトの存在確認
script_path="../scripts/core/mac/format-and-check.sh"
echo "スクリプトパス: $script_path"
if [ -f "$script_path" ]; then
    echo "✅ スクリプトファイル存在確認: $script_path"
    echo "権限: $(ls -la "$script_path")"
else
    echo "❌ スクリプトファイルが見つかりません: $script_path"
    exit 1
fi

# 一時ファイルパス設定
temp_output="$PROJECT_ROOT/.git/pre-commit-temp-output.log"
result_file="$PROJECT_ROOT/pre-commit-result.txt"
echo "一時ファイル: $temp_output"
echo "結果ファイル: $result_file"

# 実行権限確認
chmod +x "$script_path"
echo "実行権限付与完了"

# スクリプト実行（デバッグ用に短時間で停止）
echo "=== スクリプト実行開始（10秒テスト） ==="
echo "実行コマンド: $script_path > $temp_output 2>&1"

# 10秒後に強制終了
( 
    sleep 10
    pkill -f "format-and-check.sh" 2>/dev/null || true
    echo "10秒タイムアウト: プロセス強制終了"
) &
timeout_pid=$!

# スクリプト実行
"$script_path" > "$temp_output" 2>&1 &
script_pid=$!

echo "スクリプトPID: $script_pid"
echo "タイムアウトPID: $timeout_pid"

# 5秒待機してファイル確認
sleep 5
echo "=== 5秒後の状況確認 ==="
echo "一時ファイル確認:"
if [ -f "$temp_output" ]; then
    echo "✅ 一時ファイル存在: $(wc -l < "$temp_output") lines"
    echo "最新の数行:"
    tail -5 "$temp_output"
else
    echo "❌ 一時ファイル未作成"
fi

# プロセス状況確認
if kill -0 $script_pid 2>/dev/null; then
    echo "スクリプトプロセス実行中"
else
    echo "スクリプトプロセス終了済み"
fi

# クリーンアップ
kill $timeout_pid 2>/dev/null || true
kill $script_pid 2>/dev/null || true

echo "=== デバッグテスト完了 ==="
