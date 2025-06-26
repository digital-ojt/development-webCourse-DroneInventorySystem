#!/bin/bash

# =============================================================================
# macOS用 統合静的解析システム セットアップスクリプト
# =============================================================================
# 
# macOS (Intel/Apple Silicon) 環境に特化したセットアップを実行します。
# Homebrew、JDK、Maven、Node.js などの環境確認・セットアップを行います。
#
# 前提条件:
#   - Homebrew がインストール済み
#   - Git がインストール済み
#   - JDK 17 (推奨: Amazon Corretto)
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

# Homebrew確認・インストール関数
check_homebrew() {
    if command -v brew >/dev/null 2>&1; then
        log_success "Homebrew: インストール済み ($(brew --version | head -n1))"
        return 0
    else
        log_warning "Homebrew がインストールされていません"
        log_info "Homebrewをインストールしてください:"
        log_info '/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"'
        return 1
    fi
}

# JDK 17確認・推奨インストール
check_java() {
    if command -v java >/dev/null 2>&1; then
        JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2)
        JAVA_MAJOR=$(echo $JAVA_VERSION | cut -d'.' -f1)
        
        if [ "$JAVA_MAJOR" = "17" ]; then
            log_success "Java 17: インストール済み ($JAVA_VERSION)"
            return 0
        else
            log_warning "Java $JAVA_MAJOR が検出されました。JDK 17を推奨します"
            log_info "Amazon Corretto 17のインストール:"
            log_info "brew install --cask amazon-corretto17"
            return 1
        fi
    else
        log_warning "Java がインストールされていません"
        log_info "Amazon Corretto 17のインストール:"
        log_info "brew install --cask amazon-corretto17"
        return 1
    fi
}

# Maven確認・インストール
check_maven() {
    if command -v mvn >/dev/null 2>&1; then
        MAVEN_VERSION=$(mvn --version | head -n1 | cut -d' ' -f3)
        log_success "Maven: インストール済み ($MAVEN_VERSION)"
        return 0
    else
        log_warning "Maven がインストールされていません"
        log_info "Mavenのインストール:"
        log_info "brew install maven"
        return 1
    fi
}

# Node.js確認・インストール
check_nodejs() {
    if command -v node >/dev/null 2>&1; then
        NODE_VERSION=$(node --version)
        log_success "Node.js: インストール済み ($NODE_VERSION)"
        return 0
    else
        log_warning "Node.js がインストールされていません"
        log_info "Node.jsのインストール:"
        log_info "brew install node"
        return 1
    fi
}

# 統合フォーマット環境確認
check_integrated_format() {
    log_info "統合フォーマット環境の確認..."
    DRONE_DIR="DroneInventorySystem"
    
    local format_ok=true
    
    if [ -f "$DRONE_DIR/config/format-and-check.sh" ]; then
        log_success "統合フォーマットスクリプト: $DRONE_DIR/config/format-and-check.sh"
    else
        log_warning "統合フォーマットスクリプトが見つかりません"
        format_ok=false
    fi
    
    if [ -f "$DRONE_DIR/package.json" ] && [ -f "$DRONE_DIR/.prettierrc" ]; then
        log_success "Prettier Java環境: package.json + .prettierrc"
    else
        log_warning "Prettier Java環境が不完全です"
        format_ok=false
    fi
    
    if [ -f "$DRONE_DIR/config/eclipse-format.xml" ]; then
        log_success "Eclipse Formatter設定: config/eclipse-format.xml"
    else
        log_warning "Eclipse Formatter設定が見つかりません"
        format_ok=false
    fi
    
    return $([ "$format_ok" = true ] && echo 0 || echo 1)
}

# pre-commitフックセットアップ
setup_precommit_hook() {
    log_info "pre-commitフックのセットアップ..."
    
    # 既存のフックをバックアップ
    if [ -f ".git/hooks/pre-commit" ]; then
        backup_file=".git/hooks/pre-commit.backup.$(date +%Y%m%d_%H%M%S)"
        cp .git/hooks/pre-commit "$backup_file"
        log_info "既存フックをバックアップ: $backup_file"
    fi
    
    # Mac用pre-commitフックをコピー
    if [ -f "scripts/core/mac/pre-commit" ]; then
        cp scripts/core/mac/pre-commit .git/hooks/pre-commit
        chmod +x .git/hooks/pre-commit
        log_success "Mac用pre-commitフック: インストール完了"
    else
        log_error "Mac用pre-commitフックが見つかりません"
        return 1
    fi
}

# .zshrc環境設定（macOS用）
setup_zsh_environment() {
    log_info "macOS用環境設定の確認..."
    
    # .zshrcの存在確認
    if [ -f "$HOME/.zshrc" ]; then
        # JAVA_HOME設定の確認
        if grep -q "JAVA_HOME.*corretto" "$HOME/.zshrc" 2>/dev/null; then
            log_success "JAVA_HOME設定: 既に設定済み"
        else
            log_info "JAVA_HOME設定を.zshrcに追加することを推奨します"
            log_info 'echo "export JAVA_HOME=/Library/Java/JavaVirtualMachines/amazon-corretto-17.jdk/Contents/Home" >> ~/.zshrc'
        fi
    else
        log_info ".zshrcファイルが存在しません。必要に応じて作成してください"
    fi
}

# Prettier依存関係インストール
install_prettier_dependencies() {
    log_info "Prettier Java依存関係のインストール..."
    
    cd DroneInventorySystem
    
    if [ -f "package.json" ]; then
        if command -v npm >/dev/null 2>&1; then
            npm install
            log_success "Prettier Java依存関係: インストール完了"
        else
            log_warning "npm が見つかりません。Node.jsをインストールしてください"
            return 1
        fi
    else
        log_warning "package.json が見つかりません"
        return 1
    fi
    
    cd ..
}

# テスト実行
run_test() {
    log_info "セットアップテストを実行中..."
    
    # 統合フォーマットスクリプトのテスト
    cd DroneInventorySystem
    if [ -f "config/format-and-check.sh" ]; then
        chmod +x config/format-and-check.sh
        log_info "統合フォーマット・静的解析テスト実行..."
        if ./config/format-and-check.sh; then
            log_success "統合テスト: 合格"
        else
            log_warning "統合テスト: 一部警告あり（通常の動作です）"
        fi
    fi
    cd ..
}

# ガイドファイル生成
generate_guide() {
    log_info "macOS用ガイドファイルを生成中..."
    
    cat > PRE-COMMIT-GUIDE-MAC.md << 'EOF'
# macOS用 統合静的解析システム 運用ガイド

## セットアップ完了後の確認

### 1. 環境確認
```bash
# Java環境確認
java -version
# 期待値: openjdk version "17.x.x"

# Maven確認  
mvn -version
# 期待値: Apache Maven 3.x.x

# Node.js確認
node --version
# 期待値: v18.x.x 以上
```

### 2. IDE別設定

#### Eclipse IDE
- **エラー確認**: Package Explorer で `pre-commit-result.txt` を開く
- **手動実行**: Run Configurations で External Tools設定

#### IntelliJ IDEA  
- **エラー確認**: Project toolwindow で `pre-commit-result.txt` を開く
- **手動実行**: Terminal タブで `./DroneInventorySystem/config/format-and-check.sh`

#### VS Code
- **エラー確認**: Explorer で `pre-commit-result.txt` を開く  
- **手動実行**: 統合Terminal で `./DroneInventorySystem/config/format-and-check.sh`

### 3. トラブルシューティング

#### Java環境問題
```bash
# JAVA_HOME設定 (Corretto 17の場合)
export JAVA_HOME=/Library/Java/JavaVirtualMachines/amazon-corretto-17.jdk/Contents/Home
echo $JAVA_HOME
```

#### Homebrew パッケージ更新
```bash
# 推奨パッケージの最新化
brew update
brew upgrade maven node
```

#### 権限問題
```bash
# スクリプト実行権限付与
chmod +x DroneInventorySystem/config/format-and-check.sh
chmod +x .git/hooks/pre-commit
```

### 4. 継続的な保守

- **月次**: `brew update && brew upgrade` でツール更新
- **プロジェクト変更時**: 設定ファイルの見直し
- **新規メンバー参入時**: このガイドの提供

---
*macOS用統合静的解析システム by development-webCourse-DroneInventorySystem*
EOF

    log_success "macOS用ガイド: PRE-COMMIT-GUIDE-MAC.md を生成"
}

# メイン処理
main() {
    log_info "macOS用 統合静的解析システム セットアップ開始"
    
    local setup_issues=0
    
    # 環境確認
    check_homebrew || ((setup_issues++))
    check_java || ((setup_issues++))
    check_maven || ((setup_issues++))
    check_nodejs || ((setup_issues++))
    
    # 環境に問題がある場合は警告
    if [ $setup_issues -gt 0 ]; then
        log_warning "$setup_issues 個の環境問題が検出されました"
        log_info "上記の推奨インストール手順を実行後、再度セットアップを実行してください"
        log_warning "セットアップを継続しますが、一部機能が制限される可能性があります"
        read -p "続行しますか? (y/n): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            log_info "セットアップを中断しました"
            exit 1
        fi
    fi
    
    # 統合フォーマット環境確認  
    check_integrated_format
    
    # pre-commitフックセットアップ
    setup_precommit_hook
    
    # 環境設定
    setup_zsh_environment
    
    # 依存関係インストール
    install_prettier_dependencies || log_warning "Prettier依存関係のインストールに失敗しました"
    
    # テスト実行
    run_test
    
    # ガイド生成
    generate_guide
    
    log_success "macOS用セットアップ完了！"
    log_info "次のステップ:"
    log_info "  1. PRE-COMMIT-GUIDE-MAC.md でIDE設定を確認"
    log_info "  2. git commit でpre-commitフックをテスト"  
    log_info "  3. エラー時は pre-commit-result.txt で詳細確認"
    
    # 環境問題がある場合の最終メッセージ
    if [ $setup_issues -gt 0 ]; then
        log_warning "環境問題が残っています。完全な動作のため、上記の推奨インストールを実行してください"
    fi
}

# スクリプト実行
main "$@"
