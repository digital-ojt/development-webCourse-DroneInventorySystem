## 👥 開発

本プロジェクトは【Java Webアプリケーション開発コース】の学習教材として開発されています。

# ドローン在庫管理システム (DroneInventorySystem)

製造業クライアント向けのドローン部品在庫管理Webアプリケーションです。Spring Bootを使用したMVCアーキテクチャで構築されています。

## 🛠 技術スタック

### バックエンド
- **Java 17**
- **Spring Boot 3.2.9**
- **Spring Security** (認証・認可)
- **Spring Data JPA** (データアクセス)
- **Spring Validation** (バリデーション)
- **Lombok** (コード簡略化)
- **Maven** (ビルドツール)

### フロントエンド
- **Thymeleaf** (テンプレートエンジン)
- **Bootstrap** (UIフレームワーク)
- **HTML/CSS/JavaScript**

### データベース
- **MySQL**

### ログ
- **Logback**
- **SLF4J**

## 📁 プロジェクト構成

```
DroneInventorySystem/
├── src/main/java/com/digitalojt/web/
│   ├── config/          # 設定クラス
│   ├── controller/      # コントローラークラス
│   ├── entity/          # エンティティクラス
│   ├── repository/      # リポジトリインターフェース
│   ├── service/         # サービスクラス
│   ├── form/           # フォームクラス
│   ├── validation/     # バリデーションクラス
│   ├── exception/      # 例外クラス
│   └── util/           # ユーティリティクラス
├── src/main/resources/
│   ├── templates/      # Thymeleafテンプレート
│   ├── static/         # 静的リソース (CSS, JS, 画像)
│   ├── application.properties  # アプリケーション設定
│   └── messages.properties     # メッセージ定義
└── docs/               # 設計書・ドキュメント
```

## 🔧 環境設定

### 必要な環境
- Java 17
- Maven 3.6+
- MySQL 8.0+ 

### データベース設定

1. MySQLにデータベースを作成:
```sql
CREATE DATABASE stock_mng;
```

2. `application.properties`でデータベース接続を設定:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/stock_mng
spring.datasource.username=youruserName
spring.datasource.password=yourpassword
```

## 🚀 起動方法

### 1. プロジェクトのクローン
```bash
git clone [repository-url]
cd development-webCourse-DroneInventorySystem
```

### 2. アプリケーションの起動
```bash
cd DroneInventorySystem
mvn spring-boot:run
```

### 3. アクセス
ブラウザで `http://localhost:8080` にアクセス

## 📚 ドキュメント

- 詳細設計書: `docs/設計書/`
- データベース設計: `docs/stock_mng.sql`
- 試験項目書: `docs/試験項目書兼結果報告書/`
