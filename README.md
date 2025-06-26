# development-PGcourse-DroneInventorySystem

【Java PGコース（MVC編）】製造業クライアントの在庫管理システム開発

## 🎯 統合静的解析システム - クロスプラットフォーム対応

このプロジェクトは、**macOS**と**Windows**の両環境に対応した統合静的解析システムを搭載しています。

### 📋 特徴

- **🔄 クロスプラットフォーム**: macOS・Windows自動判定・対応
- **🎨 統合フォーマット**: Space→Tab変換 + Prettier Java + Eclipse Formatter
- **🔍 静的解析**: Checkstyle + PMD + SpotBugs (JDK 17対応)
- **🎮 マルチIDE**: Eclipse + IntelliJ IDEA + VS Code対応
- **⚡ ワンコマンド**: 初心者でも簡単セットアップ
- **🛠️ 開発ツール**: デバッグ・テスト・修正ツール集約（scripts/tools/）
- **📄 詳細ログ**: エラー時にIDE共通で確認可能なpre-commit-result.txt生成

### 🚀 クイックスタート

#### 1. セットアップ（OS自動判定）

```bash
# プロジェクトルートで実行
./setup.sh
```

このコマンドが自動的に：
- OS環境を判定（macOS/Windows）
- 適切なセットアップスクリプトを実行
- 必要な依存関係をチェック・インストール
- pre-commitフックを設定

#### 2. IDE別の確認方法

| IDE | エラー確認方法 |
|-----|---------------|
| **Eclipse** | Package Explorer で `pre-commit-result.txt` |
| **IntelliJ IDEA** | Project toolwindow で `pre-commit-result.txt` |
| **VS Code** | Explorer で `pre-commit-result.txt` |

#### 3. 手動実行

```bash
# 統合フォーマット・静的解析の手動実行（クロスプラットフォーム対応）
cd DroneInventorySystem
../scripts/tools/format-and-check.sh

# または、OS別スクリプトで直接実行
# macOS:
../scripts/core/mac/format-and-check.sh

# Windows:
../scripts/core/windows/format-and-check.sh
```

#### 4. 開発・デバッグツール

```bash
# プリコミットフックのデバッグ実行
./scripts/tools/debug-pre-commit.sh

# 静的解析の詳細テスト（エラー詳細付き）
./scripts/tools/test-static-analysis.sh

# 短縮版プリコミットテスト
./scripts/tools/short-pre-commit.sh

# Eclipse起動問題の修正
./scripts/tools/fix-eclipse-main-class.sh
```

### 📂 ディレクトリ構造

```
project-root/
├── setup.sh                     # クロスプラットフォーム セットアップ
├── DroneInventorySystem/
│   ├── config/                   # 設定ファイル集約ディレクトリ
│   │   ├── checkstyle-*.xml     # Checkstyle設定ファイル群
│   │   ├── eclipse-format.xml   # Eclipse Formatter設定
│   │   └── pmd-*.xml            # PMD ルールセット群
│   ├── pom.xml                   # Maven設定（config/への参照）
│   ├── package.json              # Node.js依存関係設定
│   └── src/                      # ソースコード
├── scripts/
│   ├── setup/
│   │   ├── mac/
│   │   │   └── setup-mac.sh     # macOS用セットアップ
│   │   └── windows/
│   │       └── setup-windows.sh # Windows用セットアップ
│   ├── core/
│   │   ├── mac/
│   │   │   ├── format-and-check.sh  # macOS用実行スクリプト
│   │   │   └── pre-commit           # macOS用pre-commitフック
│   │   └── windows/
│   │       ├── format-and-check.sh  # Windows用実行スクリプト
│   │       └── pre-commit           # Windows用pre-commitフック
│   └── tools/                    # 開発・デバッグツール集
│       ├── format-and-check.sh      # クロスプラットフォーム統合スクリプト
│       ├── debug-pre-commit.sh      # プリコミットフックデバッグ用
│       ├── short-pre-commit.sh      # 短縮版プリコミットテスト
│       ├── test-static-analysis.sh  # 静的解析詳細テスト
│       └── fix-eclipse-main-class.sh # Eclipse起動問題修正
└── PRE-COMMIT-GUIDE-{OS}.md     # OS別運用ガイド
```

### 🛠️ OS別要件

#### macOS
- **必須**: Homebrew, Git, JDK 17
- **推奨**: Amazon Corretto 17
- **パッケージマネージャー**: `brew install maven node`

#### Windows
- **必須**: Git for Windows (Git Bash), JDK 17
- **推奨**: Chocolatey, Amazon Corretto 17
- **パッケージマネージャー**: `choco install maven nodejs`

### 📚 詳細ドキュメント

- 📋 **[テックブログ記事](TECH_BLOG_ARTICLE.md)**: システム構築の詳細解説
- 🔧 **[macOS用ガイド](PRE-COMMIT-GUIDE-MAC.md)**: macOS固有の設定・トラブルシューティング
- 🖥️ **[Windows用ガイド](PRE-COMMIT-GUIDE-WINDOWS.md)**: Windows固有の設定・トラブルシューティング
- 📊 **[シーケンス図](scripts/STATIC_ANALYSIS_SEQUENCE_DIAGRAM.md)**: 処理フローの詳細
- 🎨 **[ツール構成図](scripts/STATIC_ANALYSIS_TOOLS_FEATURE_DIAGRAM.md)**: 設定・カスタマイズ手順
- 🔄 **[クロスプラットフォーム移行ガイド](CROSS_PLATFORM_MIGRATION_GUIDE.md)**: 移行・運用詳細
- 🗃️ **[Eclipse設定手順書](scripts/Eclipse設定手順書.md)**: Eclipse IDE詳細設定
- 📝 **[クリーンアップ履歴](CLEANUP_HISTORY.md)**: ファイル整理・削除履歴

### 🛠️ 開発・デバッグツール（scripts/tools/）

| ツール | 用途 | 実行コマンド |
|--------|------|-------------|
| **format-and-check.sh** | クロスプラットフォーム統合スクリプト | `./scripts/tools/format-and-check.sh` |
| **debug-pre-commit.sh** | プリコミットフックのデバッグ実行 | `./scripts/tools/debug-pre-commit.sh` |
| **test-static-analysis.sh** | 静的解析の詳細テスト（エラー詳細付き） | `./scripts/tools/test-static-analysis.sh` |
| **short-pre-commit.sh** | 短縮版プリコミットテスト | `./scripts/tools/short-pre-commit.sh` |
| **fix-eclipse-main-class.sh** | Eclipse起動問題の修正 | `./scripts/tools/fix-eclipse-main-class.sh` |

### ⚠️ 重要な注意事項

- **Windows環境**: 実機テストが未完了のため、動作確認後の調整が必要な場合があります
- **パフォーマンス**: Windows環境ではmacOSより実行時間が長くなる可能性があります
- **パス処理**: OS別のパス区切り文字に自動対応しています

### 🆘 トラブルシューティング

問題が発生した場合：

1. **OS別ガイドを確認**: `PRE-COMMIT-GUIDE-{OS}.md`
2. **結果ファイルを確認**: `pre-commit-result.txt`（プリコミットフック実行後に生成）
3. **手動実行でテスト**: 
   - クロスプラットフォーム版: `cd DroneInventorySystem && ../scripts/tools/format-and-check.sh`
   - macOS版: `cd DroneInventorySystem && ../scripts/core/mac/format-and-check.sh`
4. **デバッグツールの活用**:
   - プリコミットフックデバッグ: `./scripts/tools/debug-pre-commit.sh`
   - 静的解析詳細テスト: `./scripts/tools/test-static-analysis.sh`
5. **結果ファイル未生成の場合**: 
   - プリコミットフックが途中終了している可能性
   - `git log --oneline -1` でコミットが実行されたか確認
   - 長時間の静的解析でタイムアウトの可能性
   - デバッグ実行: `./scripts/tools/debug-pre-commit.sh` で詳細確認
6. **Eclipse起動問題**: `./scripts/tools/fix-eclipse-main-class.sh` で修正
7. **GitHubでIssue報告**: OS環境・エラー詳細を含めて報告

#### 📄 結果ファイルについて

- **ファイル名**: `pre-commit-result.txt`
- **生成場所**: プロジェクトルート直下
- **生成タイミング**: プリコミットフック実行時（成功・失敗に関係なく生成）
- **IDE確認方法**: Eclipse Package Explorer、IntelliJ Project tool window、VS Code Explorerで確認可能

---

*クロスプラットフォーム対応統合静的解析システム by development-webCourse-DroneInventorySystem*
