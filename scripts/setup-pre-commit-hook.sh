#!/bin/bash
# Pre-commit Hook Setup Script
# このスクリプトは開発者がプリコミットフックをセットアップするために使用します
# 🎨 2024年6月17日更新: prettier-java + Eclipse統合フォーマット環境対応

echo "🔧 プリコミットフック セットアップ開始 (統合フォーマット環境対応)"

# プロジェクトルートディレクトリの確認
if [ ! -f "development-webCourse-DroneInventorySystem/DroneInventorySystem/pom.xml" ]; then
    echo "❌ エラー: プロジェクトルートディレクトリで実行してください"
    echo "   期待される場所: 静的解析対応_202506/ ディレクトリ"
    echo "   必要なファイル: development-webCourse-DroneInventorySystem/DroneInventorySystem/pom.xml"
    exit 1
fi

# 統合フォーマット環境の確認
echo "🎨 統合フォーマット環境の確認..."
DRONE_DIR="development-webCourse-DroneInventorySystem/DroneInventorySystem"

if [ -f "$DRONE_DIR/format-and-check.sh" ]; then
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

echo "✅ プリコミットフックのインストール完了"
echo ""
echo "📋 統合フォーマット環境の設定内容:"
echo "   🎨 統合フォーマット: ./format-and-check.sh"
echo "   🎨 Prettier Java: npm + prettier-plugin-java (タブ設定)"
echo "   🎨 Eclipse Formatter: eclipse-format.xml (タブ設定)"
echo "   🎨 スペース→タブ変換: 自動実行"
echo "   • 静的解析: Checkstyle, PMD, SpotBugs"
echo "   • 除外ブランチ: main, master, develop, release/*, hotfix/*"
echo ""
echo "🧪 テスト実行:"
echo "   .git/hooks/pre-commit  # 手動テスト"
echo "   cd $DRONE_DIR && ./format-and-check.sh  # 統合フォーマットテスト"
echo ""
echo "🔧 カスタマイズ:"
echo "   除外ブランチを変更: scripts/hooks/pre-commit 内の SKIP_BRANCHES 配列"
echo "   フォーマット設定: $DRONE_DIR/.prettierrc, $DRONE_DIR/eclipse-format.xml"
echo ""
echo "🎉 統合フォーマット環境対応 プリコミットフック セットアップ完了！"
echo "    コミット時に統合フォーマット + 品質チェックが自動実行されます"
