# 静的解析システム 包括的動作フローシーケンス図

## 概要
プロジェクトにおけるEclipse + IntelliJ IDEA対応の統合静的解析システムの詳細な動作フローを表現したシーケンス図です。JDK 17環境強制、Eclipse IDE連携、pre-commitフックのすべてのフローを網羅しています。

**🎨 2025年6月19日更新**: JDK 17環境強制対応、Eclipse Package Explorer連携、SpotBugs互換性問題対応

## 目次
- [静的解析システム 包括的動作フローシーケンス図](#静的解析システム-包括的動作フローシーケンス図)
  - [概要](#概要)
  - [目次](#目次)
  - [1. Pre-commitフック実行フロー（Eclipse対応）](#1-pre-commitフック実行フローeclipse対応)
  - [2. 統合フォーマット・静的解析フロー（format-and-check.sh）](#2-統合フォーマット静的解析フローformat-and-checksh)
  - [3. JDK 17環境強制設定フロー](#3-jdk-17環境強制設定フロー)
  - [4. Eclipse IDE連携フロー](#4-eclipse-ide連携フロー)
  - [5. SpotBugs互換性問題対応フロー](#5-spotbugs互換性問題対応フロー)
  - [6. ブランチ除外フロー](#6-ブランチ除外フロー)
  - [7. エラーハンドリング・リカバリーフロー](#7-エラーハンドリングリカバリーフロー)
  - [まとめ](#まとめ)
    - [主要なフロー](#主要なフロー)
    - [検出される品質問題](#検出される品質問題)
    - [次のステップ](#次のステップ)
  - [3. CI/CD自動実行フロー（GitHub Actions）](#3-cicd自動実行フローgithub-actions)
  - [3. Pre-commitフック実行フロー](#3-pre-commitフック実行フロー)
  - [4. GitHub Actions CI/CDフロー](#4-github-actions-cicdフロー)
  - [5. 統合テストフロー（comprehensive-integration-test.sh）](#5-統合テストフローcomprehensive-integration-testsh)
  - [6. エラーハンドリングフロー](#6-エラーハンドリングフロー)
  - [7. ツール間連携フロー](#7-ツール間連携フロー)
  - [まとめ](#まとめ-1)
    - [主要なフロー](#主要なフロー-1)
    - [検出される品質問題](#検出される品質問題-1)
    - [次のステップ](#次のステップ-1)

## 1. Pre-commitフック実行フロー（Eclipse対応）

```mermaid
sequenceDiagram
    participant Dev as 開発者
    participant Eclipse as Eclipse IDE
    participant Git as Git
    participant Hook as Pre-commit Hook
    participant Script as format-and-check.sh
    participant Result as pre-commit-result.txt
    participant Guide as PRE-COMMIT-GUIDE.md
    
    Note over Dev, Guide: Eclipse開発者のコミットフロー
    Dev->>Eclipse: コードを編集・保存
    Eclipse->>Eclipse: JDK 17設定確認
    Dev->>Eclipse: Commit実行
    Eclipse->>Git: git commit実行
    
    Git->>Hook: Pre-commitフック起動
    Hook->>Hook: 現在ブランチ取得
    
    alt 除外ブランチの場合
        Hook->>Hook: main/master/develop等をチェック
        Hook->>Git: スキップメッセージ出力
        Git->>Eclipse: コミット成功
    else 対象ブランチの場合
        Hook->>Script: ./format-and-check.sh実行
        
        alt 静的解析成功
            Script-->>Hook: 終了コード 0
            Hook->>Git: コミット許可
            Git->>Eclipse: コミット成功
        else 静的解析失敗
            Script-->>Hook: 終了コード 1
            Hook->>Result: 詳細ログを pre-commit-result.txt に出力
            Hook->>Eclipse: エラーメッセージ表示
            Note over Hook, Eclipse: "Eclipse Package Explorerで<br/>pre-commit-result.txtを開いてください"
            Eclipse->>Eclipse: ダイアログ表示
            Dev->>Eclipse: Package Explorer操作
            Eclipse->>Result: pre-commit-result.txt開く
            Dev->>Guide: PRE-COMMIT-GUIDE.md参照
        end
    end
```

## 2. 統合フォーマット・静的解析フロー（format-and-check.sh）

```mermaid
sequenceDiagram
    participant Hook as Pre-commit Hook
    participant Script as format-and-check.sh
    participant Java as Java Runtime
    participant Maven as Maven
    participant Node as Node.js
    participant Check as Checkstyle
    participant PMD as PMD
    participant Spot as SpotBugs
    participant Files as File System
    
    Note over Hook, Files: JDK 17環境確認・設定フェーズ
    Hook->>Script: 実行開始
    Script->>Maven: Maven環境検出
    Maven-->>Script: Maven パス返却
    Script->>Java: java -version実行
    Java-->>Script: バージョン情報返却
    Script->>Script: JDK 17チェック
    
    alt JDK 17の場合
        Script->>Java: JAVA_HOME設定
        Note over Script: ✅ JDK 17環境確認完了
    else JDK 17以外の場合
        Script->>Script: 警告メッセージ出力
        Note over Script: ⚠️ JDK xx使用中、JDK 17推奨
    end
    
    Note over Hook, Files: 統合フォーマットフェーズ
    Script->>Files: タブインデント変換
    Files-->>Script: 変換完了
    
    Script->>Node: Prettier実行チェック
    alt Node.js環境がある場合
        Node-->>Script: npm run format実行
    else Maven prettier pluginの場合
        Script->>Maven: prettier:write実行
    end
    
    Script->>Maven: Eclipse Formatter実行
    Maven-->>Script: formatter:format完了
    
    Note over Hook, Files: 静的解析フェーズ
    Script->>Check: Checkstyle実行
    Check-->>Script: 結果返却
    
    Script->>PMD: PMD実行
    PMD-->>Script: 結果返却
    
    Script->>Spot: SpotBugs実行
    alt SpotBugs成功
        Spot-->>Script: 解析結果返却
    else Java 21互換性問題
        Spot-->>Script: クラスファイルエラー
        Script->>Script: エラー詳細分析
        Script->>Script: 適切なスキップ処理
    end
    
    Script->>Script: 総合結果判定
    Script-->>Hook: 終了コード返却
```

## 3. JDK 17環境強制設定フロー

```mermaid
flowchart TD
    A[format-and-check.sh実行] --> B{Javaバージョン確認}
    B --> C[java -version実行]
    C --> D{JDK 17?}
    
    D -->|Yes| E[✅ JDK 17環境確認]
    D -->|JDK 11| F[⚠️ JDK 11警告]
    D -->|JDK 21| G[⚠️ JDK 21警告]
    D -->|その他| H[⚠️ 非対応バージョン警告]
    
    E --> I[JAVA_HOME設定]
    F --> J[Eclipse設定案内]
    G --> K[Eclipse設定案内]
    H --> L[Eclipse設定案内]
    
    I --> M[Maven実行環境設定]
    J --> M
    K --> M
    L --> M
    
    M --> N[静的解析実行継続]
    
    style E fill:#e1f5fe
    style F fill:#fff3e0
    style G fill:#fff3e0
    style H fill:#ffebee
```

## 4. Eclipse IDE連携フロー

```mermaid
sequenceDiagram
    participant Dev as Eclipse開発者
    participant Eclipse as Eclipse IDE
    participant Explorer as Package Explorer
    participant Result as pre-commit-result.txt
    participant Guide as PRE-COMMIT-GUIDE.md
    participant Maven as Maven View
    
    Note over Dev, Maven: Eclipse開発者の静的解析結果確認フロー
    
    Dev->>Eclipse: コミット実行
    Eclipse->>Eclipse: Pre-commitフック実行
    
    alt コミット失敗時
        Eclipse->>Eclipse: エラーダイアログ表示
        Note over Eclipse: "Eclipse Package Explorerで<br/>pre-commit-result.txtを開いてください"
        
        Dev->>Explorer: Package Explorerを開く
        Explorer->>Explorer: プロジェクトルートを展開
        Dev->>Result: pre-commit-result.txtをダブルクリック
        Result->>Eclipse: ファイル内容をエディタで表示
        
        Note over Result, Eclipse: 実行時間、ブランチ、詳細ログを確認
        
        Dev->>Guide: PRE-COMMIT-GUIDE.mdを参照
        Guide->>Eclipse: トラブルシューティング情報表示
        
        alt Maven環境の問題
            Dev->>Maven: Eclipse Maven設定確認
            Maven->>Eclipse: Maven Installations設定画面
        else Java環境の問題
            Dev->>Eclipse: Preferences → Installed JREs
            Eclipse->>Eclipse: JDK 17設定確認
        else 静的解析違反
            Dev->>Eclipse: Problems View確認
            Eclipse->>Eclipse: エラー箇所表示
        end
        
        Dev->>Eclipse: 問題修正後、再コミット
    else コミット成功時
        Eclipse->>Eclipse: 成功メッセージ表示
        Note over Eclipse: ✅ Pre-commit checks成功
    end
```

## 5. SpotBugs互換性問題対応フロー

```mermaid
flowchart TD
    A[SpotBugs実行開始] --> B[spotbugs:check実行]
    B --> C{実行結果}
    
    C -->|成功| D[✅ SpotBugs合格]
    C -->|エラー| E[エラー詳細分析]
    
    E --> F{エラー種別判定}
    F -->|Unsupported class file<br/>major version 68| G[Java 21クラスファイル問題]
    F -->|その他のエラー| H[通常の静的解析エラー]
    
    G --> I[⚠️ 互換性問題メッセージ]
    I --> J[SpotBugsスキップ]
    J --> K[Checkstyle・PMD継続]
    
    H --> L[⚠️ SpotBugs違反検出]
    L --> M[詳細エラー情報出力]
    
    D --> N[静的解析継続]
    K --> N
    M --> O[終了コード1で停止]
    
    style G fill:#fff3e0
    style I fill:#fff3e0
    style J fill:#e8f5e8
    style L fill:#ffebee
    style O fill:#ffebee
```

## 6. ブランチ除外フロー

```mermaid
flowchart TD
    A[Pre-commitフック開始] --> B[現在ブランチ取得]
    B --> C[git rev-parse --abbrev-ref HEAD]
    C --> D{ブランチ判定}
    
    D -->|main| E[🔄 静的解析スキップ]
    D -->|master| E
    D -->|master-test| E
    D -->|develop| E
    D -->|release/*| E
    D -->|hotfix/*| E
    D -->|feature/*| F[🔍 静的解析実行]
    D -->|その他開発ブランチ| F
    
    E --> G[✅ コミット続行]
    F --> H[format-and-check.sh実行]
    H --> I{静的解析結果}
    
    I -->|成功| J[✅ コミット許可]
    I -->|失敗| K[❌ コミット拒否]
    K --> L[pre-commit-result.txt生成]
    
    style E fill:#e8f5e8
    style G fill:#e8f5e8
    style J fill:#e8f5e8
    style K fill:#ffebee
    style L fill:#fff3e0
```

## 7. エラーハンドリング・リカバリーフロー

```mermaid
sequenceDiagram
    participant Dev as 開発者
    participant System as 静的解析システム
    participant Error as エラーハンドラ
    participant Recovery as リカバリープロセス
    participant Guide as ガイドシステム
    
    Dev->>System: 静的解析実行
    
    alt Maven環境エラー
        System->>Error: mvn: command not found
        Error->>Recovery: Maven自動検出実行
        Recovery->>Guide: インストール手順案内
        Guide-->>Dev: Homebrewインストール案内
    end
    
    alt Java環境エラー
        System->>Error: 非対応Javaバージョン
        Error->>Recovery: JAVA_HOME自動設定
        Recovery->>Guide: Eclipse設定案内
        Guide-->>Dev: JDK 17設定手順
    end
    
    alt SpotBugs互換性エラー
        System->>Error: class file major version 68
        Error->>Recovery: エラー種別分析
        Recovery->>System: SpotBugsスキップ設定
        System->>Guide: 代替解決策案内
        Guide-->>Dev: 継続実行メッセージ
    end
    
    alt Prettier環境エラー
        System->>Error: prettier-plugin-java not found
        Error->>Recovery: Maven pluginフォールバック
        Recovery->>System: Eclipse Formatter使用
        System-->>Dev: 警告メッセージ（処理継続）
    end
    
    Note over Dev, Guide: 全てのエラーで適切なガイダンスを提供
```

## まとめ

### 主要なフロー
1. **Eclipse中心の開発体験**: ターミナル操作不要
2. **JDK 17環境強制**: プロジェクト要件の確実な適用
3. **柔軟なエラーハンドリング**: 部分的失敗でも処理継続
4. **ブランチベース運用**: 開発ブランチでのみ品質ゲート

### 検出される品質問題
- **Checkstyle**: コーディング規約違反
- **PMD**: 品質問題・複雑度
- **SpotBugs**: バグパターン（Java 17環境）
- **統合フォーマット**: インデント・スタイル統一

### 次のステップ
- IntelliJ IDEA対応の強化
- CI/CDパイプライン統合
- カスタムルール追加
- SonarQube連携検討
        Format->>Format: 統合フォーマット・チェック処理
        Format-->>Script: 完了
        Script->>Dev: 統合実行完了
    else 手動実行選択の場合
        Note over Dev, Files: Phase 3: スペース→タブ変換
        Script->>Dev: Phase 1実行確認
        Dev->>Script: Enter（実行）
        Script->>Files: find + sed によるタブ変換
        Files-->>Script: 変換完了
        
        Note over Dev, Files: Phase 4: Prettier実行（オプション）
        alt Prettier環境が利用可能
            Script->>Dev: Phase 2実行確認
            Dev->>Script: Enter（実行）
            Script->>Node: npm run format
            Node->>Files: Prettier Java フォーマット（タブ設定）
            Files-->>Node: フォーマット完了
            Node-->>Script: 実行結果
        else Prettier環境未設定
            Script->>Dev: Prettier環境未設定のためスキップ
        end
        
        Note over Dev, Files: Phase 5: Eclipse Formatter実行
        Script->>Dev: Phase 3実行確認
        Dev->>Script: Enter（実行）
        Script->>Maven: mvn formatter:format
        Maven->>Java: Eclipse Code Formatter実行
        Java->>Files: eclipse-format.xml使用でタブフォーマット
        Files-->>Java: フォーマット完了
        Java-->>Maven: 実行結果
        Maven-->>Script: フォーマット成功
    end
    Java->>Files: フォーマット状態チェック
    Files-->>Java: フォーマット違反情報
    Java-->>Maven: 違反数・詳細
    Maven-->>Script: 実行結果（exit code）
    Script->>Dev: 結果表示・次フェーズ確認
    
    Note over Dev, Files: Phase 3: 自動フォーマット実行
    Dev->>Script: Enter（実行）
    Script->>Maven: mvn fmt:format
    Maven->>Java: Google Java Format実行
    Java->>Files: ファイル自動修正
    Files-->>Java: 修正完了
    Java-->>Maven: フォーマット済みファイル数
    Maven-->>Script: 実行結果
    Script->>Dev: フォーマット結果表示
    
    Note over Dev, Files: Phase 4: フォーマット差分確認
    Dev->>Script: Enter（実行）
    Script->>Git: git diff --name-only
    Git->>Files: 変更ファイル検索
    Files-->>Git: 変更ファイル一覧
    Git-->>Script: 差分ファイル一覧
    Script->>Dev: 変更ファイル表示
    
    Note over Dev, Files: Phase 5: コンパイルチェック
    Dev->>Script: Enter（実行）
    Script->>Maven: mvn compile -DskipTests -q
    Maven->>Java: Javaコンパイル
    Java->>Files: .classファイル生成
    
    alt Lombokエラーの場合
        Java-->>Maven: コンパイルエラー
        Maven-->>Script: BUILD FAILURE
        Script->>Dev: Lombokエラー表示（予期される）
    else 正常コンパイル
        Files-->>Java: コンパイル完了
        Java-->>Maven: BUILD SUCCESS
        Maven-->>Script: 成功
        Script->>Dev: コンパイル成功表示
    end
    
    Note over Dev, Files: Phase 6: 基本スタイルチェック
    Dev->>Script: Enter（実行）
    Script->>Maven: mvn checkstyle:check -Dcheckstyle.config.location=checkstyle-simple.xml
    Maven->>Java: Checkstyle実行
    Java->>Files: checkstyle-simple.xml読み込み
    Java->>Files: ソースコード解析
    Files-->>Java: 違反情報
    Java-->>Maven: 警告レベル違反
    Maven-->>Script: BUILD SUCCESS（警告のみ）
    Script->>Dev: 警告レベル結果表示
    
    Note over Dev, Files: Phase 7: 厳格スタイルチェック
    Dev->>Script: Enter（実行）
    Script->>Maven: mvn checkstyle:check -Dcheckstyle.config.location=checkstyle-strict.xml
    Maven->>Java: Checkstyle実行
    Java->>Files: checkstyle-strict.xml読み込み
    Java->>Files: ソースコード解析
    Files-->>Java: xxx件の違反検出
    Java-->>Maven: エラーレベル違反
    Maven-->>Script: BUILD FAILURE
    Script->>Dev: xxx違反でビルド失敗表示
    
    Note over Dev, Files: Phase 8: PMD品質チェック
    Dev->>Script: Enter（実行）
    Script->>Maven: mvn pmd:check
    Maven->>Java: PMD実行
    Java->>Files: pmd-basic.xml読み込み
    Java->>Files: ソースコード解析
    Files-->>Java: xxx件の違反検出
    Java-->>Maven: 品質違反情報
    Maven-->>Script: BUILD SUCCESS（failOnViolation=false）
    Script->>Dev: xxx違反検出表示
    
    Note over Dev, Files: Phase 9: SpotBugsバグ検出
    Dev->>Script: Enter（実行）
    Script->>Maven: mvn spotbugs:check
    Maven->>Java: SpotBugs実行
    
    alt コンパイル成功の場合
        Java->>Files: .classファイル解析
        Files-->>Java: 9件のバグパターン検出
        Java-->>Maven: バグ情報
        Maven-->>Script: BUILD FAILURE（failOnError=true）
        Script->>Dev: xxxバグ検出表示
    else コンパイル失敗の場合
        Java-->>Maven: コンパイルエラーで実行不可
        Maven-->>Script: BUILD FAILURE
        Script->>Dev: SpotBugs実行不可表示
    end
    
    Note over Dev, Files: Phase 10: レポート生成
    Dev->>Script: Enter（実行）
    Script->>Maven: mvn checkstyle:checkstyle pmd:pmd spotbugs:spotbugs
    Maven->>Java: レポート生成プロセス
    
    par Checkstyleレポート
        Java->>Files: target/site/checkstyle.html生成
        Java->>Files: target/checkstyle-result.xml生成
    and PMDレポート
        Java->>Files: target/site/pmd.html生成
        Java->>Files: target/pmd.xml生成
    and SpotBugsレポート
        Java->>Files: target/site/spotbugs.html生成
        Java->>Files: target/spotbugsXml.xml生成
    end
    
    Files-->>Java: レポート生成完了
    Java-->>Maven: 生成結果
    Maven-->>Script: レポート生成成功
    
    Note over Dev, Files: Phase 11: レポート確認・Git操作
    Script->>Files: レポートファイル存在確認
    Files-->>Script: 存在状況
    Script->>Dev: レポート一覧表示
    Script->>Dev: Git操作選択プロンプト
    
    alt フォーマット済みファイルステージング
        Dev->>Script: 選択1
        Script->>Git: git add .
        Git->>Files: ステージング実行
        Files-->>Git: ステージング完了
        Git-->>Script: 完了通知
        Script->>Git: git status
        Git-->>Script: ステータス情報
        Script->>Dev: ステージング結果表示
    else テストファイルコミット
        Dev->>Script: 選択2
        Script->>Git: git add . + git commit
        Git->>Files: コミット実行
        Files-->>Git: コミット完了
        Git-->>Script: コミット完了
        Script->>Dev: コミット結果表示
    else スキップ
        Dev->>Script: 選択3
        Script->>Dev: Git操作スキップ
    end
    
    Script->>Dev: 実行完了・参考資料案内
```

## 2. 統合フォーマット実行フロー（format-and-check.sh）

```mermaid
sequenceDiagram
    participant Dev as 開発者
    participant Script as format-and-check.sh
    participant Maven as Maven
    participant Node as Node.js/Prettier
    participant Java as Java/JVM
    participant Files as ファイルシステム
    participant Reports as レポート
    participant Git as Git
    
    Note over Dev, Reports: 🚀 統合フォーマット・チェック開始
    Dev->>Script: ./format-and-check.sh 実行
    Script->>Dev: 🎯 統合フォーマット・品質チェック開始
    
    Note over Script, Reports: Phase 1: 環境確認・セットアップ
    Script->>Files: Projectディレクトリ存在確認
    Files-->>Script: ディレクトリ確認完了
    Script->>Files: pom.xml, package.json, .prettierrc, eclipse-format.xml存在確認
    Files-->>Script: 設定ファイル確認結果
    Script->>Java: Java環境確認
    Java-->>Script: Java 17.0.9 LTS確認
    Script->>Maven: Maven環境確認
    Maven-->>Script: Apache Maven 3.9.x確認
    Script->>Dev: 🔧 環境確認完了 - すべての設定ファイルが存在
    
    Note over Script, Reports: Phase 2: バックアップ＆スペース→タブ変換
    Script->>Files: 変更前バックアップ作成
    Files-->>Script: バックアップ完了
    Script->>Files: find src/main/java -name "*.java" -type f
    Files-->>Script: xxx個のJavaファイル検出
    
    loop 各Javaファイル
        Script->>Files: sed 's/    /\t/g' (4スペース→タブ)
        Files-->>Script: ファイル変換完了
    end
    
    Script->>Dev: ✅ Step 1: タブ変換完了 (47ファイル処理)
    
    Note over Script, Reports: Phase 3: Prettier Java実行
    alt package.json & .prettierrcが存在
        Script->>Node: npm run format (prettier-plugin-java)
        Note over Node, Files: .prettierrc設定: useTabs=true, tabWidth=4
        Node->>Files: prettier --write "src/**/*.java"
        Files->>Files: Prettier+Java plugin適用
        Files-->>Node: フォーマット完了
        Node-->>Script: Prettier実行成功
        Script->>Dev: ✅ Step 2: Prettier Java フォーマット完了
    else Node.js/Prettier環境未設定
        Script->>Dev: ⚠️ Step 2: Prettier環境未設定 - スキップ
    end
    
    Note over Script, Reports: Phase 4: Eclipse Code Formatter実行
    Script->>Maven: mvn net.revelc.code.formatter:formatter-maven-plugin:format -q
    Maven->>Java: Eclipse Code Formatter plugin実行
    Note over Java, Files: eclipse-format.xml設定: tab_char=tab, tab_size=4
    Java->>Files: Eclipse formatter rules適用
    Files-->>Java: フォーマット適用完了
    Java-->>Maven: BUILD SUCCESS
    Maven-->>Script: Eclipse Formatter実行完了
    Script->>Dev: ✅ Step 3: Eclipse Code Formatter完了
    
    Note over Script, Reports: Phase 5: フォーマット検証
    Script->>Maven: mvn net.revelc.code.formatter:formatter-maven-plugin:validate
    Maven->>Java: フォーマット状態チェック
    Java->>Files: フォーマット差分確認
    Files-->>Java: フォーマット状態返却
    Java-->>Maven: 検証結果
    Maven-->>Script: フォーマット検証完了
    Script->>Dev: 🔍 Step 4: フォーマット検証完了
    
    Note over Script, Reports: Phase 6: 静的解析実行（並列）
    Script->>Dev: 🚀 静的解析開始...
    
    par Checkstyle Simple
        Script->>Maven: mvn checkstyle:check -Dcheckstyle.config.location=checkstyle-simple.xml -q
        Maven->>Java: Checkstyle basic rules実行
        Java->>Files: 構文・スタイルチェック
        Files-->>Java: 違反情報検出
        Java-->>Maven: 結果（警告レベル多数）
        Maven-->>Script: BUILD SUCCESS (警告のみ)
    and PMD品質チェック
        Script->>Maven: mvn pmd:check -q
        Maven->>Java: PMD code quality analysis実行
        Java->>Files: コード品質分析
        Files-->>Java: xxx件の品質問題検出
        Java-->>Maven: BUILD FAILURE (failOnViolation=false)
        Maven-->>Script: PMD: xxx件違反検出
    and SpotBugsバグ検出
        Script->>Maven: mvn compile spotbugs:check -q
        Maven->>Java: SpotBugs bytecode analysis実行
        Java->>Files: バイトコード解析
        Files-->>Java: xxx件のバグパターン検出
        Java-->>Maven: BUILD FAILURE (xxx bugs found)
        Maven-->>Script: SpotBugs: xxx件バグ検出
    end
    
    Note over Script, Reports: Phase 7: レポート生成
    Script->>Maven: mvn checkstyle:checkstyle pmd:pmd spotbugs:spotbugs -q
    Maven->>Java: 全ツールレポート生成
    
    par Checkstyleレポート
        Java->>Files: target/site/checkstyle.html生成
    and PMDレポート  
        Java->>Files: target/site/pmd.html生成
    and SpotBugsレポート
        Java->>Files: target/site/spotbugs.html生成
    end
    
    Files-->>Java: レポート生成完了
    Java-->>Maven: BUILD SUCCESS
    Maven-->>Script: レポート生成完了
    
    Note over Script, Reports: Phase 8: Git差分確認
    Script->>Git: git diff --name-only
    Git->>Files: 変更ファイル検索
    Files-->>Git: 変更されたJavaファイル一覧
    Git-->>Script: 差分ファイル情報
    
    Note over Script, Reports: Phase 9: 結果サマリー＆表示
    Script->>Script: 実行結果集計・分析
    Script->>Files: レポートファイル存在確認
    Files-->>Script: HTMLレポート確認完了
    
    Script->>Dev: 📊 ═══ 実行結果サマリー ═══
    Script->>Dev: ✅ タブ変換: xxxファイル処理完了
    Script->>Dev: ✅ Prettier Java: フォーマット適用
    Script->>Dev: ✅ Eclipse Formatter: 統一スタイル適用
    Script->>Dev: ⚠️ 品質チェック結果:
    Script->>Dev:     - PMD: xxx件の品質問題
    Script->>Dev:     - SpotBugs: xxx件のバグパターン
    Script->>Dev: 📁 詳細レポート: target/site/*.html
    Script->>Dev: 🔗 統合設定ファイル:
    Script->>Dev:     - .prettierrc (Prettier+Java設定)
    Script->>Dev:     - eclipse-format.xml (Eclipse設定)
    Script->>Dev:     - pom.xml (Maven plugin設定)
    Script->>Dev: 💡 Tips: VS Code使用時は Prettier拡張を有効化
    Script->>Dev: 🎉 統合フォーマット・品質チェック完了
```

## 3. CI/CD自動実行フロー（GitHub Actions）

```mermaid
sequenceDiagram
    participant Dev as 開発者
    participant GitHub as GitHub
    participant Actions as GitHub Actions
    participant Runner as Ubuntu Runner
    participant Maven as Maven
    participant Java as Java 17
    participant Artifacts as Artifacts
    
    Note over Dev, Artifacts: トリガー: push/pull_request
    Dev->>GitHub: git push（コード変更）
    GitHub->>Actions: ワークフロートリガー
    Actions->>Runner: Ubuntu環境起動
    
    Note over Runner, Artifacts: Phase 1: 環境セットアップ
    Runner->>GitHub: ソースコードチェックアウト
    Runner->>Runner: JDK 17セットアップ（temurin）
    Runner->>Runner: Maven依存関係キャッシュ確認
    
    Note over Runner, Artifacts: Phase 2: 自動フォーマット
    Runner->>Maven: cd project && mvn fmt:format -q
    Maven->>Java: Google Java Format実行
    Java->>Runner: ファイルフォーマット
    Runner->>GitHub: git diff --quiet（変更確認）
    
    alt フォーマット変更がある場合
        Runner->>GitHub: git config設定
        Runner->>GitHub: git add -A
        Runner->>GitHub: git commit -m "Auto-format"
        GitHub->>GitHub: 自動コミット
    else 変更なし
        Runner->>Runner: フォーマット変更なし
    end
    
    Note over Runner, Artifacts: Phase 3: 基本スタイルチェック
    Runner->>Maven: mvn checkstyle:check -Dcheckstyle.config.location=checkstyle-simple.xml
    Maven->>Java: Checkstyle基本実行
    Java->>Maven: 警告レベル結果
    Maven->>Runner: continue-on-error: true
    
    Note over Runner, Artifacts: Phase 4: 厳格品質チェック
    Runner->>Maven: mvn checkstyle:check -Dcheckstyle.config.location=checkstyle-strict.xml
    Maven->>Java: Checkstyle厳格実行
    
    alt 違反検出の場合
        Java->>Maven: xxx違反検出
        Maven->>Runner: BUILD FAILURE
        Runner->>Actions: ワークフロー失敗
        Actions->>GitHub: 品質ゲート失敗
        GitHub->>Dev: 失敗通知
    else 違反なしの場合
        Java->>Maven: BUILD SUCCESS
        Maven->>Runner: 成功
    end
    
    par PMDチェック
        Runner->>Maven: mvn pmd:check
        Maven->>Java: PMD実行
        Java->>Maven: xxx違反検出
        Maven->>Runner: BUILD FAILURE
    and SpotBugsチェック
        Runner->>Maven: mvn compile spotbugs:check
        Maven->>Java: SpotBugs実行
        Java->>Maven: xxxバグ検出
        Maven->>Runner: BUILD FAILURE
    end
    
    Note over Runner, Artifacts: Phase 5: レポート生成・保存
    Runner->>Maven: mvn checkstyle:checkstyle pmd:pmd spotbugs:spotbugs
    Maven->>Java: 全レポート生成
    Java->>Runner: HTMLレポート生成
    Runner->>Artifacts: レポートアップロード
    Artifacts->>GitHub: レポート保存
    
    Note over Runner, Artifacts: Phase 6: 結果サマリー
    Runner->>Actions: GITHUB_STEP_SUMMARY作成
    Actions->>GitHub: 実行結果表示
    GitHub->>Dev: 詳細結果通知
```

## 3. Pre-commitフック実行フロー

```mermaid
sequenceDiagram
    participant Dev as 開発者
    participant Git as Git
    participant Hook as pre-commit hook
    participant Maven as Maven
    participant Java as Java/JVM
    participant Files as ファイルシステム
    
    Note over Dev, Files: Gitコミット開始
    Dev->>Git: git commit -m "message"
    Git->>Hook: pre-commitフック起動
    
    Note over Hook, Files: Phase 1: 環境確認
    Hook->>Hook: projectディレクトリ確認
    Hook->>Java: java -version確認
    Java-->>Hook: Java 17 Corretto確認
    Hook->>Maven: mvn -version確認
    Maven-->>Hook: Maven 3.x確認
    
    Note over Hook, Files: Phase 2: ステージファイル確認
    Hook->>Git: git diff --cached --name-only
    Git-->>Hook: ステージ済みJavaファイル一覧
    
    alt Javaファイルがステージされている場合
        Note over Hook, Files: Phase 3: 自動フォーマット
        Hook->>Maven: mvn fmt:format
        Maven->>Java: Google Java Format実行
        Java->>Files: フォーマット適用
        Files-->>Java: フォーマット完了
        Java-->>Maven: xxxファイル処理完了
        Maven-->>Hook: フォーマット成功
        
        Note over Hook, Files: Phase 4: フォーマット後の変更確認
        Hook->>Git: git diff --name-only
        Git-->>Hook: 変更ファイル一覧
        
        alt フォーマットによる変更がある場合
            Hook->>Git: git add .
            Git-->>Hook: 自動ステージング完了
        end
        
        Note over Hook, Files: Phase 5: 静的解析実行
        Hook->>Maven: mvn checkstyle:check -Dcheckstyle.config.location=checkstyle-simple.xml
        Maven->>Java: Checkstyle実行
        Java->>Files: 規約チェック
        Files-->>Java:xxx件の違反検出
        Java-->>Maven: 違反情報
        Maven-->>Hook: BUILD FAILURE
        
        Hook->>Dev: ❌ Pre-commit検証失敗
        Hook->>Dev: 🔍 xxx件のCheckstyle違反検出
        Hook->>Dev: 📝 修正後に再コミットが必要
        Hook->>Git: exit 1（コミット中断）
        Git-->>Dev: コミット失敗
        
    else Javaファイルの変更なし
        Hook->>Git: exit 0（コミット続行）
        Git-->>Dev: コミット成功
    end
```

## 4. GitHub Actions CI/CDフロー

```mermaid
sequenceDiagram
    participant Dev as 開発者
    participant GitHub as GitHub
    participant Runner as GitHub Runner
    participant Maven as Maven
    participant Java as Java/JVM
    participant Reports as Reports
    
    Note over Dev, Reports: CI/CDパイプライン開始
    Dev->>GitHub: git push origin main
    GitHub->>Runner: workflow trigger
    
    Note over Runner, Reports: Job: static-analysis
    Runner->>Runner: Ubuntu 22.04環境構築
    Runner->>GitHub: actions/checkout@v4
    GitHub-->>Runner: ソースコード取得
    
    Note over Runner, Reports: Java環境セットアップ
    Runner->>Runner: actions/setup-java@v4
    Runner->>Runner: Java 17 Corretto インストール
    Runner->>Java: java -version確認
    Java-->>Runner: openjdk 17.0.9 2023-10-17 LTS
    
    Note over Runner, Reports: Maven依存関係解決
    Runner->>Maven: mvn clean compile -DskipTests
    Maven->>Java: コンパイル実行
    Java-->>Maven: コンパイル成功
    Maven-->>Runner: BUILD SUCCESS
    
    Note over Runner, Reports: 静的解析実行（並列）
    par Checkstyle Simple
        Runner->>Maven: mvn checkstyle:check -Dcheckstyle.config.location=checkstyle-simple.xml
        Maven->>Java: Checkstyle実行
        Java-->>Maven: xxx件違反検出
        Maven-->>Runner: BUILD FAILURE
    and PMD Check
        Runner->>Maven: mvn pmd:check
        Maven->>Java: PMD実行
        Java-->>Maven: xxx件違反検出
        Maven-->>Runner: BUILD FAILURE
    and SpotBugs Check
        Runner->>Maven: mvn spotbugs:check
        Maven->>Java: SpotBugs実行
        Java-->>Maven: xxx件バグ検出
        Maven-->>Runner: BUILD FAILURE
    end
    
    Note over Runner, Reports: 厳格品質チェック
    Runner->>Maven: mvn checkstyle:check -Dcheckstyle.config.location=checkstyle-strict.xml
    Maven->>Java: 厳格Checkstyle実行
    Java-->>Maven: xxx件違反検出
    Maven-->>Runner: BUILD FAILURE
    
    Note over Runner, Reports: レポート生成
    Runner->>Maven: mvn checkstyle:checkstyle pmd:pmd spotbugs:spotbugs
    Maven->>Java: レポート生成実行
    Java->>Reports: HTMLレポート生成
    Reports-->>Java: 生成完了
    Java-->>Maven: SUCCESS
    Maven-->>Runner: BUILD SUCCESS
    
    Note over Runner, Reports: アーティファクト保存
    Runner->>Runner: actions/upload-artifact@v4
    Runner->>GitHub: レポートアップロード
    GitHub-->>Runner: アーティファクト保存完了
    
    Note over Runner, Reports: 結果通知
    Runner->>GitHub: ワークフロー結果更新
    GitHub->>Dev: ❌ CI/CD失敗通知
    GitHub->>Dev: 📊 品質レポート利用可能
```

## 5. 統合テストフロー（comprehensive-integration-test.sh）

```mermaid
sequenceDiagram
    participant Dev as 開発者
    participant Script as comprehensive-integration-test.sh
    participant Maven as Maven
    participant Java as Java/JVM
    participant Files as ファイルシステム
    participant Reports as Reports
    
    Note over Dev, Reports: 統合テスト開始
    Dev->>Script: ./comprehensive-integration-test.sh
    Script->>Script: 開始時刻記録
    Script->>Files: projectディレクトリ確認
    Files-->>Script: 存在確認
    
    Note over Script, Reports: Phase 1: 環境検証
    Script->>Java: java -version
    Java-->>Script: Java 17.0.9 LTS
    Script->>Maven: mvn -version
    Maven-->>Script: Apache Maven 3.9.x
    Script->>Files: 設定ファイル存在確認
    Files-->>Script: checkstyle-simple.xml ✓, pmd-basic.xml ✓
    
    Note over Script, Reports: Phase 2: プロジェクトクリーンアップ
    Script->>Maven: mvn clean
    Maven->>Files: target/ディレクトリ削除
    Files-->>Maven: クリーンアップ完了
    Maven-->>Script: BUILD SUCCESS
    
    Note over Script, Reports: Phase 3: コンパイル検証
    Script->>Maven: mvn compile -DskipTests -q
    Maven->>Java: コンパイル実行
    Java->>Files: クラスファイル生成
    Files-->>Java: コンパイル完了
    Java-->>Maven: 成功
    Maven-->>Script: BUILD SUCCESS
    
    Note over Script, Reports: Phase 4: フォーマット状態確認
    Script->>Maven: mvn fmt:check
    Maven->>Java: フォーマットチェック
    Java->>Files: フォーマット状態分析
    Files-->>Java: xxxファイル確認
    Java-->>Maven: フォーマット不適合
    Maven-->>Script: BUILD FAILURE
    
    Note over Script, Reports: Phase 5: 自動フォーマット実行
    Script->>Maven: mvn fmt:format
    Maven->>Java: フォーマット適用
    Java->>Files: xxxファイル自動修正
    Files-->>Java: フォーマット適用完了
    Java-->>Maven: 修正完了
    Maven-->>Script: BUILD SUCCESS
    
    Note over Script, Reports: Phase 6: 静的解析実行（並列）
    par Checkstyle Simple
        Script->>Maven: mvn checkstyle:check (simple)
        Maven-->>Script: xxx件違反、BUILD FAILURE
    and Checkstyle Strict
        Script->>Maven: mvn checkstyle:check (strict)
        Maven-->>Script: xxx件違反、BUILD FAILURE
    and PMD Basic
        Script->>Maven: mvn pmd:check
        Maven-->>Script: xxx件違反、BUILD FAILURE
    and SpotBugs
        Script->>Maven: mvn spotbugs:check
        Maven-->>Script: xxx件バグ、BUILD FAILURE
    end
    
    Note over Script, Reports: Phase 7: レポート生成
    Script->>Maven: mvn checkstyle:checkstyle pmd:pmd spotbugs:spotbugs
    Maven->>Java: レポート生成
    Java->>Reports: HTMLレポート作成
    Reports-->>Java: 生成完了
    Java-->>Maven: SUCCESS
    Maven-->>Script: BUILD SUCCESS
    
    Note over Script, Reports: Phase 8: 結果集計・表示
    Script->>Script: 実行時間計算
    Script->>Script: 品質統計計算（xxx件総問題）
    Script->>Files: レポートファイル確認
    Files-->>Script: target/site/checkstyle.html等
    
    Script->>Dev: 📊 統合テスト結果表示
    Script->>Dev: ⏱️ 実行時間: XXs
    Script->>Dev: 🔍 総問題数: xxx件
    Script->>Dev: 📂 レポート場所表示
    Script->>Dev: ❌ 品質ゲート: 改善必要
```

## 6. エラーハンドリングフロー

```mermaid
sequenceDiagram
    participant System as システム
    participant Error as エラーハンドラー
    participant Log as ログ
    participant Dev as 開発者
    participant Fix as 修正処理
    
    Note over System, Fix: 一般的なエラーハンドリング
    System->>System: 処理実行中
    System->>Error: エラー発生
    
    Error->>Error: エラー種別判定
    
    alt Java環境エラー
        Error->>Log: LinkageError: クラスファイルバージョン不一致
        Error->>Dev: Java 17環境確認要請
        Dev->>Fix: export JAVA_HOME=/Library/Java/JavaVirtualMachines/amazon-corretto-17.jdk/Contents/Home
        Fix->>System: 環境修正
        System->>System: 処理再実行
        
    else Maven依存関係エラー
        Error->>Log: 依存関係解決失敗
        Error->>Dev: mvn clean install実行要請
        Dev->>Fix: mvn clean install -U
        Fix->>System: 依存関係更新
        System->>System: 処理再実行
        
    else 設定ファイルエラー
        Error->>Log: XML構文エラー
        Error->>Dev: 設定ファイル確認要請
        Dev->>Fix: checkstyle-simple.xml修正
        Fix->>System: 設定修正
        System->>System: 処理再実行
        
    else ツール実行エラー
        Error->>Log: Plugin execution failed
        Error->>Dev: pom.xml設定確認要請
        Dev->>Fix: plugin version更新
        Fix->>System: 設定修正
        System->>System: 処理再実行
        
    else ファイルアクセスエラー
        Error->>Log: ファイル読み書きエラー
        Error->>Dev: 権限・パス確認要請
        Dev->>Fix: chmod 755 またはパス修正
        Fix->>System: アクセス権修正
        System->>System: 処理再実行
    end
    
    Note over System, Fix: エラー解決確認
    System->>Dev: 処理正常完了
    Dev->>Log: 解決ログ記録
```

## 7. ツール間連携フロー

```mermaid
sequenceDiagram
    participant Format as Google Java Format
    participant Checkstyle as Checkstyle
    participant PMD as PMD
    participant SpotBugs as SpotBugs
    participant Reports as 統合レポート
    participant Quality as 品質ゲート
    
    Note over Format, Quality: ツール実行順序と連携
    
    Note over Format, Quality: Phase 1: コードフォーマット
    Format->>Format: Googleスタイル適用
    Format->>Checkstyle: フォーマット済みコード提供
    Format->>PMD: フォーマット済みコード提供
    
    Note over Format, Quality: Phase 2: 構文・規約チェック
    Checkstyle->>Checkstyle: コーディング規約検証
    Checkstyle->>Reports: xxx件違反レポート
    PMD->>PMD: コード品質分析
    PMD->>Reports: xxx件違反レポート
    
    Note over Format, Quality: Phase 3: バグ検出
    SpotBugs->>SpotBugs: バイトコード解析
    SpotBugs->>Reports: xxx件バグレポート
    
    Note over Format, Quality: Phase 4: 結果統合
    Reports->>Reports: 違反情報統合
    Reports->>Quality: 総問題数: xxx件
    
    Quality->>Quality: 品質基準判定
    
    alt 品質基準未達成（問題数 > 0）
        Quality->>Quality: ❌ 品質ゲート: 失敗
        Quality->>Format: 再フォーマット推奨
        Quality->>Checkstyle: 規約修正推奨
        Quality->>PMD: コード改善推奨
        Quality->>SpotBugs: バグ修正推奨
    else 品質基準達成
        Quality->>Quality: ✅ 品質ゲート: 合格
        Quality->>Reports: リリース承認
    end
    
    Note over Format, Quality: Phase 5: 継続的改善
    Quality->>Format: フォーマット設定調整推奨
    Quality->>Checkstyle: ルール設定調整推奨
    Quality->>PMD: 解析レベル調整推奨
    Quality->>SpotBugs: 検出レベル調整推奨
```

## まとめ

この包括的なシーケンス図は、プロジェクトにおける静的解析ツールの完全な動作フローを表現しています。

### 主要なフロー
1. **手動実行**: 開発者による対話式実行
2. **Pre-commit**: Git コミット時の自動チェック  
3. **CI/CD**: GitHub Actions による継続的品質管理
4. **統合テスト**: 包括的な品質検証
5. **エラーハンドリング**: 問題発生時の対応手順
6. **ツール間連携**: 各ツールの協調動作

### 検出される品質問題
- **Checkstyle**: xxx件のコーディング規約違反
- **PMD**: xxx件のコード品質問題  
- **SpotBugs**: xxx件の潜在的バグ
- **総計**: xxx件の改善すべき問題

### 次のステップ
1. 段階的な品質改善計画の実行
2. チーム固有ルールの追加
3. 継続的な設定最適化
4. 品質メトリクスの定期的な見直し
