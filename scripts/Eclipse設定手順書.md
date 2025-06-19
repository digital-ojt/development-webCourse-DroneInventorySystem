# Eclipse IDE タブインデント設定手順書

## 概要
このドキュメントでは、DroneInventorySystemプロジェクトでタブインデントを使用するためのEclipse IDE設定手順を説明します。

## 前提条件
- Eclipse IDE for Enterprise Java and Web Developers (2023-06以降推奨)
- 本プロジェクトがEclipseワークスペースにインポート済み

## 設定手順

### 1. フォーマッター設定のインポート

1. **Eclipse を起動**し、本プロジェクトを開きます

2. **メニューバー**から以下を選択：
   ```
   Window → Preferences (macOSの場合: Eclipse → Preferences)
   ```

3. **設定ダイアログ**で以下のパスに移動：
   ```
   Java → Code Style → Formatter
   ```

4. **Import...** ボタンをクリック

5. **ファイル選択ダイアログ**で以下のファイルを選択：
   ```
   プロジェクトルート/eclipse-format.xml
   ```

6. **開く** をクリック

7. **プロファイル名** が以下になっていることを確認：
   ```
   Java Coding Standards - Tab Indentation
   ```

8. **適用して閉じる** をクリック

### 2. プロジェクト固有設定の有効化

1. **プロジェクトエクスプローラー**で `DroneInventorySystem` プロジェクトを右クリック

2. **Properties** を選択

3. **プロジェクトプロパティダイアログ**で以下のパスに移動：
   ```
   Java Code Style → Formatter
   ```

4. **Enable project specific settings** にチェックを入れる

5. **Active profile** で以下を選択：
   ```
   Java Coding Standards - Tab Indentation
   ```

6. **適用して閉じる** をクリック

### 3. エディタ設定の確認

1. **メニューバー**から以下を選択：
   ```
   Window → Preferences → General → Editors → Text Editors
   ```

2. 以下の設定を確認・変更：
   - ☑️ **Insert spaces for tabs** のチェックを**外す**
   - **Displayed tab width**: `4`
   - ☑️ **Show whitespace characters** にチェック（推奨）

3. **適用して閉じる** をクリック

### 4. Java エディタ設定の確認

1. **メニューバー**から以下を選択：
   ```
   Window → Preferences → Java → Editor
   ```

2. **Typing** タブで以下を確認：
   - ☑️ **Tab policy**: `Use spaces` のチェックを**外す**
   - **Indentation size**: `4`
   - **Tab size**: `4`

3. **適用して閉じる** をクリック

### 5. 自動フォーマット設定

1. **メニューバー**から以下を選択：
   ```
   Window → Preferences → Java → Editor → Save Actions
   ```

2. 以下の設定を有効化：
   - ☑️ **Perform the selected actions on save**
   - ☑️ **Format source code**
   - ☑️ **Format all lines**
   - ☑️ **Organize imports**

3. **適用して閉じる** をクリック

## 動作確認

### 1. フォーマットテスト

1. **任意のJavaファイル**を開く

2. **Ctrl+Shift+F** (macOS: **Cmd+Shift+F**) でフォーマット実行

3. **インデントがタブになっている**ことを確認
   - 空白文字表示を有効にしている場合、タブは `→` で表示されます

### 2. 保存時自動フォーマット確認

1. **任意のJavaファイル**で意図的にフォーマットを崩す

2. **Ctrl+S** (macOS: **Cmd+S**) で保存

3. **自動的にタブインデントが適用される**ことを確認

## トラブルシューティング

### 問題: タブが適用されない

**原因**: プロジェクト固有の設定が無効
**解決策**: 手順2を再度実行し、プロジェクト固有設定を有効化

### 問題: フォーマッター設定が見つからない

**原因**: eclipse-format.xmlの場所が間違っている
**解決策**: 
```bash
# プロジェクトルートにファイルがあることを確認
ls -la eclipse-format.xml
```

### 問題: 他のプロジェクトにも設定が適用される

**原因**: グローバル設定が変更された
**解決策**: 各プロジェクトで個別に手順2を実行

## 追加の設定ファイル

このプロジェクトには以下の設定ファイルも含まれています：

- **`.editorconfig`**: エディタ横断的な設定
- **`.prettierrc`**: Prettier設定（CI/CD用）
- **`.vscode/settings.json`**: VS Code設定
- **`format-and-check.sh`**: 統合フォーマット・チェックスクリプト

## 参考情報

### Maven コマンド

```bash
# フォーマット実行
mvn formatter:format

# 静的解析チェック
mvn checkstyle:check pmd:check spotbugs:check

# 統合フォーマット・チェック
./format-and-check.sh
```

### キーボードショートカット

- **フォーマット**: Ctrl+Shift+F (macOS: Cmd+Shift+F)
- **インポート整理**: Ctrl+Shift+O (macOS: Cmd+Shift+O)
- **保存**: Ctrl+S (macOS: Cmd+S)

---

# Pre-commit結果の確認方法（Eclipse）

## コミット失敗時の確認手順

1. **プロジェクトルートの `pre-commit-result.txt` を開く**
   - Package Explorerでプロジェクトルート直下の `pre-commit-result.txt` をダブルクリック
   - ここにpre-commitの詳細結果が記録されています

2. **手動でpre-commitを実行したい場合**
   ```bash
   # macOSのターミナル.appで実行
   cd [プロジェクトパス]
   cd DroneInventorySystem
   ./format-and-check.sh
   ```

## 自動生成されるファイル
- `pre-commit-result.txt`: 最新のpre-commit実行結果
- このファイルは `.gitignore` に追加済みです

## 静的解析の内容
- 🎨 統合フォーマット (Prettier Java + Eclipse Formatter)
- 🔍 Checkstyle による品質チェック
- 🔍 PMD による品質チェック
- 🔍 SpotBugs による品質チェック
