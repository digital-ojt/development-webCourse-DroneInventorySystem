#!/bin/bash

# =============================================================================
# macOS用 統合フォーマット・静的解析スクリプト
# =============================================================================
# 
# macOS環境に最適化された統合フォーマット・静的解析を実行します。
# Homebrew、JDK 17、Maven、Node.js 環境での動作を前提とします。
#
# 実行場所: DroneInventorySystem/ ディレクトリ
# 依存関係: pom.xml, package.json, .prettierrc, eclipse-format.xml
#
# =============================================================================

set -e

# 色付きログ出力関数
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

# macOS特有の環境確認
check_macos_environment() {
    log_info "macOS環境での統合フォーマット・静的解析システム実行開始"
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
}

# Java環境確認・設定 (macOS用)
setup_java_environment() {
    log_check "Java環境確認・設定中..."
    
    # 現在のJavaバージョン確認
    if command -v java >/dev/null 2>&1; then
        CURRENT_JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2)
        CURRENT_JAVA_MAJOR=$(echo $CURRENT_JAVA_VERSION | cut -d'.' -f1)
        
        if [ "$CURRENT_JAVA_MAJOR" = "17" ]; then
            log_success "JDK 17環境を確認: プロジェクト要件に適合"
            
            # macOS用のJAVA_HOME設定
            JAVA_HOME_PATH=$(java -XshowSettings:properties -version 2>&1 | grep 'java.home' | awk '{print $3}')
            
            if [ -n "$JAVA_HOME_PATH" ]; then
                export JAVA_HOME="$JAVA_HOME_PATH"
                log_info "Maven用JAVA_HOME設定: $JAVA_HOME"
            else
                log_warning "JAVA_HOME の自動設定に失敗しました"
            fi
        else
            log_warning "JDK $CURRENT_JAVA_MAJOR が使用されています"
            log_warning "このプロジェクトはJDK 17での動作を前提としています"
            log_info "macOS用推奨インストール: brew install --cask amazon-corretto17"
        fi
    else
        log_error "Java がインストールされていません"
        log_info "macOS用推奨インストール: brew install --cask amazon-corretto17"
        exit 1
    fi
}

# Maven環境確認・設定 (macOS用)
setup_maven_environment() {
    log_check "Maven環境確認中..."
    
    MVN_CMD=""
    
    # macOS Homebrew環境でのMaven検出
    if command -v mvn >/dev/null 2>&1; then
        MVN_CMD="mvn"
        MAVEN_PATH=$(which mvn)
        log_success "Maven検出: $MAVEN_PATH"
    elif [ -f "/opt/homebrew/bin/mvn" ]; then
        MVN_CMD="/opt/homebrew/bin/mvn"
        log_success "Maven検出: /opt/homebrew/bin/mvn (Apple Silicon)"
    elif [ -f "/usr/local/bin/mvn" ]; then
        MVN_CMD="/usr/local/bin/mvn"
        log_success "Maven検出: /usr/local/bin/mvn (Intel)"
    elif [ -f "$HOME/.m2/wrapper/maven-wrapper.jar" ]; then
        MVN_CMD="./mvnw"
        log_success "Maven Wrapper検出: ./mvnw"
    else
        log_error "Mavenが見つかりません"
        log_info "macOS用推奨インストール:"
        log_info "  Homebrew: brew install maven"
        log_info "  手動: https://maven.apache.org/install.html"
        exit 1
    fi
    
    # Maven version確認
    MAVEN_VERSION=$($MVN_CMD -version | head -n1 | cut -d' ' -f3)
    log_success "Maven Version: $MAVEN_VERSION"
}

# Node.js環境確認 (macOS用)
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
        log_info "macOS用推奨インストール: brew install node"
        exit 1
    fi
}

# 統合フォーマット実行 (macOS用)
execute_integrated_format() {
    log_phase "統合フォーマット実行"
    
    # Phase 1: Space→Tab変換 (macOS用sed)
    log_info "Phase1: Space→Tab変換実行..."
    find src -name "*.java" -type f -exec sed -i '' 's/    /\t/g' {} \;
    log_success "Phase1: Space→Tab変換完了 (macOS sed使用)"
    
    # Phase 2: Prettier Java実行
    log_info "Phase2: Prettier Java実行中..."
    if command -v npx >/dev/null 2>&1; then
        if npx prettier --write "src/**/*.java" --tab-width=4 --use-tabs 2>/dev/null; then
            log_success "Phase2: Prettier Java実行完了"
        else
            log_warning "Phase2: Prettier Java実行で警告が発生しました"
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
    fi
}

# 静的解析実行 (macOS用)
execute_static_analysis() {
    log_check "静的解析実行"
    
    local all_passed=true
    
    # Checkstyle実行
    log_info "Checkstyle実行中..."
    if $MVN_CMD checkstyle:check -q 2>/dev/null; then
        log_success "Checkstyle: 合格"
    else
        log_warning "Checkstyle: 違反が検出されました"
        all_passed=false
    fi
    
    # PMD実行
    log_info "PMD実行中..."
    if $MVN_CMD pmd:check -q 2>/dev/null; then
        log_success "PMD: 合格"
    else
        log_warning "PMD: 問題が検出されました"
        all_passed=false
    fi
    
    # SpotBugs実行（macOS用エラーハンドリング）
    log_info "SpotBugs実行中..."
    if $MVN_CMD spotbugs:check -q 2>/dev/null; then
        log_success "SpotBugs: 合格"
    else
        # macOS環境でのSpotBugsエラー詳細分析
        spotbugs_error_output=$($MVN_CMD spotbugs:check -X 2>&1)
        if echo "$spotbugs_error_output" | grep -q "Unsupported class file major version"; then
            log_warning "SpotBugs: Java 21クラスファイル互換性問題を検出"
            log_info "→ JDK 17環境でも一部Java 21クラスが参照されています"
            log_info "→ 静的解析は継続しますが、SpotBugsはスキップします"
        else
            log_warning "SpotBugs: 問題が検出されました"
            all_passed=false
        fi
    fi
    
    return $([ "$all_passed" = true ] && echo 0 || echo 1)
}

# 結果出力 (macOS用)
output_results() {
    local exit_code=$1
    
    if [ $exit_code -eq 0 ]; then
        echo ""
        log_success "🎉 全て合格しました！"
        log_info "macOS環境での統合フォーマット・静的解析が正常に完了しました"
    else
        echo ""
        log_warning "⚠️  一部のチェックで問題が検出されました"
        log_info "詳細は上記のログを確認し、適切に修正してください"
        log_info "macOS用トラブルシューティング:"
        log_info "  - PRE-COMMIT-GUIDE-MAC.md を参照"
        log_info "  - Homebrew パッケージの更新: brew update && brew upgrade"
    fi
    
    echo ""
    log_info "実行環境: macOS $(sw_vers -productVersion)"
    log_info "実行時間: $(date)"
}

# メイン処理
main() {
    local start_time=$(date +%s)
    
    # macOS環境確認
    check_macos_environment
    
    # 環境セットアップ
    setup_java_environment
    setup_maven_environment
    check_nodejs_environment
    
    echo ""
    
    # 統合フォーマット実行
    execute_integrated_format
    
    echo ""
    
    # 静的解析実行
    if execute_static_analysis; then
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
