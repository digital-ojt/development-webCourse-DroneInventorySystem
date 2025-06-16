# Pre-commit Hook 管理ガイド

## 概要
DroneInventorySystemプロジェクトの品質を保証するためのプリコミットフックシステムです。

## 機能
- **静的解析**: Checkstyle, PMD, SpotBugs による品質チェック
- **自動フォーマット**: Google Java Format による統一コード整形
- **ブランチ除外**: 特定ブランチでの品質チェックスキップ
- **詳細レポート**: 違反内容と修正方法の表示

## セットアップ

### インストール
```bash
# プロジェクトルートで実行
./scripts/setup-pre-commit-hook.sh
```

### アンインストール
```bash
# プリコミットフックを無効化
./scripts/uninstall-pre-commit-hook.sh
```

## ブランチ除外設定

以下のブランチでは静的解析をスキップします：
- `main`
- `master`
- `develop`
- `release/*`（release/v1.0など）
- `hotfix/*`（hotfix/bugfixなど）

### カスタマイズ方法
`scripts/hooks/pre-commit` ファイルの `SKIP_BRANCHES` 配列を編集：

```bash
SKIP_BRANCHES=(
    "main"
    "master"
    "develop"
    "release/*"
    "hotfix/*"
    "feature/urgent-fix"  # 追加例
)
```

## 動作フロー

### 通常ブランチ（feature/*など）
```
git commit → 品質チェック実行 → 違反あり → コミット阻止
                              → 違反なし → コミット成功
```

### 除外ブランチ（main, masterなど）
```
git commit → ブランチチェック → 除外対象 → 品質チェックスキップ → コミット成功
```

## 品質チェック内容

### Phase 1: 自動フォーマット
- Google Java Format による統一スタイル適用

### Phase 2: コンパイルチェック
- Javaコンパイルエラーの検出

### Phase 3: Checkstyle（厳格モード）
- コーディング規約違反の検出
- 340+違反パターンをチェック

### Phase 4: PMD品質チェック
- 複雑度、未使用変数等の品質問題検出
- 17+品質違反パターンをチェック

### Phase 5: SpotBugsバグ検出
- 潜在的バグパターンの検出
- null pointer、リソースリーク等をチェック

## トラブルシューティング

### 権限エラー
```bash
chmod +x .git/hooks/pre-commit
```

### フックが実行されない
```bash
# 再インストール
./scripts/setup-pre-commit-hook.sh
```

### 一時的にスキップしたい場合
```bash
git commit --no-verify -m "メッセージ"
```

## ファイル構成
```
scripts/
├── hooks/
│   └── pre-commit              # プリコミットフック本体
├── setup-pre-commit-hook.sh   # インストールスクリプト
├── uninstall-pre-commit-hook.sh # アンインストールスクリプト
└── README.md                   # このファイル
```

## 開発者向け情報

### 手動テスト
```bash
# プリコミットフックを手動実行
.git/hooks/pre-commit
```

### ログ確認
```bash
# 詳細な品質チェック結果
mvn checkstyle:check -Dcheckstyle.config.location=checkstyle-strict.xml
mvn pmd:check
mvn spotbugs:check
```

### バックアップファイル
- インストール時: `.git/hooks/pre-commit.backup.*`
- アンインストール時: `.git/hooks/pre-commit.removed.*`
