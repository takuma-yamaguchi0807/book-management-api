# テーブル定義

本プロジェクトでは、書籍と著者を管理するために、以下の 3 つのテーブルを定義する。

- `authors`：著者情報を管理するテーブル
- `books`：書籍情報を管理するテーブル
- `books_authors`：書籍と著者の多対多関係を表現する中間テーブル

対象の RDB は PostgreSQL を想定する。

---

## テーブル定義

### 1. authors

著者情報を管理するテーブル。

```sql
CREATE TABLE authors (
    id         BIGSERIAL PRIMARY KEY,
    name       TEXT NOT NULL,
    birth_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

---

### 2. books

書籍情報を管理するテーブル。

```sql
CREATE TABLE books (
    id         BIGSERIAL PRIMARY KEY,
    title      TEXT NOT NULL,
    price      INTEGER NOT NULL,
    published  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

---

### 3. books_authors（中間テーブル）

書籍と著者の多対多関係を管理する中間テーブル。

```sql
CREATE TABLE books_authors (
    book_id   BIGINT NOT NULL REFERENCES books(id),
    author_id BIGINT NOT NULL REFERENCES authors(id),
    PRIMARY KEY (book_id, author_id)
);
```

---

## 設計意図

## authors テーブル

- 著者は複数の書籍を執筆できるため、`authors` は独立したテーブルとして定義する。
- `name` は要件上文字数制限がなく、PostgreSQL では `VARCHAR(n)` と `TEXT` に実質的な性能差がないため、柔軟性とシンプルさを優先して `TEXT` を採用している。
- `birth_date` は誕生日を扱うため、タイムゾーンを必要としない `DATE` 型を使用している。
- `created_at` / `updated_at` は、今回のコーディングテストの範囲ではタイムゾーン差異を意識する必要性が低いため、  
   `TIMESTAMP`（タイムゾーンなし）を採用した。  
  単一環境でのローカル検証が中心であり、DB 設計を必要以上に複雑化しないための判断。

---

## books テーブル

- 書籍は複数の著者と紐づく可能性があるため、`books` は独立したテーブルとして定義している。
- `title` は文字数制限が要件として求められていないため、`TEXT` を採用している。
- `price` は今回「小数点以下が存在しない金額（1 円単位）」として扱うため、`INTEGER` を採用した。  
  通貨を小数で扱うケースでは `NUMERIC(10,2)` などを利用するが、本スコープでは小数点表現が不要と判断し、  
  オーバーエンジニアリングを避ける目的で整数表現に統一している。
- 出版状態は要件上「未出版 / 出版済み」の 2 状態のみであり、それ以上の拡張（絶版、予約、校正中など）は求められていない。  
  ステータス管理の拡張型（ENUM や TEXT 型）を設けると、今回のスコープに対し過剰となるため、  
  状態はシンプルな `published BOOLEAN` を採用している。  
  遷移ルール（出版済み → 未出版への変更禁止）はアプリケーション側のドメインロジックで扱う方針とした。
- `created_at` / `updated_at` についても authors 同様、スコープの観点から `TIMESTAMP` を採用している。

---

## books_authors テーブル（中間テーブル）

- 書籍と著者は多対多の関係を持つため、その関係を正規化して表現する中間テーブルを定義。
- テーブル名は `books_authors` とし、両者のテーブル名の複数形を結合することで、「中間テーブル」であることが直感的に分かるように命名している。
- `(book_id, author_id)` の複合主キーとすることで、同一の組み合わせでの重複登録を防ぎ、  
  データ整合性を簡潔に保っている。
- 外部キー制約により `books` / `authors` 両テーブルとの整合性を強制している。

---

## 制約（CHECK）をデータベースに持たせない方針

- `price >= 0` や `birth_date < CURRENT_DATE` などの不変条件は、  
  DB の `CHECK` 制約で表現することも可能だが、今回は **アプリケーション側のバリデーションが十分** と判断した。
- CHECK 制約を過度に導入すると、仕様変更時のスキーマ修正・再デプロイ・データ移行などのコストが増えやすい。  
  コーディングテストという限定的スコープでは、複雑性より明確さ・変更容易性を優先した。
- 特に「出版済み状態から未出版に戻せない」といったビジネスロジック寄りの制約は  
  **ドメイン層 / サービス層で明示的に扱う方が意図を説明しやすい**。
- 以上から、本設計では DB 側のチェック制約は定義せず、  
  **アプリケーション層での検証に一本化し、柔軟で理解しやすい構造**とする方針を取っている。

---
