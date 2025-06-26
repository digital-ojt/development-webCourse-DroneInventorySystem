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
