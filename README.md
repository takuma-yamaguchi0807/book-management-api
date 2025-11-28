# Book Management API

書籍と著者を管理する REST API です。

## 技術スタック

- **言語**: Kotlin
- **フレームワーク**: Spring Boot 3.5.7
- **データベース**: PostgreSQL
- **ORM**: jOOQ
- **マイグレーション**: Flyway
- **ビルドツール**: Gradle

## プロジェクト構成

### パッケージ構成

```
com.example.bookmanagementapi
├── BookManagementApiApplication.kt
├── presentation              # プレゼンテーション層
│   ├── author
│   │   ├── AuthorController.kt
│   │   ├── CreateAuthorRequest.kt
│   │   ├── UpdateAuthorRequest.kt
│   │   └── AuthorResponse.kt
│   └── book
│       ├── BookController.kt        # 著者に紐づく書籍一覧取得も含む
│       ├── CreateBookRequest.kt
│       ├── UpdateBookRequest.kt
│       └── BookResponse.kt
├── usecase                   # ユースケース層
│   ├── author
│   │   ├── CreateAuthorUsecase.kt
│   │   ├── CreateAuthorRequestDto.kt
│   │   ├── CreateAuthorResponseDto.kt
│   │   ├── UpdateAuthorUsecase.kt
│   │   ├── UpdateAuthorRequestDto.kt
│   │   └── UpdateAuthorResponseDto.kt
│   └── book
│       ├── CreateBookUsecase.kt
│       ├── CreateBookRequestDto.kt
│       ├── CreateBookResponseDto.kt
│       ├── UpdateBookUsecase.kt
│       ├── UpdateBookRequestDto.kt
│       ├── UpdateBookResponseDto.kt
│       ├── GetBooksByAuthorUsecase.kt
│       ├── GetBooksByAuthorRequestDto.kt
│       └── GetBooksByAuthorResponseDto.kt
├── domain                    # ドメイン層
│   ├── author
│   │   └── Author.kt
│   ├── book
│   │   └── Book.kt
│   └── queryservice
│       └── BookQueryService.kt      # インターフェース
└── infra                     # インフラストラクチャ層
    ├── entity
    │   ├── AuthorEntity.kt
    │   └── BookEntity.kt
    ├── repository
    │   ├── AuthorRepository.kt
    │   └── BookRepository.kt
    └── queryservice
        └── BookQueryServiceImpl.kt
```

### レイヤー構成

- **Presentation 層**: HTTP リクエスト/レスポンスの処理、バリデーション
- **Usecase 層**: ユースケースの実装、ビジネスロジックの調整
- **Domain 層**: ドメインモデル、ドメインロジック、クエリサービス（インターフェース）
- **Infra 層**: データベースアクセス（jOOQ を使用）、エンティティ、リポジトリ・クエリサービスの実装

依存関係の方向: Presentation → Usecase → Domain ← Infra

### CQRS パターン

- **Command 側**: `infra/repository` - データの作成・更新・削除（CRUD 操作）
- **Query 側**:
  - `infra/repository` - 単一エンティティの読み取り（単純なクエリ）
  - `domain/queryservice`（インターフェース） + `infra/queryservice`（実装） - 複数テーブルを跨いだ集約的な読み取り操作（複雑なクエリ）

## 起動方法

### 前提条件

- Java 17 以上
- Docker および Docker Compose（開発環境のデータベース起動用）

### 1. データベースの起動

Docker Compose を使用して PostgreSQL を起動します。

```bash
docker compose up -d
```

データベース接続情報:

- ホスト: `localhost`
- ポート: `5432`（Docker Compose で自動割り当て）
- データベース名: `mydatabase`
- ユーザー名: `myuser`
- パスワード: `secret`

### 2. アプリケーションの起動

#### Gradle を使用する場合

```bash
./gradlew bootRun
```

Windows の場合:

```bash
gradlew.bat bootRun
```

#### IDE から起動する場合

`BookManagementApiApplication.kt` の `main` 関数を実行します。

### 3. アプリケーションの確認

アプリケーションはデフォルトで `http://localhost:8080` で起動します。

API 仕様は `docs/openapi.yml` を参照してください。

## ビルド

```bash
./gradlew build
```

## テスト

```bash
./gradlew test
```

## ドキュメント

プロジェクトの設計ドキュメントは `docs/` 配下にあります。

- `functional-requirements.md`: 機能要件
- `openapi.yml`: API 仕様
- `architect.md`: アーキテクチャ設計（設計判断と意図）
- `db.md`: データベース設計
- `er.md`: ER 図

## 開発ガイドライン

開発時の方針については `AGENTS.md` を参照してください。
