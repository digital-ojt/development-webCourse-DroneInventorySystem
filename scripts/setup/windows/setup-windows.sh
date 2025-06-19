#!/bin/bash

# =============================================================================
# Windows用 統合静的解析システム セットアップスクリプト
# =============================================================================
# 
# Windows 10/11 環境 (Git Bash) に特化したセットアップを実行します。
# Chocolatey、JDK、Maven、Node.js などの環境確認・セットアップガイドを提供します。
#
# 前提条件:
#   - Git for Windows (Git Bash) がインストール済み
#   - 管理者権限でのパッケージマネージャー使用可能
#   - JDK 17 (推奨: Amazon Corretto or Eclipse Temurin)
#
# 注意: このスクリプトは実機テストを行っていません。
#       Windows環境での動作確認後、必要に応じて調整してください。
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

log_note() {
    echo "📝 $1"
}

# Windows環境確認
check_windows_environment() {
    log_info "Windows環境の確認..."
    
    # Git Bash確認
    if [ -n "$BASH_VERSION" ]; then
        log_success "Git Bash環境: 確認済み"
    else
        log_warning "Git Bash環境ではない可能性があります"
    fi
    
    # Windows version確認
    if command -v systeminfo >/dev/null 2>&1; then
        WINDOWS_VERSION=$(systeminfo | grep "OS Name" | cut -d: -f2 | sed 's/^ *//')
        log_success "Windows環境: $WINDOWS_VERSION"
    else
        log_warning "Windows環境の詳細確認ができません"
    fi
}

# Chocolatey確認・推奨インストール
check_chocolatey() {
    if command -v choco >/dev/null 2>&1; then
        CHOCO_VERSION=$(choco --version)
        log_success "Chocolatey: インストール済み (v$CHOCO_VERSION)"
        return 0
    else
        log_warning "Chocolatey がインストールされていません"
        log_note "Chocolateyのインストール (管理者権限のPowerShellで実行):"
        log_note 'Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString("https://community.chocolatey.org/install.ps1"))'
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
            
            # JAVA_HOME確認
            if [ -n "$JAVA_HOME" ]; then
                log_success "JAVA_HOME: 設定済み ($JAVA_HOME)"
            else
                log_warning "JAVA_HOME が設定されていません"
                log_note "Windows環境変数の設定が必要です"
            fi
            return 0
        else
            log_warning "Java $JAVA_MAJOR が検出されました。JDK 17を推奨します"
            log_note "推奨インストール方法:"
            log_note "  1. Chocolatey: choco install corretto17jdk"
            log_note "  2. 手動: https://corretto.aws/downloads/latest/amazon-corretto-17-x64-windows-jdk.msi"
            return 1
        fi
    else
        log_warning "Java がインストールされていません"
        log_note "推奨インストール方法:"
        log_note "  1. Chocolatey: choco install corretto17jdk"
        log_note "  2. 手動: https://corretto.aws/downloads/latest/amazon-corretto-17-x64-windows-jdk.msi"
        return 1
    fi
}

# Maven確認・推奨インストール
check_maven() {
    if command -v mvn >/dev/null 2>&1; then
        MAVEN_VERSION=$(mvn --version | head -n1 | cut -d' ' -f3)
        log_success "Maven: インストール済み ($MAVEN_VERSION)"
        return 0
    else
        log_warning "Maven がインストールされていません"
        log_note "推奨インストール方法:"
        log_note "  1. Chocolatey: choco install maven"
        log_note "  2. 手動: https://maven.apache.org/download.cgi"
        return 1
    fi
}

# Node.js確認・推奨インストール
check_nodejs() {
    if command -v node >/dev/null 2>&1; then
        NODE_VERSION=$(node --version)
        log_success "Node.js: インストール済み ($NODE_VERSION)"
        
        if command -v npm >/dev/null 2>&1; then
            NPM_VERSION=$(npm --version)
            log_success "npm: インストール済み (v$NPM_VERSION)"
        else
            log_warning "npm が見つかりません"
        fi
        return 0
    else
        log_warning "Node.js がインストールされていません"
        log_note "推奨インストール方法:"
        log_note "  1. Chocolatey: choco install nodejs"
        log_note "  2. 手動: https://nodejs.org/en/download/"
        return 1
    fi
}

# 統合フォーマット環境確認
check_integrated_format() {
    log_info "統合フォーマット環境の確認..."
    DRONE_DIR="DroneInventorySystem"
    
    local format_ok=true
    
    if [ -f "$DRONE_DIR/format-and-check.sh" ]; then
        log_success "統合フォーマットスクリプト: $DRONE_DIR/format-and-check.sh"
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
    
    if [ -f "$DRONE_DIR/eclipse-format.xml" ]; then
        log_success "Eclipse Formatter設定: eclipse-format.xml"
    else
        log_warning "Eclipse Formatter設定が見つかりません"
        format_ok=false
    fi
    
    return $([ "$format_ok" = true ] && echo 0 || echo 1)
}

# Windows用pre-commitフックセットアップ
setup_precommit_hook() {
    log_info "Windows用pre-commitフックのセットアップ..."
    
    # 既存のフックをバックアップ
    if [ -f ".git/hooks/pre-commit" ]; then
        backup_file=".git/hooks/pre-commit.backup.$(date +%Y%m%d_%H%M%S)"
        cp .git/hooks/pre-commit "$backup_file"
        log_info "既存フックをバックアップ: $backup_file"
    fi
    
    # Windows用pre-commitフックをコピー
    if [ -f "scripts/core/windows/pre-commit" ]; then
        cp scripts/core/windows/pre-commit .git/hooks/pre-commit
        chmod +x .git/hooks/pre-commit
        log_success "Windows用pre-commitフック: インストール完了"
    else
        log_error "Windows用pre-commitフックが見つかりません"
        return 1
    fi
}

# Windows環境変数設定ガイド
show_environment_guide() {
    log_info "Windows環境変数設定ガイド..."
    
    log_note "以下の環境変数を設定してください:"
    log_note ""
    log_note "1. JAVA_HOME の設定:"
    log_note "   - 設定 → システム → 詳細設定 → 環境変数"
    log_note "   - システム環境変数に追加:"
    log_note "     変数名: JAVA_HOME"
    log_note "     値: C:\\Program Files\\Amazon Corretto\\jdk17.x.x_x (実際のパスに置き換え)"
    log_note ""
    log_note "2. PATH の更新:"
    log_note "   - 既存のPATH変数に以下を追加:"
    log_note "     %JAVA_HOME%\\bin"
    log_note "     Maven\\bin (Mavenインストールパス)"
    log_note "     Node.js (Node.jsインストールパス)"
    log_note ""
    log_note "3. 設定確認:"
    log_note "   - コマンドプロンプトを再起動"
    log_note "   - java -version"
    log_note "   - mvn -version"
    log_note "   - node --version"
}

# Prettier依存関係インストール
install_prettier_dependencies() {
    log_info "Prettier Java依存関係のインストール..."
    
    cd DroneInventorySystem
    
    if [ -f "package.json" ]; then
        if command -v npm >/dev/null 2>&1; then
            log_info "npm installを実行中..."
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

# Windows用テスト実行
run_windows_test() {
    log_info "Windows用セットアップテストを実行中..."
    
    # 統合フォーマットスクリプトのテスト (Windows用)
    cd DroneInventorySystem
    if [ -f "format-and-check.sh" ]; then
        chmod +x format-and-check.sh
        log_info "統合フォーマット・静的解析テスト実行..."
        log_note "注意: Windows環境でのテストは実機確認が必要です"
        
        # Windowsでの実行テスト
        if ./format-and-check.sh; then
            log_success "統合テスト: 合格"
        else
            log_warning "統合テスト: 一部警告あり（環境依存の可能性があります）"
        fi
    fi
    cd ..
}

# Windows用ガイドファイル生成
generate_windows_guide() {
    log_info "Windows用ガイドファイルを生成中..."
    
    cat > PRE-COMMIT-GUIDE-WINDOWS.md << 'EOF'
# Windows用 統合静的解析システム 運用ガイド

## セットアップ完了後の確認

### 1. 環境確認 (Git Bash)
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

### 2. 環境変数確認 (コマンドプロンプト)
```cmd
echo %JAVA_HOME%
echo %PATH%
```

### 3. IDE別設定

#### Eclipse IDE (Windows)
- **エラー確認**: Package Explorer で `pre-commit-result.txt` を開く
- **手動実行**: External Tools で `./DroneInventorySystem/format-and-check.sh` 設定
- **パス区切り**: Windows用パス区切り文字 (\\) に注意

#### IntelliJ IDEA (Windows)
- **エラー確認**: Project toolwindow で `pre-commit-result.txt` を開く
- **手動実行**: Terminal (Git Bash) で `./DroneInventorySystem/format-and-check.sh`
- **Git統合**: Git for Windows との連携確認

#### VS Code (Windows)
- **エラー確認**: Explorer で `pre-commit-result.txt` を開く
- **手動実行**: 統合Terminal (Git Bash) で `./DroneInventorySystem/format-and-check.sh`
- **拡張機能**: Prettier、Java Extension Pack 推奨

### 4. Windows固有のトラブルシューティング

#### 権限問題
```bash
# Git Bash で実行権限付与
chmod +x DroneInventorySystem/format-and-check.sh
chmod +x .git/hooks/pre-commit
```

#### パス問題
```bash
# Windows形式のパス確認
echo $JAVA_HOME
echo $PATH

# パス区切り文字の確認
# Windows: セミコロン (;)
# Git Bash: コロン (:)
```

#### 改行コード問題
```bash
# Git の改行コード設定確認
git config --global core.autocrlf
# 推奨値: true (Windows) または input (クロスプラットフォーム)
```

#### 実行時間の問題
- Windows環境では実行時間が長くなる場合があります
- Windows Defender のリアルタイム保護設定を確認
- プロジェクトフォルダを除外リストに追加することを検討

### 5. パフォーマンス最適化

#### Windows Defender除外設定
1. Windows Security を開く
2. ウイルスと脅威の防止 → 除外
3. フォルダを追加: プロジェクトルートディレクトリ
4. プロセスを追加: java.exe、node.exe

#### SSD最適化
- プロジェクトファイルをSSDに配置
- 定期的な Disk Cleanup 実行

### 6. 継続的な保守

#### Chocolatey更新
```powershell
# 管理者権限のPowerShellで実行
choco upgrade all
```

#### 手動更新
- JDK: Amazon Corretto最新版
- Maven: Apache Maven最新版  
- Node.js: LTS版の最新

### 7. 既知の制限事項

- **実機テスト未完了**: Windows環境での動作確認が必要
- **パフォーマンス**: macOS環境よりも実行時間が長い可能性
- **パス処理**: Windows固有のパス処理が必要な場合があります

### 8. サポート情報

問題が発生した場合:
1. PRE-COMMIT-GUIDE-WINDOWS.md を確認
2. pre-commit-result.txt の詳細エラーを確認
3. GitHubのIssueで報告 (Windows環境での問題として)

---
*Windows用統合静的解析システム by development-webCourse-DroneInventorySystem*
*注意: 実機テスト未完了のため、動作確認後の調整が必要な場合があります*
EOF

    log_success "Windows用ガイド: PRE-COMMIT-GUIDE-WINDOWS.md を生成"
}

# メイン処理
main() {
    log_info "Windows用 統合静的解析システム セットアップ開始"
    log_warning "注意: このスクリプトは実機テストを行っていません"
    log_warning "Windows環境での動作確認後、必要に応じて調整してください"
    
    local setup_issues=0
    
    # Windows環境確認
    check_windows_environment
    
    # 環境確認
    check_chocolatey || ((setup_issues++))
    check_java || ((setup_issues++))
    check_maven || ((setup_issues++))
    check_nodejs || ((setup_issues++))
    
    # 環境に問題がある場合は詳細ガイド表示
    if [ $setup_issues -gt 0 ]; then
        log_warning "$setup_issues 個の環境問題が検出されました"
        log_info "Windows用セットアップガイドを表示します..."
        show_environment_guide
        log_warning "上記の環境設定を完了後、再度セットアップを実行してください"
        
        read -p "環境設定をスキップしてセットアップを続行しますか? (y/n): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            log_info "セットアップを中断しました"
            log_info "環境設定完了後、再度 ./setup.sh を実行してください"
            exit 1
        fi
    fi
    
    # 統合フォーマット環境確認  
    check_integrated_format
    
    # Windows用pre-commitフックセットアップ
    setup_precommit_hook
    
    # 依存関係インストール
    install_prettier_dependencies || log_warning "Prettier依存関係のインストールに失敗しました"
    
    # Windows用テスト実行
    run_windows_test
    
    # Windows用ガイド生成
    generate_windows_guide
    
    log_success "Windows用セットアップ完了！"
    log_info "次のステップ:"
    log_info "  1. PRE-COMMIT-GUIDE-WINDOWS.md でIDE・環境設定を確認"
    log_info "  2. Windows環境変数の設定 (JAVA_HOME, PATH)"
    log_info "  3. git commit でpre-commitフックをテスト"  
    log_info "  4. エラー時は pre-commit-result.txt で詳細確認"
    
    # 環境問題がある場合の最終メッセージ
    if [ $setup_issues -gt 0 ]; then
        log_warning "環境問題が残っています。完全な動作のため、環境変数・パッケージ設定を完了してください"
    fi
    
    log_note "重要: Windows環境での実機テストを実施し、必要に応じてスクリプトを調整してください"
}

# スクリプト実行
main "$@"
