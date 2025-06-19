#!/bin/bash

# =============================================================================
# 統合静的解析システム - クロスプラットフォーム セットアップスクリプト
# =============================================================================
# 
# このスクリプトは、Mac/Windows環境を自動判定し、適切なセットアップを実行します。
# 
# 使用方法:
#   Mac/Linux: ./setup.sh
#   Windows (Git Bash): ./setup.sh
#
# 対応OS:
#   - macOS (Intel/Apple Silicon)
#   - Windows 10/11 (Git Bash環境)
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

# OS判定関数
detect_os() {
    if [[ "$OSTYPE" == "darwin"* ]]; then
        echo "mac"
    elif [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "cygwin" ]] || [[ "$OSTYPE" == "win32" ]]; then
        echo "windows"
    else
        echo "unknown"
    fi
}

# メイン処理
main() {
    log_info "統合静的解析システム - クロスプラットフォーム セットアップ開始"
    
    # OS判定
    OS=$(detect_os)
    log_info "OS検出: $OS"
    
    # プロジェクトルート確認
    if [ ! -f "DroneInventorySystem/pom.xml" ]; then
        log_error "プロジェクトルートディレクトリで実行してください"
        log_error "DroneInventorySystem/pom.xml が見つかりません"
        exit 1
    fi
    
    # OS別セットアップスクリプト実行
    case $OS in
        "mac")
            log_info "macOS用セットアップを実行します"
            if [ -f "scripts/setup/mac/setup-mac.sh" ]; then
                chmod +x scripts/setup/mac/setup-mac.sh
                ./scripts/setup/mac/setup-mac.sh
            else
                log_error "macOS用セットアップスクリプトが見つかりません"
                exit 1
            fi
            ;;
        "windows")
            log_info "Windows用セットアップを実行します"
            if [ -f "scripts/setup/windows/setup-windows.sh" ]; then
                chmod +x scripts/setup/windows/setup-windows.sh
                ./scripts/setup/windows/setup-windows.sh
            else
                log_error "Windows用セットアップスクリプトが見つかりません"
                exit 1
            fi
            ;;
        *)
            log_error "サポートされていないOS: $OS"
            log_error "対応OS: macOS, Windows (Git Bash)"
            exit 1
            ;;
    esac
    
    log_success "クロスプラットフォーム セットアップ完了！"
    log_info "次のステップ:"
    log_info "  1. 使用するIDEに応じた設定を確認してください"
    log_info "  2. 'git commit' でpre-commitフックをテストしてください"
    log_info "  3. エラー時は 'pre-commit-result.txt' で詳細を確認してください"
}

# スクリプト実行
main "$@"
