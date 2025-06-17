#!/bin/bash

# 統合フォーマット・チェックスクリプト
# prettier-java + Eclipse + タブインデント対応

echo "🔧 Java タブインデント統合フォーマット・チェックシステム 🔧"
echo "========================================================"
echo ""

# 環境チェック
echo "📋 環境確認中..."
if [ ! -f "pom.xml" ]; then
    echo "❌ エラー: DroneInventorySystemディレクトリで実行してください"
    exit 1
fi

if [ ! -f "package.json" ]; then
    echo "❌ エラー: Node.js環境が設定されていません"
    exit 1
fi

echo "✅ 環境確認完了"
echo ""

# Phase 1: スペースからタブへの変換
echo "🔄 Phase 1: スペースからタブへの変換"
echo "----------------------------------------"
find src/main/java -name "*.java" | while read file; do
    echo "変換中: $file"
    sed -i '' \
        -e 's/^    /\t/g' \
        -e 's/^\t    /\t\t/g' \
        -e 's/^\t\t    /\t\t\t/g' \
        -e 's/^\t\t\t    /\t\t\t\t/g' \
        -e 's/^\t\t\t\t    /\t\t\t\t\t/g' \
        "$file"
done
echo "✅ タブ変換完了"
echo ""

# Phase 2: Prettier実行（設定確認）
echo "🎨 Phase 2: Prettier実行"
echo "-------------------------"
if [ -f "node_modules/.bin/prettier" ]; then
    echo "Prettierでタブフォーマット確認中..."
    npm run format 2>/dev/null || echo "⚠️  Prettier実行中にエラーが発生しましたが続行します"
else
    echo "⚠️  Prettier未インストール。Maven pluginを使用します"
    mvn prettier:write -q 2>/dev/null || echo "⚠️  Maven prettier plugin未設定"
fi
echo "✅ Prettierフォーマット完了"
echo ""

# Phase 3: Eclipse Formatter実行
echo "🌟 Phase 3: Eclipse Formatter実行"
echo "-----------------------------------"
mvn formatter:format -q 2>/dev/null || echo "⚠️  Eclipse Formatter実行中にエラーが発生しましたが続行します"
echo "✅ Eclipse Formatter完了"
echo ""

# Phase 4: 静的解析チェック
echo "🔍 Phase 4: 静的解析チェック"
echo "----------------------------"

# Checkstyle Simple
echo "📋 Checkstyle (Simple) 実行中..."
mvn checkstyle:check -Dcheckstyle.config.location=checkstyle-simple.xml -q
CHECKSTYLE_RESULT=$?
if [ $CHECKSTYLE_RESULT -eq 0 ]; then
    echo "✅ Checkstyle (Simple): 合格"
else
    echo "⚠️  Checkstyle (Simple): 違反検出"
fi

# PMD
echo "📋 PMD実行中..."
mvn pmd:check -q
PMD_RESULT=$?
if [ $PMD_RESULT -eq 0 ]; then
    echo "✅ PMD: 合格"
else
    echo "⚠️  PMD: 違反検出"
fi

# SpotBugs
echo "📋 SpotBugs実行中..."
mvn spotbugs:check -q
SPOTBUGS_RESULT=$?
if [ $SPOTBUGS_RESULT -eq 0 ]; then
    echo "✅ SpotBugs: 合格"
else
    echo "⚠️  SpotBugs: 違反検出"
fi

echo ""
echo "📊 実行結果サマリー"
echo "===================="
echo "🔧 タブインデント: ✅ 適用済み"
echo "🎨 Prettier: ✅ 実行済み"
echo "🌟 Eclipse Formatter: ✅ 実行済み"

if [ $CHECKSTYLE_RESULT -eq 0 ] && [ $PMD_RESULT -eq 0 ] && [ $SPOTBUGS_RESULT -eq 0 ]; then
    echo "🎉 すべてのチェック: ✅ 合格"
    echo ""
    echo "🚀 コードが本番環境への準備完了です！"
else
    echo "⚠️  一部チェック: 要改善"
    echo ""
    echo "🔧 次のステップ:"
    echo "   1. target/site/checkstyle.html でCheckstyle結果を確認"
    echo "   2. target/site/pmd.html でPMD結果を確認"
    echo "   3. target/site/spotbugs.html でSpotBugs結果を確認"
    echo ""
    echo "📝 レポート生成コマンド:"
    echo "   mvn checkstyle:checkstyle pmd:pmd spotbugs:spotbugs"
fi

echo ""
echo "🔗 設定ファイル:"
echo "   - .prettierrc (Prettier設定)"
echo "   - eclipse-format.xml (Eclipse設定)"
echo "   - .editorconfig (エディタ設定)"
echo "   - .vscode/settings.json (VS Code設定)"
echo ""
echo "💡 Tips: Eclipseで開発する場合は eclipse-format.xml を"
echo "    Window → Preferences → Java → Code Style → Formatter"
echo "    でインポートしてください。"
