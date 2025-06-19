#!/bin/bash
# Pre-commit Hook Uninstall Script
# このスクリプトはプリコミットフックを無効化するために使用します

echo "🗑️  プリコミットフック アンインストール開始"

# プロジェクトルートディレクトリの確認
if [ ! -f "DroneInventorySystem/pom.xml" ]; then
    echo "❌ エラー: プロジェクトルートディレクトリで実行してください"
    echo "   期待される場所: development-webCourse-DroneInventorySystem/"
    exit 1
fi

# プリコミットフックの存在確認
if [ ! -f ".git/hooks/pre-commit" ]; then
    echo "ℹ️  プリコミットフックは既に存在しません"
    exit 0
fi

# バックアップを作成してから削除
echo "📦 現在のプリコミットフックをバックアップ中..."
cp .git/hooks/pre-commit .git/hooks/pre-commit.removed.$(date +%Y%m%d_%H%M%S)

# プリコミットフックを削除
rm .git/hooks/pre-commit

echo "✅ プリコミットフックを無効化しました"
echo ""
echo "📋 実行された操作:"
echo "   • プリコミットフックを削除"
echo "   • バックアップファイルを作成: .git/hooks/pre-commit.removed.*"
echo ""
echo "🔄 再有効化方法:"
echo "   ./scripts/setup-pre-commit-hook.sh を実行"
echo ""
echo "🎉 アンインストール完了！コミット時の品質チェックは無効化されました"
