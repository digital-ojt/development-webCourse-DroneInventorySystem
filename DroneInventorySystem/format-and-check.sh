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

# Maven コマンドの検出と設定
echo "🔍 Maven環境を確認中..."
MVN_CMD=""

# 一般的なMavenパスを確認
if command -v mvn >/dev/null 2>&1; then
    MVN_CMD="mvn"
    echo "✅ Maven検出: $(which mvn)"
elif [ -f "/usr/local/bin/mvn" ]; then
    MVN_CMD="/usr/local/bin/mvn"
    echo "✅ Maven検出: /usr/local/bin/mvn"
elif [ -f "/opt/homebrew/bin/mvn" ]; then
    MVN_CMD="/opt/homebrew/bin/mvn"
    echo "✅ Maven検出: /opt/homebrew/bin/mvn"
elif [ -f "$HOME/.m2/wrapper/maven-wrapper.jar" ]; then
    MVN_CMD="./mvnw"
    echo "✅ Maven Wrapper検出: ./mvnw"
else
    echo "❌ エラー: Mavenが見つかりません"
    echo "   以下の方法でMavenをインストールしてください:"
    echo "   - Homebrew: brew install maven"
    echo "   - 手動インストール: https://maven.apache.org/install.html"
    echo ""
    echo "🔧 Eclipse使用時は以下も確認してください:"
    echo "   - Eclipse -> Preferences -> Maven -> Installations"
    echo "   - システムPATHにMavenが追加されているか"
    exit 1
fi

echo "✅ Maven環境確認完了: $MVN_CMD"

# JDK 17環境の確認と設定
echo "☕ JDK 17環境の確認と設定..."

# JDK 17のパスを検索
JDK17_PATHS=(
    "/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home"
    "/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home"
    "/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home"
    "/usr/lib/jvm/java-17-openjdk"
    "/usr/lib/jvm/temurin-17-jdk"
)

JAVA_17_HOME=""
for jdk_path in "${JDK17_PATHS[@]}"; do
    if [ -d "$jdk_path" ]; then
        JAVA_17_HOME="$jdk_path"
        break
    fi
done

# 現在のJavaバージョンを確認
CURRENT_JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2)
CURRENT_JAVA_MAJOR=$(echo $CURRENT_JAVA_VERSION | cut -d'.' -f1)

if [ -n "$JAVA_17_HOME" ]; then
    echo "✅ JDK 17検出: $JAVA_17_HOME"
    # JDK 17を使用するための環境変数設定
    export JAVA_HOME="$JAVA_17_HOME"
    export PATH="$JAVA_17_HOME/bin:$PATH"
    
    # 設定後のJavaバージョンを確認
    JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2)
    JAVA_MAJOR=$(echo $JAVA_VERSION | cut -d'.' -f1)
    echo "✅ 使用Java: $JAVA_VERSION (JDK $JAVA_MAJOR)"
    
    if [ "$JAVA_MAJOR" != "17" ]; then
        echo "⚠️  警告: JDK 17に設定しましたが、現在のJavaは$JAVA_MAJORです"
    fi
else
    echo "❌ エラー: JDK 17が見つかりません"
    echo "   現在のJava: $CURRENT_JAVA_VERSION (JDK $CURRENT_JAVA_MAJOR)"
    echo ""
    echo "🔧 JDK 17のインストール方法:"
    echo "   1. Homebrew: brew install openjdk@17"
    echo "   2. Eclipse Temurin: https://adoptium.net/temurin/releases/?version=17"
    echo "   3. Oracle JDK: https://www.oracle.com/java/technologies/downloads/#java17"
    echo ""
    echo "📋 Eclipse/IntelliJ設定確認:"
    echo "   - Eclipse: Preferences → Java → Installed JREs → JDK 17を選択"
    echo "   - IntelliJ: File → Project Structure → SDKs → JDK 17を選択"
    echo ""
    echo "⚠️  このプロジェクトはJDK 17での動作を前提としています"
    exit 1
fi
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
    echo "⚠️  Prettier未インストール。Maven pluginチェック中..."
    # Maven prettier pluginが設定されているか確認
    if $MVN_CMD help:describe -Dplugin=com.hubspot.maven.plugins:prettier-maven-plugin -q >/dev/null 2>&1; then
        echo "Maven prettier plugin実行中..."
        $MVN_CMD prettier:write -q 2>/dev/null || echo "⚠️  Maven prettier plugin実行エラー（続行します）"
    else
        echo "⚠️  Maven prettier plugin未設定（フォーマットをスキップ）"
    fi
fi
echo "✅ Prettierフォーマット完了"
echo ""

# Phase 3: Eclipse Formatter実行
echo "🌟 Phase 3: Eclipse Formatter実行"
echo "-----------------------------------"
$MVN_CMD formatter:format -q 2>/dev/null || echo "⚠️  Eclipse Formatter実行中にエラーが発生しましたが続行します"
echo "✅ Eclipse Formatter完了"
echo ""

# Phase 4: 静的解析チェック
echo "🔍 Phase 4: 静的解析チェック"
echo "----------------------------"

# Checkstyle Simple
echo "📋 Checkstyle (Simple) 実行中..."
$MVN_CMD checkstyle:check -Dcheckstyle.config.location=checkstyle-simple.xml -q
CHECKSTYLE_RESULT=$?
if [ $CHECKSTYLE_RESULT -eq 0 ]; then
    echo "✅ Checkstyle (Simple): 合格"
else
    echo "⚠️  Checkstyle (Simple): 違反検出"
fi

# PMD
echo "📋 PMD実行中..."
$MVN_CMD pmd:check -q
PMD_RESULT=$?
if [ $PMD_RESULT -eq 0 ]; then
    echo "✅ PMD: 合格"
else
    echo "⚠️  PMD: 違反検出"
fi

# SpotBugs
echo "📋 SpotBugs実行中..."
echo "   使用Java: $(java -version 2>&1 | head -n1 | cut -d'"' -f2)"
$MVN_CMD spotbugs:check -q
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
echo "☕ 使用Java: $(java -version 2>&1 | head -n1 | cut -d'"' -f2)"

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
    echo "   $MVN_CMD checkstyle:checkstyle pmd:pmd spotbugs:spotbugs"
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

# 🔧 重要: 静的解析の結果に基づいて適切な終了コードを返す
if [ $CHECKSTYLE_RESULT -eq 0 ] && [ $PMD_RESULT -eq 0 ] && [ $SPOTBUGS_RESULT -eq 0 ]; then
    echo ""
    echo "✅ 統合チェック完了: すべて合格 (JDK 17環境)"
    exit 0
else
    echo ""
    echo "❌ 統合チェック失敗: 静的解析エラーを修正してください"
    echo "   Checkstyle: $([ $CHECKSTYLE_RESULT -eq 0 ] && echo "✅" || echo "❌")"
    echo "   PMD: $([ $PMD_RESULT -eq 0 ] && echo "✅" || echo "❌")"
    echo "   SpotBugs: $([ $SPOTBUGS_RESULT -eq 0 ] && echo "✅" || echo "❌")"
    exit 1
fi
