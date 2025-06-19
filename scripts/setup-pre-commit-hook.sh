#!/bin/bash
# Pre-commit Hook Setup Script
# このスクリプトは開発者がプリコミットフックをセットアップするために使用します

echo "🔧 プリコミットフック セットアップ開始"

# プロジェクトルートディレクトリの確認
if [ ! -f "DroneInventorySystem/pom.xml" ]; then
    echo "❌ エラー: プロジェクトルートディレクトリで実行してください"
    echo "   期待される場所: development-webCourse-DroneInventorySystem/"
    exit 1
fi

# 既存のプリコミットフックをバックアップ
if [ -f ".git/hooks/pre-commit" ]; then
    echo "📦 既存のプリコミットフックをバックアップ中..."
    cp .git/hooks/pre-commit .git/hooks/pre-commit.backup.$(date +%Y%m%d_%H%M%S)
    echo "✅ バックアップ完了: .git/hooks/pre-commit.backup.*"
fi

# 新しいプリコミットフックをコピー
echo "📝 新しいプリコミットフックをインストール中..."
cp scripts/hooks/pre-commit .git/hooks/pre-commit

# 実行権限を付与
chmod +x .git/hooks/pre-commit

echo "✅ プリコミットフックのインストール完了"
echo ""
echo "📋 設定内容:"
echo "   • 静的解析: Checkstyle, PMD, SpotBugs"
echo "   • 自動フォーマット: Google Java Format"
echo "   • 除外ブランチ: main, master, develop, release/*, hotfix/*"
echo ""
echo "🧪 テスト実行:"
echo "   .git/hooks/pre-commit  # 手動テスト"
echo ""
echo "🔧 カスタマイズ:"
echo "   除外ブランチを変更: scripts/hooks/pre-commit 内の SKIP_BRANCHES 配列"
echo ""
echo "🎉 セットアップ完了！コミット時に自動的に品質チェックが実行されます"
