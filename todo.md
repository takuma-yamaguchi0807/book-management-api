# Book Management API 実装タスク

## 実装状況

- ✅ POST /authors - 著者登録（実装済み）
- ✅ PUT /authors/{id} - 著者更新（実装済み）
- ✅ POST /books - 書籍登録（実装済み）
- ✅ PUT /books/{id} - 書籍更新（実装済み）
- ✅ GET /authors/{id}/books - 著者に紐づく書籍一覧取得

---

## タスク一覧

### 1. 著者更新機能の実装

- [x] AuthorRepository に findById と update メソッドを追加
- [x] AuthorRepositoryImpl に findById と update を実装（jOOQ で SELECT/UPDATE）
- [x] UpdateAuthorUsecase と DTO を作成
- [x] AuthorController に PUT /authors/{id}エンドポイントを追加
- [x] 例外ハンドリング（ResourceNotFoundException）を追加

### 2. 書籍関連ドメインモデルの実装

- [x] 書籍関連の値オブジェクトを作成（BookId, Title, Price, PublishedStatus）
- [x] Book エンティティと BookRepository インターフェースを作成
- [x] BookFields オブジェクトを作成

### 3. 書籍登録機能の実装

- [x] Flyway マイグレーション（books, books_authors テーブル）を作成
- [x] BookEntity と BookRepositoryImpl を作成
- [x] CreateBookUsecase を作成（著者 ID 存在チェック含む、N+1 問題対応済み）
- [x] BookController に POST /books エンドポイントを追加
- [x] 例外ハンドリング（RESOURCE_NOT_FOUND）を追加

### 4. 書籍更新機能の実装

- [x] UpdateBookUsecase を作成（出版済み → 未出版チェック含む、N+1 問題対応済み）
- [x] BookController に PUT /books/{id}エンドポイントを追加
- [x] 例外ハンドリング（BOOK_NOT_FOUND, BUSINESS_RULE_VIOLATION）を追加

### 5. 書籍一覧取得機能の実装

- [x] BookQueryService インターフェースを作成
- [x] BookQueryServiceImpl を実装（JOIN クエリ、ページネーション）
- [x] GetBooksByAuthorUsecase を作成
- [x] BookController に GET /authors/{id}/books エンドポイントを追加

### 6. 例外ハンドリングの拡張

- [x] 例外クラスを作成（ResourceNotFoundException, BusinessRuleViolationException）
- [x] GlobalExceptionHandler に BusinessRuleViolationException のハンドリングを追加
- [x] GlobalExceptionHandler のメッセージを messages.properties に定義（ErrorMessages を使用）
- [x] 書籍関連のバリデーションメッセージを追加

### 7. 単体テスト（UT）の追加と動作確認

- [x] ドメイン層の単体テストを追加（値オブジェクト、エンティティのバリデーション）
- [x] Usecase 層の単体テストを追加（ビジネスロジックのテスト）
- [x] Repository 層の単体テストを追加（データアクセスのテスト）
- [x] Controller 層の単体テストを追加（HTTP リクエスト/レスポンスのテスト）
- [ ] 全体的に UT の見直し。
- [ ] Postman 等での動作確認（各エンドポイントの手動テスト）

---

## 実装順序

1. 著者更新機能の実装
2. 書籍関連ドメインモデルの実装
3. 書籍登録機能の実装
4. 書籍更新機能の実装
5. 書籍一覧取得機能の実装
6. 例外ハンドリングの拡張
7. 単体テスト（UT）の追加と動作確認
