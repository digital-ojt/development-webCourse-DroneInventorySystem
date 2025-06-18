#!/bin/bash
# Pre-commit Hook Setup Script
# このスクリプトは開発者がプリコミットフックをセットアップするために使用します
# 🎨 2024年6月17日更新: prettier-java + Eclipse統合フォーマット環境対応

echo "🔧 プリコミットフック セットアップ開始 (統合フォーマット環境対応)"

# プロジェクトルートディレクトリの確認
if [ ! -f "DroneInventorySystem/pom.xml" ]; then
    echo "❌ エラー: プロジェクトルートディレクトリで実行してください"
    echo "   期待される場所: development-webCourse-DroneInventorySystem ディレクトリ"
    echo "   必要なファイル: development-webCourse-DroneInventorySystem/DroneInventorySystem/pom.xml"
    exit 1
fi

# 統合フォーマット環境の確認
echo "🎨 統合フォーマット環境の確認..."
DRONE_DIR="DroneInventorySystem"

if [ -f "$DRONE_DIR/scripts/format-and-check.sh" ]; then
    echo "✅ 統合フォーマットスクリプト: $DRONE_DIR/format-and-check.sh"
else
    echo "⚠️  統合フォーマットスクリプトが見つかりません"
fi

if [ -f "$DRONE_DIR/package.json" ] && [ -f "$DRONE_DIR/.prettierrc" ]; then
    echo "✅ Prettier Java環境: package.json + .prettierrc"
else
    echo "⚠️  Prettier Java環境が不完全です"
fi

if [ -f "$DRONE_DIR/eclipse-format.xml" ]; then
    echo "✅ Eclipse Formatter設定: eclipse-format.xml"
else
    echo "⚠️  Eclipse Formatter設定が見つかりません"
fi

# 既存のプリコミットフックをバックアップ
if [ -f ".git/hooks/pre-commit" ]; then
    echo "📦 既存のプリコミットフックをバックアップ中..."
    cp .git/hooks/pre-commit .git/hooks/pre-commit.backup.$(date +%Y%m%d_%H%M%S)
    echo "✅ バックアップ完了: .git/hooks/pre-commit.backup.*"
fi

# 新しいプリコミットフックをコピー
echo "📝 新しいプリコミットフック(統合フォーマット対応)をインストール中..."
cp scripts/hooks/pre-commit .git/hooks/pre-commit

# 実行権限を付与
chmod +x .git/hooks/pre-commit

echo "⚙️  Git設定でpre-commitの詳細出力を設定中..."

# pre-commitの詳細出力設定
git config --local core.hooksPath .git/hooks
git config --local commit.verbose true
git config --local advice.detachedHead false
git config --local pre-commit.verbose true

echo "✅ pre-commitの詳細出力設定が完了しました"

echo "📋 Eclipse用の確認ファイルを作成中..."

# カスタムpre-commitフックでEclipse用の結果ファイルを生成
cat > .git/hooks/pre-commit << 'EOF'
#!/bin/bash

# 除外ブランチリスト（静的解析をスキップするブランチ）
SKIP_BRANCHES=(
    "main"
    "master" 
    "master-test"
    "develop"
    "release/*"
    "hotfix/*"
)

# 現在のブランチ名を取得
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)

# ブランチ除外チェック
for skip_branch in "${SKIP_BRANCHES[@]}"; do
    if [[ "$CURRENT_BRANCH" == $skip_branch ]]; then
        echo "🔄 ブランチ '$CURRENT_BRANCH' は静的解析をスキップします"
        echo "✅ コミットを続行します（品質チェック無し）"
        exit 0
    fi
done

echo "🔍 Pre-commit checks を実行中..."
echo "📍 現在のブランチ: $CURRENT_BRANCH"

# プロジェクトルートディレクトリを保存
PROJECT_ROOT=$(pwd)

# 統合フォーマット環境での静的解析実行
cd DroneInventorySystem

# format-and-check.shの実行結果を保存
echo "実行時間: $(date)" > "$PROJECT_ROOT/.git/pre-commit-last-run.log"
echo "ブランチ: $CURRENT_BRANCH" >> "$PROJECT_ROOT/.git/pre-commit-last-run.log"
echo "ディレクトリ: $(pwd)" >> "$PROJECT_ROOT/.git/pre-commit-last-run.log"
echo "=== 静的解析実行結果 ===" >> "$PROJECT_ROOT/.git/pre-commit-last-run.log"

./format-and-check.sh >> "$PROJECT_ROOT/.git/pre-commit-last-run.log" 2>&1
exit_code=$?

echo "=== 実行完了 (終了コード: $exit_code) ===" >> "$PROJECT_ROOT/.git/pre-commit-last-run.log"

# Eclipse で確認しやすい場所にコピー
cp "$PROJECT_ROOT/.git/pre-commit-last-run.log" "$PROJECT_ROOT/pre-commit-result.txt"

# プロジェクトルートに戻る
cd "$PROJECT_ROOT"

if [ $exit_code -ne 0 ]; then
    echo ""
    echo "❌ Pre-commit checks に失敗しました (終了コード: $exit_code)"
    echo ""
    echo "📋 詳細確認方法："
    echo "   Eclipse のPackage Explorerで 'pre-commit-result.txt' を開いてください"
    echo ""
    echo "🔧 修正後、再度コミットを試してください"
    echo ""
    # コミットを確実に停止
    exit $exit_code
else
    echo "✅ Pre-commit checks が成功しました"
    # 成功時はresultファイルに成功メッセージも追加
    echo "" >> "$PROJECT_ROOT/pre-commit-result.txt"
    echo "✅ 静的解析が正常に完了しました" >> "$PROJECT_ROOT/pre-commit-result.txt"
fi

exit 0
EOF

# 実行権限を再設定
chmod +x .git/hooks/pre-commit

# .gitignoreに追加（重複チェック）
if ! grep -q "pre-commit-result.txt" .gitignore 2>/dev/null; then
    echo "pre-commit-result.txt" >> .gitignore
    echo "✅ .gitignore に pre-commit-result.txt を追加しました"
fi

echo "✅ プリコミットフックのインストール完了"
echo ""
echo "🧪 pre-commitフックのテスト実行中..."
if .git/hooks/pre-commit; then
    echo "✅ pre-commitフックテスト: 成功"
else
    echo "⚠️  pre-commitフックテスト: 静的解析エラーが検出されました"
    echo "   詳細は pre-commit-result.txt を確認してください"
fi
echo ""
echo "📋 統合フォーマット環境の設定内容:"
echo "   🎨 統合フォーマット: ./format-and-check.sh"
echo "   🎨 Prettier Java: npm + prettier-plugin-java (タブ設定)"
echo "   🎨 Eclipse Formatter: eclipse-format.xml (タブ設定)"
echo "   🎨 スペース→タブ変換: 自動実行"
echo "   • 静的解析: Checkstyle, PMD, SpotBugs"
echo "   • 除外ブランチ: main, master, develop, release/*, hotfix/*"
echo ""
echo "🔍 Eclipse用pre-commit結果確認:"
echo "   📁 コミット失敗時: プロジェクトルートの 'pre-commit-result.txt' を開く"
echo "   📋 操作ガイド: 'PRE-COMMIT-GUIDE.md' を参照"
echo "   🚫 除外ブランチ: main, master, master-test, develop, release/*, hotfix/*"
echo ""
echo "🧪 テスト実行:"
echo "   .git/hooks/pre-commit  # 手動テスト"
echo "   cd $DRONE_DIR && ./format-and-check.sh  # 統合フォーマットテスト"
echo ""
echo "🔧 カスタマイズ:"
echo "   除外ブランチを変更: .git/hooks/pre-commit 内の SKIP_BRANCHES 配列"
echo "   フォーマット設定: $DRONE_DIR/.prettierrc, $DRONE_DIR/eclipse-format.xml"
echo ""
echo "🎉 統合フォーマット環境対応 プリコミットフック セットアップ完了！"
echo "    コミット時に統合フォーマット + 品質チェックが自動実行されます"
echo "    📋 Eclipse用: コミット失敗時は 'pre-commit-result.txt' で詳細確認"
echo "    🚫 除外ブランチでは静的解析がスキップされます"
