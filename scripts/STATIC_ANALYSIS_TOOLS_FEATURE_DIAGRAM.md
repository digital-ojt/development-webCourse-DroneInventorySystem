# 静的解析ツール特徴・設定・カスタマイズ手順図

## 概要
プロジェクトで利用している静的解析ツールの特徴、設定方法、およびカスタマイズ手順を図解したドキュメントです。

**🎨 2024年12月21日更新**: IntelliJ IDEA統合対応、JDK 17環境強制、SpotBugs互換性問題対応を追加  
**🎨 2024年6月17日更新**: prettier-java + Eclipse統合フォーマット環境対応、タブインデント統一設定を追加

## 静的解析ツール全体構成図

```mermaid
graph TB
    subgraph "静的解析エコシステム"
        SA[静的解析システム]
        
        subgraph "統合フォーマットレイヤー 🎨 NEW"
            IFS[config/format-and-check.sh<br/>統合フォーマットスクリプト]
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
        
        subgraph "クロスIDE設定 🎨 UPDATED"
            VSC[.vscode/settings.json<br/>Prettier統合設定]
            EC[.editorconfig<br/>タブ設定統一]
            EPR[Eclipse設定手順書.md]
            IJC[IntelliJ IDEA設定<br/>Code Style設定]
            PRT[pre-commit-result.txt<br/>IDE共通エラー表示]
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
    ST --> IJC
    
    PC --> IFS
    CI --> IFS
    MS --> IFS
    
    IFS --> PRT
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

## IntelliJ IDEA統合設定フロー 🎨 NEW

```mermaid
flowchart TD
    START[IntelliJ IDEA統合設定開始]
    
    START --> A1[Code Style設定]
    A1 --> A2[Settings → Editor → Code Style → Java]
    A2 --> A3[Tab size: 4, Use tab character: ON]
    
    A3 --> B1[Git統合設定]
    B1 --> B2[Version Control → Git設定]    
    B2 --> B3[Pre-commit hook有効化]
    
    B3 --> C1[プロジェクト設定]
    C1 --> C2[Project Structure → Project SDK: JDK 17]
    C2 --> C3[Maven設定確認]
    
    C3 --> D1[pre-commitテスト]
    D1 --> D2[Git → Commit Changes]
    D2 --> D3[pre-commitフック実行]
    
    D3 --> E1{実行結果}
    E1 -->|成功| F1[IntelliJ IDEA統合完了]
    E1 -->|失敗| G1[pre-commit-result.txt確認]
    
    G1 --> G2[Project toolwindowで詳細確認]
    G2 --> G3[エラー修正]
    G3 --> D1
    
    F1 --> H1[内蔵Terminal設定]
    H1 --> H2[./config/format-and-check.sh手動実行可能]
    H2 --> END[IntelliJ IDEA環境構築完了]
```

## Eclipse vs IntelliJ IDEA 設定比較フロー

```mermaid
flowchart TD
    subgraph "IDE選択フロー"
        START[IDE選択]
        
        START --> CHOOSE{使用IDE選択}
        CHOOSE -->|Eclipse| ECLIP[Eclipse設定フロー]
        CHOOSE -->|IntelliJ IDEA| INTEL[IntelliJ IDEA設定フロー]
        CHOOSE -->|両方| BOTH[マルチIDE環境設定]
    end
    
    subgraph "Eclipse設定"
        ECLIP --> E1[Preferences → Java → Code Style]
        E1 --> E2[Formatter設定: eclipse-format.xml]
        E2 --> E3[Checkstyle Plugin導入]
        E3 --> E4[External Tools設定]
        E4 --> E5[EGit設定]
    end
    
    subgraph "IntelliJ IDEA設定"
        INTEL --> I1[Settings → Code Style → Java]
        I1 --> I2[Tab設定: Use tab character]
        I2 --> I3[Git統合確認]
        I3 --> I4[Terminal設定]
        I4 --> I5[Code Inspection設定]
    end
    
    subgraph "マルチIDE環境"
        BOTH --> M1[.editorconfig作成]
        M1 --> M2[統一設定ファイル配置]
        M2 --> M3[pre-commit-result.txt共通化]
        M3 --> M4[setup-pre-commit-hook.sh実行]
    end
    
    E5 --> TEST[統合テスト実行]
    I5 --> TEST
    M4 --> TEST
    
    TEST --> VERIFY{動作確認}
    VERIFY -->|成功| SUCCESS[環境構築完了]
    VERIFY -->|失敗| DEBUG[トラブルシューティング]
    
    DEBUG --> CHOOSE
    SUCCESS --> END[統合静的解析システム運用開始]
```

## 統合フォーマット設定カスタマイズフロー 🎨 UPDATED

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
    B1 --> B6[IntelliJ IDEA設定<br/>Code Style設定]
    
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
    
    B6 --> G1[IntelliJ IDEA統合]
    G1 --> G2[Tab character使用設定<br/>Git統合確認]
    
    C3 --> H1[統合スクリプト作成]
    D3 --> H1
    E2 --> H1
    F2 --> H1
    G2 --> H1
    
    H1 --> H2[config/format-and-check.sh<br/>統合実行スクリプト]
    H2 --> H3[JDK 17環境強制チェック]
    H3 --> H4[Phase1: Space→Tab変換]
    H4 --> H5[Phase2: Prettier Java実行]
    H5 --> H6[Phase3: Eclipse Formatter実行]
    H6 --> H7[Phase4: 品質チェック実行]
    H7 --> H8[SpotBugs互換性問題対応]
    
    H8 --> I1[統合テスト]
    I1 --> I2[47個Javaファイル処理確認]
    I2 --> I3[タブインデント統一確認]
    I3 --> I4[Eclipse + IntelliJ IDEA + VS Code動作確認]
    I4 --> I5[pre-commit-result.txt生成確認]
    
    I5 --> J1{統合テスト結果}
    J1 -->|失敗| K1[設定調整]
    J1 -->|成功| L1[統合フォーマット環境完成]
    
    K1 --> B1
    L1 --> END[統合設定完了]
```

## JDK 17環境強制・SpotBugs互換性対応フロー 🎨 NEW

```mermaid
flowchart TD
    START[静的解析システム実行開始]
    
    START --> A1[現在のJava環境確認]
    A1 --> A2[java -version実行]
    A2 --> A3[バージョン解析]
    
    A3 --> B1{Java Version判定}
    B1 -->|Java 17| B2[✅ JDK 17環境確認]
    B1 -->|Other| B3[⚠️ JDK 17以外検出]
    
    B2 --> C1[JAVA_HOME設定]
    B3 --> C2[警告表示・処理継続]
    
    C1 --> C3[java.home プロパティ取得]
    C3 --> C4[Maven用JAVA_HOME設定]
    C2 --> C4
    
    C4 --> D1[Maven検証実行]
    D1 --> D2[mvn -version確認]
    D2 --> D3[Java 17での動作確認]
    
    D3 --> E1[静的解析ツール実行]
    E1 --> E2[Checkstyle実行]
    E2 --> E3[PMD実行]
    E3 --> E4[SpotBugs実行試行]
    
    E4 --> F1{SpotBugs実行結果}
    F1 -->|成功| F2[✅ SpotBugs: 合格]
    F1 -->|失敗| F3[エラー内容詳細分析]
    
    F3 --> G1{エラー種別判定}
    G1 -->|Unsupported class file major version| G2[Java 21互換性問題検出]
    G1 -->|Other Error| G3[通常のSpotBugsエラー]
    
    G2 --> G4[⚠️ SpotBugs: Java 21クラスファイル互換性問題]
    G4 --> G5[JDK 17環境でもJava 21クラス参照を検出]
    G5 --> G6[SpotBugsスキップ・処理継続]
    G6 --> G7[スキップ理由のログ出力]
    
    G3 --> G8[通常のSpotBugsエラー処理]
    G8 --> G9[エラー詳細表示]
    G9 --> G10[処理失敗]
    
    F2 --> H1[全ツール結果統合]
    G7 --> H1
    G10 --> H2[エラー結果統合]
    
    H1 --> I1[✅ 統合静的解析成功]
    H2 --> I2[❌ 統合静的解析失敗]
    
    I1 --> J1[pre-commit-result.txt生成]
    I2 --> J1
    J1 --> J2[成功/失敗の詳細情報記録]
    J2 --> END[結果をIDEに通知]
```

## 複数JDKベンダー対応フロー

```mermaid
flowchart TD
    START[Java環境検出開始]
    
    START --> A1[システムのjavaコマンド実行]
    A1 --> A2[java -XshowSettings:properties -version]
    A2 --> A3[Java実行環境詳細取得]
    
    A3 --> B1[ベンダー情報解析]
    B1 --> B2{JDKベンダー判定}
    
    B2 -->|Amazon Corretto| C1[Amazon Corretto 17検出]
    B2 -->|Eclipse Temurin| C2[Eclipse Temurin 17検出]
    B2 -->|Oracle JDK| C3[Oracle JDK 17検出]
    B2 -->|OpenJDK| C4[OpenJDK 17検出]
    B2 -->|その他| C5[その他JDK 17検出]
    
    C1 --> D1[/opt/homebrew/Cellar/openjdk@17/]
    C2 --> D2[/Library/Java/JavaVirtualMachines/temurin-17.jdk/]
    C3 --> D3[/Library/Java/JavaVirtualMachines/jdk-17.oracle.com/]
    C4 --> D4[/usr/lib/jvm/java-17-openjdk/]
    C5 --> D5[java.home プロパティから動的取得]
    
    D1 --> E1[JAVA_HOME設定]
    D2 --> E1
    D3 --> E1
    D4 --> E1
    D5 --> E1
    
    E1 --> F1[Maven実行環境設定]
    F1 --> F2[export JAVA_HOME]
    F2 --> F3[mvn -version検証]
    
    F3 --> G1{Maven Java版確認}
    G1 -->|Java 17| G2[✅ Maven Java 17環境確認]
    G1 -->|Other| G3[⚠️ Maven Java版不整合]
    
    G2 --> H1[静的解析実行準備完了]
    G3 --> H2[警告表示・処理継続]
    
    H1 --> END[Maven + Java 17環境構築完了]
    H2 --> END
```

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
    
    F1 --> F2[config/format-and-check.sh利用]
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

## トラブルシューティングフロー 🎨 UPDATED

```mermaid
flowchart TD
    START[問題発生]
    
    START --> A1{問題の種類}
    
    A1 -->|Java環境| B1[Java version確認<br/>java -version]
    A1 -->|依存関係| B2[Maven依存関係確認<br/>mvn dependency:tree]
    A1 -->|設定ファイル| B3[設定ファイル構文確認]
    A1 -->|実行エラー| B4[ログ確認]
    A1 -->|IDE固有| B5[IDE別問題確認]
    
    B1 --> C1[JAVA_HOME設定<br/>Java 17 Corretto]
    B2 --> C2[依存関係解決<br/>mvn clean install]
    B3 --> C3[XMLスキーマ確認]
    B4 --> C4[詳細エラーログ分析]
    B5 --> C5{IDE種別確認}
    
    C5 -->|Eclipse| C6[Package Explorer で pre-commit-result.txt 確認]
    C5 -->|IntelliJ IDEA| C7[Project toolwindow で pre-commit-result.txt 確認]
    C5 -->|VS Code| C8[Explorer で pre-commit-result.txt 確認]
    
    C1 --> D1[環境変数永続化<br/>~/.zshrc更新]
    C2 --> D2[プロジェクトクリーン<br/>mvn clean]
    C3 --> D3[設定ファイル修正]
    C4 --> D4[問題箇所特定]
    C6 --> D5[Eclipse EGit設定確認]
    C7 --> D6[IntelliJ IDEA Git統合確認]
    C8 --> D7[VS Code Git拡張確認]
    
    D1 --> E1[再テスト実行]
    D2 --> E1
    D3 --> E1
    D4 --> E1
    D5 --> E1
    D6 --> E1
    D7 --> E1
    
    E1 --> F1{解決確認}
    F1 -->|No| G1[上位エスカレーション<br/>GitHub Issue作成]
    F1 -->|Yes| H1[解決完了・記録]
    
    G1 --> G2[問題詳細・環境情報収集]
    G2 --> G3[再現手順作成]
    G3 --> START
    
    H1 --> H2[解決手順のドキュメント化]
    H2 --> END[問題解決完了]
```

## IDE別エラー対応フロー

```mermaid
flowchart TD
    subgraph "Eclipse エラー対応"
        ECL_START[Eclipse でエラー発生]
        ECL_START --> ECL_1[コミット時に無言ダイアログ]
        ECL_1 --> ECL_2[Package Explorer 確認]
        ECL_2 --> ECL_3[pre-commit-result.txt 表示]
        ECL_3 --> ECL_4[詳細エラー内容確認]
        ECL_4 --> ECL_5[ファイル修正]
        ECL_5 --> ECL_6[再コミット実行]
    end
    
    subgraph "IntelliJ IDEA エラー対応"
        IJ_START[IntelliJ IDEA でエラー発生]
        IJ_START --> IJ_1[Git統合でコミット失敗]
        IJ_1 --> IJ_2[Project toolwindow 確認]
        IJ_2 --> IJ_3[pre-commit-result.txt 表示]
        IJ_3 --> IJ_4[内蔵ターミナルで詳細確認]
        IJ_4 --> IJ_5[Quick Fix で自動修正]
        IJ_5 --> IJ_6[再コミット実行]
    end
    
    subgraph "VS Code エラー対応"
        VS_START[VS Code でエラー発生]
        VS_START --> VS_1[Source Control でエラー]
        VS_1 --> VS_2[Explorer で結果確認]
        VS_2 --> VS_3[pre-commit-result.txt 表示]
        VS_3 --> VS_4[統合ターミナルで手動実行]
        VS_4 --> VS_5[Prettier 自動修正]
        VS_5 --> VS_6[再コミット実行]
    end
    
    ECL_6 --> SUCCESS[コミット成功]
    IJ_6 --> SUCCESS
    VS_6 --> SUCCESS
    
    SUCCESS --> END[IDE別エラー対応完了]
```

## まとめ

このドキュメントは、プロジェクトにおける統合静的解析システムの包括的な設定・カスタマイズガイドです。

### 🎯 主要な成果

1. **Eclipse + IntelliJ IDEA + VS Code 統合対応**
   - どのIDEを選択しても同じ品質ゲートを通過
   - IDE固有の操作方法に対応した結果表示

2. **JDK 17環境の強制統一**
   - 複数JDKベンダーの自動検出・対応
   - Maven実行時のJAVA_HOME統一設定

3. **タブインデント統一フォーマット**
   - Space→Tab変換の前処理
   - Prettier Java + Eclipse Formatter統合
   - クロスIDE設定ファイル管理

4. **SpotBugs互換性問題の解決**
   - Java 21クラスファイル互換性エラーの自動検出
   - エラー時のスキップ処理とログ出力

5. **統合エラーハンドリング**
   - pre-commit-result.txt による詳細エラー表示
   - IDE別のトラブルシューティングフロー

### 🔧 技術的特徴

- **柔軟性**: 各ツールの設定レベルを段階的に調整可能
- **拡張性**: 新しいツールやルールの追加が容易
- **保守性**: 設定ファイルの依存関係を明確化
- **運用性**: 自動化とマニュアル実行の両方に対応

### 📈 品質向上効果

```mermaid
graph LR
    subgraph "導入前"
        A1[IDE固有の設定] --> A2[品質基準の差]
        A2 --> A3[コードレビューの負荷]
    end
    
    subgraph "導入後"
        B1[統一品質ゲート] --> B2[自動品質チェック]
        B2 --> B3[高品質コードベース]
    end
    
    A3 --> B1
    B3 --> C1[継続的品質改善]
```

### 次のステップ

1. **品質基準の段階的向上**
   - checkstyle-simple.xml から checkstyle-strict.xml への移行
   - PMD・SpotBugsルールの段階的厳格化

2. **チーム固有ルールの追加**
   - プロジェクト特有のコーディング規約
   - セキュリティ要件に応じたカスタムルール

3. **CI/CDパイプライン統合**
   - GitHub Actions との連携強化
   - 品質メトリクスの可視化

4. **定期的なメンテナンス**
   - 依存関係の更新
   - 新しいJavaバージョンへの対応
   - IDE新バージョンへの追従

このシステムにより、開発者は好みのIDEを使いながら、統一された高品質なコードベースを維持できるようになります。
