# 静的解析ツール特徴・設定・カスタマイズ手順図

## 概要
DroneInventorySystemプロジェクトで利用している静的解析ツールの特徴、設定方法、およびカスタマイズ手順を図解したドキュメントです。

**🎨 2024年6月17日更新**: prettier-java + Eclipse統合フォーマット環境対応、タブインデント統一設定を追加

## 静的解析ツール全体構成図

```mermaid
graph TB
    subgraph "静的解析エコシステム"
        SA[静的解析システム]
        
        subgraph "統合フォーマットレイヤー 🎨 NEW"
            IFS[format-and-check.sh<br/>統合フォーマットスクリプト]
            ST[Space→Tab変換]
            PJ[Prettier Java<br/>npm + prettier-plugin-java]
            EF[Eclipse Code Formatter<br/>formatter-maven-plugin]
        end
        
        subgraph "コード品質チェック"
            CS[Checkstyle]
            PMD[PMD]
            SB[SpotBugs]
        end
        
        subgraph "レガシーフォーマット"
            GJF[Google Java Format<br/>※タブ対応不可]
        end
        
        subgraph "自動化レイヤー"
            PC[Pre-commit Hook]
            CI[GitHub Actions]
            MS[Maven Scripts]
        end
        
        subgraph "設定ファイル 🎨 UPDATED"
            CSC[checkstyle-simple.xml<br/>checkstyle-strict.xml]
            PMDC[pmd-basic.xml]
            EFC[eclipse-format.xml<br/>TAB設定統合]
            PRC[.prettierrc<br/>useTabs: true]
            PCJ[package.json<br/>prettier + prettier-plugin-java]
            POM[pom.xml<br/>formatter-maven-plugin統合]
        end
        
        subgraph "クロスIDE設定 🎨 NEW"
            VSC[.vscode/settings.json<br/>Prettier統合設定]
            EC[.editorconfig<br/>タブ設定統一]
            EPR[Eclipse設定手順書.md]
        end
    end
    
    SA --> IFS
    IFS --> ST
    IFS --> PJ
    IFS --> EF
    
    SA --> CS
    SA --> PMD
    SA --> SB
    SA --> GJF
    
    ST --> EFC
    PJ --> PRC
    PJ --> PCJ
    EF --> EFC
    EF --> POM
    
    CS --> CSC
    PMD --> PMDC
    GJF --> POM
    SB --> POM
    
    PJ --> VSC
    EF --> EPR
    ST --> EC
    
    PC --> IFS
    CI --> IFS
    MS --> IFS
```

## 各ツールの特徴と役割マトリックス

```mermaid
graph LR
    subgraph "静的解析ツール特徴マトリックス"
        
        subgraph "Checkstyle特徴"
            CS1[コーディング規約チェック]
            CS2[命名規則検証]
            CS3[インデント・スペース検証]
            CS4[JavaDoc検証]
            CS5[デザインパターン違反検出]
        end
        
        subgraph "PMD特徴"
            PMD1[コード複雑度分析]
            PMD2[潜在的バグ検出]
            PMD3[パフォーマンス問題検出]
            PMD4[セキュリティ脆弱性検出]
            PMD5[設計原則違反検出]
        end
        
        subgraph "SpotBugs特徴"
            SB1[バイトコード解析]
            SB2[null pointer例外検出]
            SB3[リソースリーク検出]
            SB4[スレッドセーフティ問題検出]
            SB5[セキュリティ脆弱性検出]
        end
        
        subgraph "Prettier Java特徴 🎨 NEW"
            PJ1[タブベースフォーマット]
            PJ2[Node.js/npm生態系統合]
            PJ3[設定ファイル駆動フォーマット]
            PJ4[VS Code拡張統合]
            PJ5[prettier-plugin-java利用]
        end
        
        subgraph "Eclipse Code Formatter特徴"
            EF1[Eclipse設定ファイル適用]
            EF2[詳細フォーマットルール]
            EF3[タブインデント完全対応]
            EF4[Maven Plugin統合]
            EF5[エンタープライズ品質保証]
        end
        
        subgraph "Google Java Format特徴 ⚠️ LEGACY"
            GJF1[自動コードフォーマット]
            GJF2[Googleスタイル準拠]
            GJF3[スペースインデント固定]
            GJF4[改行・スペース統一]
            GJF5[インポート文整理]
        end
    end
```
        end
    end
```

## ツール設定レベル構成図

```mermaid
graph TD
    subgraph "設定レベル階層"
        
        subgraph "Level 1: 基本設定"
            L1A[checkstyle-simple.xml<br/>警告レベル設定]
            L1B[pmd-basic.xml<br/>基本ルールセット]
            L1C[SpotBugs基本設定<br/>medium effort]
        end
        
        subgraph "Level 2: 厳格設定"
            L2A[checkstyle-strict.xml<br/>エラーレベル設定]
            L2B[PMD拡張ルール<br/>全カテゴリ有効]
            L2C[SpotBugs最大設定<br/>max effort]
        end
        
        subgraph "Level 3: カスタム設定"
            L3A[プロジェクト固有ルール]
            L3B[チーム独自基準]
            L3C[レガシーコード対応]
        end
        
        L1A --> L2A
        L1B --> L2B
        L1C --> L2C
        
        L2A --> L3A
        L2B --> L3B
        L2C --> L3C
    end
```

## Checkstyle設定カスタマイズフロー

```mermaid
flowchart TD
    START[Checkstyleカスタマイズ開始]
    
    START --> A1[現在の違反数確認<br/>mvn checkstyle:check]
    A1 --> A2[違反の重要度分析]
    
    A2 --> B1{違反レベル選択}
    B1 -->|簡単| B2[checkstyle-simple.xml使用<br/>警告レベル]
    B1 -->|厳格| B3[checkstyle-strict.xml使用<br/>エラーレベル]
    
    B2 --> C1[基本ルール適用]
    B3 --> C2[全ルール適用]
    
    C1 --> D1[段階的ルール追加]
    C2 --> D2[カスタムルール作成]
    
    D1 --> E1[チーム基準設定]
    D2 --> E1
    
    E1 --> F1[pom.xml設定更新]
    F1 --> G1[ビルド失敗設定]
    G1 --> H1[検証実行]
    
    H1 --> I1{合格基準達成?}
    I1 -->|No| J1[ルール調整]
    I1 -->|Yes| K1[設定確定]
    
    J1 --> A2
    K1 --> END[カスタマイズ完了]
```

## PMD設定カスタマイズフロー

```mermaid
flowchart TD
    START[PMDカスタマイズ開始]
    
    START --> A1[ルールセット選択]
    A1 --> B1{プロジェクト特性}
    
    B1 -->|Web Application| B2[rulesets/java/quickstart.xml<br/>基本セット]
    B1 -->|Enterprise| B3[rulesets/java/design.xml<br/>設計重視]
    B1 -->|Security重視| B4[rulesets/java/security.xml<br/>セキュリティ重視]
    
    B2 --> C1[基本ルール適用]
    B3 --> C2[設計ルール適用]
    B4 --> C3[セキュリティルール適用]
    
    C1 --> D1[カスタムルール追加]
    C2 --> D1
    C3 --> D1
    
    D1 --> E1[除外設定<br/>exclude patterns]
    E1 --> F1[重要度設定<br/>priority levels]
    F1 --> G1[pmd-basic.xml更新]
    
    G1 --> H1[検証実行<br/>mvn pmd:check]
    H1 --> I1{結果確認}
    
    I1 -->|調整必要| J1[ルール調整]
    I1 -->|OK| K1[設定確定]
    
    J1 --> E1
    K1 --> END[カスタマイズ完了]
```

## SpotBugs設定カスタマイズフロー

```mermaid
flowchart TD
    START[SpotBugsカスタマイズ開始]
    
    START --> A1[Effort Level設定]
    A1 --> B1{解析精度選択}
    
    B1 -->|高速| B2[min effort<br/>基本バグのみ]
    B1 -->|標準| B3[default effort<br/>一般的バグ]
    B1 -->|詳細| B4[max effort<br/>全バグ検出]
    
    B2 --> C1[基本設定適用]
    B3 --> C2[標準設定適用]
    B4 --> C3[最大設定適用]
    
    C1 --> D1[Bug Categories選択]
    C2 --> D1
    C3 --> D1
    
    D1 --> E1{重要カテゴリ選択}
    E1 -->|Correctness| E2[論理エラー検出]
    E1 -->|Security| E3[セキュリティ問題検出]
    E1 -->|Performance| E4[パフォーマンス問題検出]
    E1 -->|Multithreaded| E5[並行処理問題検出]
    
    E2 --> F1[pom.xml更新]
    E3 --> F1
    E4 --> F1
    E5 --> F1
    
    F1 --> G1[検証実行<br/>mvn spotbugs:check]
    G1 --> H1{結果確認}
    
    H1 -->|調整必要| I1[設定調整]
    H1 -->|OK| J1[設定確定]
    
    I1 --> D1
    J1 --> END[カスタマイズ完了]
```

## 統合フォーマット設定カスタマイズフロー 🎨 NEW

```mermaid
flowchart TD
    START[統合フォーマット設定開始]
    
    START --> A1[タブインデント統一方針決定]
    A1 --> A2[tabWidth: 4, useTabs: true]
    
    A2 --> B1[環境別設定ファイル作成]
    B1 --> B2[.prettierrc<br/>Prettier設定]
    B1 --> B3[eclipse-format.xml<br/>Eclipse設定]
    B1 --> B4[.editorconfig<br/>エディタ統一設定]
    B1 --> B5[.vscode/settings.json<br/>VS Code設定]
    
    B2 --> C1[Node.js環境セットアップ]
    C1 --> C2[package.json作成]
    C2 --> C3[prettier + prettier-plugin-java<br/>依存関係追加]
    
    B3 --> D1[Eclipse設定手順書作成]
    D1 --> D2[プロファイル作成手順]
    D2 --> D3[タブ設定詳細化]
    
    B4 --> E1[クロスエディタ設定]
    E1 --> E2[root = true<br/>*.java = tab]
    
    B5 --> F1[VS Code Prettier統合]
    F1 --> F2[formatOnSave: true<br/>editor.insertSpaces: false]
    
    C3 --> G1[統合スクリプト作成]
    D3 --> G1
    E2 --> G1
    F2 --> G1
    
    G1 --> G2[format-and-check.sh<br/>統合実行スクリプト]
    G2 --> G3[Phase1: Space→Tab変換]
    G3 --> G4[Phase2: Prettier Java実行]
    G4 --> G5[Phase3: Eclipse Formatter実行]
    G5 --> G6[Phase4: 品質チェック実行]
    
    G6 --> H1[統合テスト]
    H1 --> H2[47個Javaファイル処理確認]
    H2 --> H3[タブインデント統一確認]
    H3 --> H4[Eclipse + VS Code動作確認]
    
    H4 --> I1{統合テスト結果}
    I1 -->|失敗| J1[設定調整]
    I1 -->|成功| K1[統合フォーマット環境完成]
    
    J1 --> B1
    K1 --> END[統合設定完了]
```

## Google Java Format設定カスタマイズフロー ⚠️ LEGACY

```mermaid
flowchart TD
    START[Google Java Formatカスタマイズ開始]
    
    START --> WARNING[⚠️ タブインデント非対応警告]
    WARNING --> A1[フォーマットスタイル選択]
    A1 --> B1{スタイル選択}
    
    B1 -->|Google| B2[GOOGLE style<br/>Googleコーディング規約<br/>※スペースのみ]
    B1 -->|AOSP| B3[AOSP style<br/>Android Open Source Project<br/>※スペースのみ]
    
    B2 --> C1[基本設定適用]
    B3 --> C2[AOSP設定適用]
    
    C1 --> RECOMMEND[💡 推奨: 統合フォーマット環境への移行]
    C2 --> RECOMMEND
    
    RECOMMEND --> D1[現在の設定維持 or 移行選択]
    D1 --> E1{移行判断}
    
    E1 -->|移行| F1[統合フォーマット環境セットアップ]
    E1 -->|維持| G1[Google Java Format継続使用]
    
    F1 --> F2[format-and-check.sh利用]
    G1 --> G2[mvn fmt:format継続]
    
    F2 --> END[統合環境移行完了]
    G2 --> END[Google Java Format継続]
```

## 自動化設定統合フロー

```mermaid
flowchart TD
    START[自動化設定開始]
    
    START --> A1[Pre-commit Hook設定]
    A1 --> A2[.git/hooks/pre-commit作成]
    A2 --> A3[実行権限付与<br/>chmod +x]
    
    A3 --> B1[GitHub Actions設定]
    B1 --> B2[.github/workflows/static-analysis.yml作成]
    B2 --> B3[CI/CDパイプライン構築]
    
    B3 --> C1[Maven統合設定]
    C1 --> C2[pom.xmlにプラグイン統合]
    C2 --> C3[ビルドライフサイクル統合]
    
    C3 --> D1[スクリプト作成]
    D1 --> D2[manual-static-analysis.sh]
    D1 --> D3[comprehensive-integration-test.sh]
    D1 --> D4[test-static-analysis-failures.sh]
    
    D2 --> E1[統合テスト実行]
    D3 --> E1
    D4 --> E1
    
    E1 --> F1{自動化テスト結果}
    F1 -->|失敗| G1[設定調整]
    F1 -->|成功| H1[自動化完了]
    
    G1 --> A1
    H1 --> END[自動化設定完了]
```

## 設定ファイル依存関係図

```mermaid
graph TD
    subgraph "設定ファイル依存関係"
        POM[pom.xml<br/>メイン設定]
        
        subgraph "Checkstyle設定"
            CSS[checkstyle-simple.xml]
            CST[checkstyle-strict.xml]
        end
        
        subgraph "PMD設定"
            PMDB[pmd-basic.xml]
        end
        
        subgraph "フォーマット設定"
            EF[eclipse-format.xml]
        end
        
        subgraph "自動化設定"
            PC[pre-commit]
            GHA[static-analysis.yml]
        end
        
        POM --> CSS
        POM --> CST
        POM --> PMDB
        POM --> EF
        
        PC --> POM
        GHA --> POM
        
        CSS -.-> CST
        POM -.-> PMDB
        POM -.-> EF
    end
```

## ツール実行順序とタイミング

```mermaid
gantt
    title 静的解析ツール実行タイミング
    dateFormat X
    axisFormat %s
    
    section Pre-commit
    フォーマット実行          :a1, 0, 1s
    Checkstyle検証           :a2, after a1, 1s
    PMD検証                  :a3, after a2, 1s
    SpotBugs検証             :a4, after a3, 1s
    
    section Build Time
    コンパイル               :b1, 0, 2s
    Checkstyle (simple)      :b2, after b1, 1s
    PMD基本チェック          :b3, after b2, 1s
    SpotBugs基本チェック     :b4, after b3, 1s
    
    section CI/CD
    環境構築                 :c1, 0, 3s
    Checkstyle (strict)      :c2, after c1, 2s
    PMD全ルール              :c3, after c2, 2s
    SpotBugs最大解析         :c4, after c3, 3s
    レポート生成             :c5, after c4, 1s
```

## トラブルシューティングフロー

```mermaid
flowchart TD
    START[問題発生]
    
    START --> A1{問題の種類}
    
    A1 -->|Java環境| B1[Java version確認<br/>java -version]
    A1 -->|依存関係| B2[Maven依存関係確認<br/>mvn dependency:tree]
    A1 -->|設定ファイル| B3[設定ファイル構文確認]
    A1 -->|実行エラー| B4[ログ確認]
    
    B1 --> C1[JAVA_HOME設定<br/>Java 17 Corretto]
    B2 --> C2[依存関係解決<br/>mvn clean install]
    B3 --> C3[XMLスキーマ確認]
    B4 --> C4[詳細エラーログ分析]
    
    C1 --> D1[環境変数永続化<br/>~/.zshrc更新]
    C2 --> D2[プロジェクトクリーン<br/>mvn clean]
    C3 --> D3[設定ファイル修正]
    C4 --> D4[問題箇所特定]
    
    D1 --> E1[再テスト実行]
    D2 --> E1
    D3 --> E1
    D4 --> E1
    
    E1 --> F1{解決確認}
    F1 -->|No| G1[上位エスカレーション]
    F1 -->|Yes| H1[解決完了]
    
    G1 --> START
    H1 --> END[問題解決]
```

## まとめ

このドキュメントは、DroneInventorySystemプロジェクトにおける静的解析ツールの包括的な設定・カスタマイズガイドです。各ツールの特徴を理解し、プロジェクトの要件に応じて適切な設定を選択・カスタマイズすることで、高品質なコードベースの維持が可能になります。

### 次のステップ
1. 各ツールの設定レベルを段階的に引き上げ
2. チーム固有のルール追加
3. 継続的な品質改善プロセスの確立
4. 定期的な設定見直しとメンテナンス
