package com.example.bookmanagementapi.infra.queryservice

import com.example.bookmanagementapi.domain.author.AuthorId
import com.example.bookmanagementapi.domain.pagination.Pagination
import com.example.bookmanagementapi.domain.queryservice.AuthorSummaryDto
import com.example.bookmanagementapi.domain.queryservice.BookQueryRepository
import com.example.bookmanagementapi.domain.queryservice.BookQueryResultDto
import com.example.bookmanagementapi.domain.queryservice.BookWithAuthorsDto
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

/**
 * 書籍クエリリポジトリ実装
 * QueryService用の読み取り専用リポジトリ
 */
@Repository
class BookQueryRepositoryImpl(
    private val dsl: DSLContext
) : BookQueryRepository {
    
    companion object {
        private val BOOKS_TABLE = DSL.table("books")
        private val BOOKS_AUTHORS_TABLE = DSL.table("books_authors")
        private val AUTHORS_TABLE = DSL.table("authors")
        
        // booksテーブルのフィールド
        private val BOOK_ID = DSL.field("books.id", Long::class.java)
        private val TITLE = DSL.field("books.title", String::class.java)
        private val PRICE = DSL.field("books.price", Int::class.java)
        private val PUBLISHED = DSL.field("books.published", Boolean::class.java)
        private val BOOK_CREATED_AT = DSL.field("books.created_at", java.sql.Timestamp::class.java)
        private val BOOK_UPDATED_AT = DSL.field("books.updated_at", java.sql.Timestamp::class.java)
        
        // books_authorsテーブルのフィールド
        private val AUTHOR_ID_FK = DSL.field("books_authors.author_id", Long::class.java)
        private val BOOK_ID_FK = DSL.field("books_authors.book_id", Long::class.java)
        
        // authorsテーブルのフィールド
        private val AUTHOR_ID = DSL.field("authors.id", Long::class.java)
        private val AUTHOR_NAME = DSL.field("authors.name", String::class.java)
    }
    
    override fun countByAuthorId(authorId: AuthorId): Long {
        return dsl.selectCount()
            .from(BOOKS_TABLE)
            .innerJoin(BOOKS_AUTHORS_TABLE).on(BOOK_ID.eq(BOOK_ID_FK))
            .where(AUTHOR_ID_FK.eq(authorId.value))
            .fetchOne(0, Long::class.java) ?: 0L
    }

    override fun findByAuthorId(
        authorId: AuthorId,
        pagination: Pagination
    ): BookQueryResultDto {
        // ページネーション適用で書籍を取得
        val offset = pagination.calculateOffset()
        
        // 指定された著者IDに紐づく書籍を取得（重複を避けるためDISTINCT）
        val bookRecords = dsl.selectDistinct(BOOK_ID, TITLE, PRICE, PUBLISHED, BOOK_CREATED_AT, BOOK_UPDATED_AT)
            .from(BOOKS_TABLE)
            .innerJoin(BOOKS_AUTHORS_TABLE).on(BOOK_ID.eq(BOOK_ID_FK))
            .where(AUTHOR_ID_FK.eq(authorId.value))
            .orderBy(BOOK_ID)
            .limit(pagination.pageSize.value)
            .offset(offset)
            .fetch()
        
        if (bookRecords.isEmpty()) {
            return BookQueryResultDto(books = emptyList())
        }
        
        // 書籍IDリストを取得
        val bookIds = bookRecords.map { it.getValue(BOOK_ID) }
        
        // 各書籍に紐づく全ての著者情報をJOINで取得
        // 1つの書籍に複数の著者がいる場合、複数行になる
        val bookAuthorRecords = dsl.select(
            BOOK_ID_FK,
            AUTHOR_ID,
            AUTHOR_NAME
        )
            .from(BOOKS_AUTHORS_TABLE)
            .innerJoin(AUTHORS_TABLE).on(AUTHOR_ID_FK.eq(AUTHOR_ID))
            .where(BOOK_ID_FK.`in`(bookIds))
            .orderBy(BOOK_ID_FK, AUTHOR_ID)
            .fetch()
        
        // 書籍IDごとに著者情報をグループ化
        val authorsByBookId = bookAuthorRecords
            .groupBy { it.getValue(BOOK_ID_FK) }
            .mapValues { (_, records) ->
                records.map { record ->
                    AuthorSummaryDto(
                        id = record.getValue(AUTHOR_ID),
                        name = record.getValue(AUTHOR_NAME)
                    )
                }
            }
        
        // DTOに変換
        val booksWithAuthors = bookRecords.map { record ->
            val bookId = record.getValue(BOOK_ID)
            BookWithAuthorsDto(
                id = bookId,
                title = record.getValue(TITLE),
                price = record.getValue(PRICE),
                published = record.getValue(PUBLISHED),
                authors = authorsByBookId[bookId] ?: emptyList(),
                createdAt = record.getValue(BOOK_CREATED_AT).toLocalDateTime(),
                updatedAt = record.getValue(BOOK_UPDATED_AT).toLocalDateTime()
            )
        }
        
        return BookQueryResultDto(books = booksWithAuthors)
    }
}

