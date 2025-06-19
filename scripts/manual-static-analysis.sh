#!/bin/bash

# 静的解析手動実行ガイドスクリプト
# Pre-commitフック無効状態での段階的実行

echo "🔧 静的解析手動実行ガイド"
echo "========================================"
echo ""

# 作業ディレクトリ確認
echo "📁 作業ディレクトリ確認..."
if [ ! -d "DroneInventorySystem" ]; then
    echo "❌ DroneInventorySystemディレクトリが見つかりません"
    echo "   プロジェクトルートディレクトリで実行してください"
    exit 1
fi

cd DroneInventorySystem

# Pre-commitフック状態確認
echo "🔍 Pre-commitフック状態確認..."
if [ -f "../.git/hooks/pre-commit" ]; then
    echo "⚠️  Pre-commitフックが有効です"
    echo "   無効化しますか？ (y/n)"
    read -r response
    if [[ "$response" =~ ^[Yy]$ ]]; then
        mv ../.git/hooks/pre-commit ../.git/hooks/pre-commit.backup
        echo "✅ Pre-commitフックを一時的に無効化しました"
    fi
else
    echo "✅ Pre-commitフックは無効化済みです"
fi

echo ""

# 段階的実行関数
execute_phase() {
    local phase_num="$1"
    local phase_name="$2"
    local command="$3"
    local description="$4"
    
    echo "========================================"
    echo "Phase $phase_num: $phase_name"
    echo "========================================"
    echo "説明: $description"
    echo ""
    echo "実行コマンド: $command"
    echo ""
    echo "実行しますか？ [Enter]で続行、[s]でスキップ、[q]で終了:"
    read -r response
    
    case "$response" in
        "q"|"Q")
            echo "実行を終了します"
            exit 0
            ;;
        "s"|"S")
            echo "⏭️  Phase $phase_num をスキップしました"
            echo ""
            return
            ;;
        *)
            echo "🚀 Phase $phase_num 実行中..."
            echo ""
            eval "$command"
            local exit_code=$?
            echo ""
            if [ $exit_code -eq 0 ]; then
                echo "✅ Phase $phase_num 完了"
            else
                echo "⚠️  Phase $phase_num でエラーが発生しました（期待される場合があります）"
            fi
            echo ""
            echo "結果を確認して、次に進む準備ができたら [Enter] を押してください:"
            read -r
            ;;
    esac
}

# Phase 1: フォーマットチェック
execute_phase "1" "フォーマット状態確認" \
    "mvn fmt:check" \
    "現在のフォーマット状態を確認します。違反があれば一覧表示されます。"

# Phase 2: フォーマット実行
execute_phase "2" "自動フォーマット実行" \
    "mvn fmt:format" \
    "Google Java Formatを使用してコードを自動フォーマットします。"

# Phase 3: フォーマット結果確認
execute_phase "3" "フォーマット結果確認" \
    "git diff --name-only" \
    "フォーマットによる変更ファイルを確認します。"

# Phase 4: コンパイルチェック
execute_phase "4" "コンパイルチェック" \
    "mvn compile -DskipTests -q" \
    "Javaコードがコンパイル可能かチェックします。Lombokエラーが出る可能性があります。"

# Phase 5: 基本スタイルチェック
execute_phase "5" "基本スタイルチェック（警告レベル）" \
    "mvn checkstyle:check -Dcheckstyle.config.location=checkstyle-simple.xml" \
    "基本的なコーディング規約をチェックします。警告が表示されますが処理は継続されます。"

# Phase 6: 厳格スタイルチェック
execute_phase "6" "厳格スタイルチェック（エラーレベル）" \
    "mvn checkstyle:check -Dcheckstyle.config.location=checkstyle-strict.xml" \
    "厳格なコーディング規約をチェックします。エラーでBUILD FAILUREになることが期待されます。"

# Phase 7: PMD品質チェック
execute_phase "7" "PMD品質チェック" \
    "mvn pmd:check" \
    "コード品質と設計問題をチェックします。違反が検出される予定です。"

# Phase 8: SpotBugsチェック
execute_phase "8" "SpotBugsバグ検出" \
    "mvn spotbugs:check" \
    "潜在的なバグパターンを検出します。コンパイルエラーがある場合は実行できません。"

# Phase 9: レポート生成
execute_phase "9" "詳細レポート生成" \
    "mvn checkstyle:checkstyle pmd:pmd spotbugs:spotbugs" \
    "HTMLとXML形式の詳細レポートを生成します。"

# Phase 10: レポート確認
echo "========================================"
echo "Phase 10: 生成されたレポート確認"
echo "========================================"
echo ""
echo "📊 生成されたレポートファイル:"
echo ""

if [ -f "target/site/checkstyle.html" ]; then
    echo "✅ Checkstyle HTMLレポート: target/site/checkstyle.html"
else
    echo "❌ Checkstyle HTMLレポートが見つかりません"
fi

if [ -f "target/site/pmd.html" ]; then
    echo "✅ PMD HTMLレポート: target/site/pmd.html"
else
    echo "❌ PMD HTMLレポートが見つかりません"
fi

if [ -f "target/site/spotbugs.html" ]; then
    echo "✅ SpotBugs HTMLレポート: target/site/spotbugs.html"
else
    echo "❌ SpotBugs HTMLレポートが見つかりません"
fi

echo ""
echo "📋 XMLレポートファイル:"

if [ -f "target/checkstyle-result.xml" ]; then
    echo "✅ Checkstyle XMLレポート: target/checkstyle-result.xml"
else
    echo "❌ Checkstyle XMLレポートが見つかりません"
fi

if [ -f "target/pmd.xml" ]; then
    echo "✅ PMD XMLレポート: target/pmd.xml"
    echo "   📊 PMD違反数: $(grep -c 'violation' target/pmd.xml)"
else
    echo "❌ PMD XMLレポートが見つかりません"
fi

echo ""

# 結果サマリー
echo "========================================"
echo "🎯 実行結果サマリー"
echo "========================================"
echo ""
echo "以下のコマンドで詳細を確認できます："
echo ""
echo "📊 レポートブラウザで表示:"
echo "open target/site/checkstyle.html    # Checkstyle結果"
echo "open target/site/pmd.html           # PMD結果"
echo "open target/site/spotbugs.html      # SpotBugs結果"
echo ""
echo "📋 コンソールで確認:"
echo "cat target/checkstyle-result.xml    # Checkstyle詳細"
echo "cat target/pmd.xml                  # PMD詳細"
echo ""
echo "🔄 Pre-commitフック復旧:"
echo "mv ../.git/hooks/pre-commit.backup ../.git/hooks/pre-commit"
echo ""

# Git操作確認
echo "📝 Gitファイル操作を実行しますか？"
echo "[1] フォーマット済みファイルをステージング"
echo "[2] テストファイルをコミット"  
echo "[3] スキップ"
echo "選択してください (1-3):"
read -r git_choice

case "$git_choice" in
    "1")
        echo "📝 フォーマット済みファイルをステージング中..."
        git add .
        echo "✅ ステージング完了"
        git status
        ;;
    "2")
        echo "📝 フォーマット済みファイルをステージング中..."
        git add .
        echo "💾 テストファイルをコミット中..."
        git commit -m "Manual test: Static analysis failure test files (formatted)"
        echo "✅ コミット完了"
        git log --oneline -1
        ;;
    "3")
        echo "⏭️  Git操作をスキップしました"
        ;;
    *)
        echo "⏭️  無効な選択です。Git操作をスキップしました"
        ;;
esac

echo ""
echo "🎉 静的解析手動実行ガイド完了！"
echo ""
echo "📚 参考資料:"
echo "- MANUAL_STATIC_ANALYSIS_GUIDE.md: 詳細な手動実行ガイド"
echo "- STATIC_ANALYSIS_TEST_RESULTS.md: テスト結果レポート"
echo "- STATIC_ANALYSIS_FAILURE_TEST_GUIDE.md: テスト実行ガイド"
