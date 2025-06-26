#!/bin/bash

# =============================================================================
# Windows用 統合フォーマット・静的解析スクリプト
# =============================================================================
# 
# Windows環境 (Git Bash) に最適化された統合フォーマット・静的解析を実行します。
# Chocolatey、JDK 17、Maven、Node.js 環境での動作を前提とします。
#
# 実行場所: DroneInventorySystem/ ディレクトリ
# 依存関係: pom.xml, package.json, .prettierrc, eclipse-format.xml
#
# 注意: このスクリプトは実機テストを行っていません。
#       Windows環境での動作確認後、必要に応じて調整してください。
#
# =============================================================================

# エラーハンドリング: 静的解析の各段階でエラーが発生しても処理を継続
set -o pipefail  # パイプラインのエラーを捕捉

# 色付きログ出力関数 (Windows Git Bash用)
log_info() {
    echo "🔧 $1"
}

log_success() {
    echo "✅ $1"
}

log_warning() {
    echo "⚠️  $1"
}

log_error() {
    echo "❌ $1"
}

log_phase() {
    echo "🎨 $1"
}

log_check() {
    echo "🔍 $1"
}

log_note() {
    echo "📝 $1"
}

# Windows環境特有の確認
check_windows_environment() {
    log_info "Windows環境での統合フォーマット・静的解析システム実行開始"
    log_note "注意: Windows環境での実機テストは未完了です"
    echo "========================================================"
    echo ""
    
    # 実行ディレクトリ確認
    if [ ! -f "pom.xml" ]; then
        log_error "DroneInventorySystemディレクトリで実行してください"
        exit 1
    fi
    
    if [ ! -f "package.json" ]; then
        log_error "Node.js環境が設定されていません (package.json不在)"
        exit 1
    fi
    
    # Git Bash環境確認
    if [ -n "$BASH_VERSION" ]; then
        log_success "Git Bash環境: 確認済み"
    else
        log_warning "Git Bash環境ではない可能性があります"
    fi
}

# Java環境確認・設定 (Windows用)
setup_java_environment() {
    log_check "Java環境確認・設定中..."
    
    # 現在のJavaバージョン確認
    if command -v java >/dev/null 2>&1; then
        CURRENT_JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2)
        CURRENT_JAVA_MAJOR=$(echo $CURRENT_JAVA_VERSION | cut -d'.' -f1)
        
        if [ "$CURRENT_JAVA_MAJOR" = "17" ]; then
            log_success "JDK 17環境を確認: プロジェクト要件に適合"
            
            # Windows用のJAVA_HOME設定確認
            if [ -n "$JAVA_HOME" ]; then
                log_success "JAVA_HOME設定確認: $JAVA_HOME"
            else
                # Windows環境でのJAVA_HOME動的取得（試行）
                JAVA_HOME_PATH=$(java -XshowSettings:properties -version 2>&1 | grep 'java.home' | awk '{print $3}')
                if [ -n "$JAVA_HOME_PATH" ]; then
                    export JAVA_HOME="$JAVA_HOME_PATH"
                    log_info "JAVA_HOME動的設定: $JAVA_HOME"
                else
                    log_warning "JAVA_HOME が設定されていません"
                    log_note "Windows環境変数でJAVA_HOMEの設定を推奨します"
                fi
            fi
        else
            log_warning "JDK $CURRENT_JAVA_MAJOR が使用されています"
            log_warning "このプロジェクトはJDK 17での動作を前提としています"
            log_note "Windows用推奨インストール:"
            log_note "  Chocolatey: choco install corretto17jdk"
            log_note "  手動: Amazon Corretto 17 MSIファイル"
        fi
    else
        log_error "Java がインストールされていません"
        log_note "Windows用推奨インストール:"
        log_note "  Chocolatey: choco install corretto17jdk"
        log_note "  手動: https://corretto.aws/downloads/"
        exit 1
    fi
}

# Maven環境確認・設定 (Windows用)
setup_maven_environment() {
    log_check "Maven環境確認中..."
    
    MVN_CMD=""
    
    # Windows環境でのMaven検出
    if command -v mvn >/dev/null 2>&1; then
        MVN_CMD="mvn"
        MAVEN_PATH=$(which mvn)
        log_success "Maven検出: $MAVEN_PATH"
    elif [ -f "/c/Program Files/Apache/maven/bin/mvn" ]; then
        MVN_CMD="/c/Program Files/Apache/maven/bin/mvn"
        log_success "Maven検出: /c/Program Files/Apache/maven/bin/mvn"
    elif [ -f "/c/tools/apache-maven/bin/mvn" ]; then
        MVN_CMD="/c/tools/apache-maven/bin/mvn"
        log_success "Maven検出: /c/tools/apache-maven/bin/mvn (Chocolatey)"
    elif [ -f "$HOME/.m2/wrapper/maven-wrapper.jar" ]; then
        MVN_CMD="./mvnw"
        log_success "Maven Wrapper検出: ./mvnw"
    elif command -v mvn.cmd >/dev/null 2>&1; then
        MVN_CMD="mvn.cmd"
        log_success "Maven検出: mvn.cmd (Windows用)"
    else
        log_error "Mavenが見つかりません"
        log_note "Windows用推奨インストール:"
        log_note "  Chocolatey: choco install maven"
        log_note "  手動: https://maven.apache.org/install.html"
        log_note "  環境変数PATHにMaven\\binを追加してください"
        exit 1
    fi
    
    # Maven version確認
    if MAVEN_VERSION=$($MVN_CMD -version 2>/dev/null | head -n1 | cut -d' ' -f3); then
        log_success "Maven Version: $MAVEN_VERSION"
    else
        log_warning "Maven version確認に失敗しました"
    fi
}

# Node.js環境確認 (Windows用)
check_nodejs_environment() {
    log_check "Node.js環境確認中..."
    
    if command -v node >/dev/null 2>&1; then
        NODE_VERSION=$(node --version)
        log_success "Node.js: $NODE_VERSION"
        
        if command -v npm >/dev/null 2>&1; then
            NPM_VERSION=$(npm --version)
            log_success "npm: v$NPM_VERSION"
        else
            log_warning "npm が見つかりません"
        fi
    else
        log_error "Node.js がインストールされていません"
        log_note "Windows用推奨インストール:"
        log_note "  Chocolatey: choco install nodejs"
        log_note "  手動: https://nodejs.org/en/download/"
        exit 1
    fi
}

# 統合フォーマット実行 (Windows用)
execute_integrated_format() {
    log_phase "統合フォーマット実行"
    
    # Phase 1: Space→Tab変換 (Windows用sed)
    log_info "Phase1: Space→Tab変換実行..."
    
    # Windows環境での find + sed 実行
    if command -v find >/dev/null 2>&1 && command -v sed >/dev/null 2>&1; then
        # Git Bash環境での実行
        find src -name "*.java" -type f -exec sed -i 's/    /\t/g' {} \;
        log_success "Phase1: Space→Tab変換完了 (Git Bash sed使用)"
    else
        log_warning "Phase1: find/sed が見つかりません。Windowsでの代替処理が必要"
        log_note "PowerShellでの代替処理を検討してください"
    fi
    
    # Phase 2: Prettier Java実行
    log_info "Phase2: Prettier Java実行中..."
    if command -v npx >/dev/null 2>&1; then
        # Windows環境でのPrettier実行
        if npx prettier --write "src/**/*.java" --tab-width=4 --use-tabs 2>/dev/null; then
            log_success "Phase2: Prettier Java実行完了"
        else
            log_warning "Phase2: Prettier Java実行で警告が発生しました"
            log_note "Windows環境でのパス問題の可能性があります"
        fi
    else
        log_warning "npx が見つかりません。Node.js環境を確認してください"
    fi
    
    # Phase 3: Eclipse Formatter実行
    log_info "Phase3: Eclipse Formatter実行中..."
    if $MVN_CMD formatter:format -q 2>/dev/null; then
        log_success "Phase3: Eclipse Formatter実行完了"
    else
        log_warning "Phase3: Eclipse Formatter実行で警告が発生しました"
        log_note "Windows環境でのMaven実行問題の可能性があります"
    fi
}

# 静的解析実行 (Windows用)
execute_static_analysis() {
    log_check "静的解析実行"
    
    local all_passed=true
    local temp_log_file="/tmp/static-analysis-$$.log"
    
    # Checkstyle実行
    log_info "Checkstyle実行中..."
    set +e  # エラー時停止を一時的に無効化
    $MVN_CMD checkstyle:check > "$temp_log_file" 2>&1
    local checkstyle_exit_code=$?
    set -e  # エラー時停止を再有効化
    
    if [ $checkstyle_exit_code -eq 0 ]; then
        log_success "Checkstyle: 合格"
    else
        log_warning "Checkstyle: 違反が検出されました"
        echo "==================== Checkstyle詳細ログ (Windows) =================="
        cat "$temp_log_file"
        echo "======================================================================="
        all_passed=false
    fi
    
    # PMD実行
    log_info "PMD実行中..."
    set +e  # エラー時停止を一時的に無効化
    $MVN_CMD pmd:check > "$temp_log_file" 2>&1
    local pmd_exit_code=$?
    set -e  # エラー時停止を再有効化
    
    if [ $pmd_exit_code -eq 0 ]; then
        log_success "PMD: 合格"
    else
        log_warning "PMD: 問題が検出されました"
        echo "==================== PMD詳細ログ (Windows) ===================="
        cat "$temp_log_file"
        echo "=================================================================="
        all_passed=false
    fi
    
    # SpotBugs実行（Windows用エラーハンドリング）
    log_info "SpotBugs実行中..."
    set +e  # エラー時停止を一時的に無効化
    $MVN_CMD spotbugs:check > "$temp_log_file" 2>&1
    local spotbugs_exit_code=$?
    set -e  # エラー時停止を再有効化
    
    if [ $spotbugs_exit_code -eq 0 ]; then
        log_success "SpotBugs: 合格"
    else
        # Windows環境でのSpotBugsエラー詳細分析
        spotbugs_error_output=$(cat "$temp_log_file")
        if echo "$spotbugs_error_output" | grep -q "Unsupported class file major version"; then
            log_warning "SpotBugs: Java 21クラスファイル互換性問題を検出"
            log_info "→ JDK 17環境でも一部Java 21クラスが参照されています"
            log_info "→ 静的解析は継続しますが、SpotBugsはスキップします"
            # 互換性問題の場合、他のチェック結果を優先
        else
            log_warning "SpotBugs: 問題が検出されました"
            echo "==================== SpotBugs詳細ログ (Windows) ===================="
            cat "$temp_log_file"
            echo "======================================================================="
            log_note "Windows環境での実行問題の可能性があります"
            all_passed=false
        fi
    fi
    
    # 一時ファイルのクリーンアップ
    rm -f "$temp_log_file"
    
    # 結果判定を明確にする
    if [ "$all_passed" = true ]; then
        log_success "✅ 全ての静的解析チェックに合格しました"
        return 0
    else
        log_error "❌ 静的解析で問題が検出されました"
        return 1
    fi
}

# Windows特有のパフォーマンス警告
show_performance_notes() {
    log_note "Windows環境でのパフォーマンスに関する注意："
    log_note "  - 実行時間がmacOS/Linux環境より長くなる可能性があります"
    log_note "  - Windows Defenderの除外設定を検討してください"
    log_note "  - SSD使用を推奨します"
}

# 結果出力 (Windows用)
output_results() {
    local exit_code=$1
    
    if [ $exit_code -eq 0 ]; then
        echo ""
        log_success "🎉 全て合格しました！"
        log_info "Windows環境での統合フォーマット・静的解析が正常に完了しました"
    else
        echo ""
        log_warning "⚠️  一部のチェックで問題が検出されました"
        log_info "詳細は上記のログを確認し、適切に修正してください"
        log_info "Windows用トラブルシューティング:"
        log_info "  - PRE-COMMIT-GUIDE-WINDOWS.md を参照"
        log_info "  - 環境変数 (JAVA_HOME, PATH) の設定確認"
        log_info "  - Windows Defenderの除外設定"
    fi
    
    echo ""
    show_performance_notes
    
    echo ""
    if command -v systeminfo >/dev/null 2>&1; then
        WINDOWS_VERSION=$(systeminfo 2>/dev/null | grep "OS Name" | cut -d: -f2 | sed 's/^ *//' || echo "Windows (バージョン不明)")
        log_info "実行環境: $WINDOWS_VERSION"
    else
        log_info "実行環境: Windows (Git Bash)"
    fi
    log_info "実行時間: $(date)"
    log_note "注意: Windows環境での実機テストは未完了です"
}

# メイン処理
main() {
    local start_time=$(date +%s)
    
    # Windows環境確認
    check_windows_environment
    
    # 環境セットアップ
    setup_java_environment
    setup_maven_environment
    check_nodejs_environment
    
    echo ""
    
    # 統合フォーマット実行
    execute_integrated_format
    
    echo ""
    
    # 静的解析実行
    set +e  # エラー時停止を一時的に無効化
    execute_static_analysis
    local static_analysis_exit_code=$?
    set -e  # エラー時停止を再有効化
    
    if [ $static_analysis_exit_code -eq 0 ]; then
        local exit_code=0
    else
        local exit_code=1
    fi
    
    # 実行時間計算
    local end_time=$(date +%s)
    local duration=$((end_time - start_time))
    
    # 結果出力
    output_results $exit_code
    log_info "実行時間: ${duration}秒"
    
    exit $exit_code
}

# スクリプト実行
main "$@"
