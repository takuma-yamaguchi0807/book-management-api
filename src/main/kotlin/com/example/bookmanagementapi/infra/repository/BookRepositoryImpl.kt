package com.example.bookmanagementapi.infra.repository

import com.example.bookmanagementapi.domain.book.Book
import com.example.bookmanagementapi.domain.book.BookAuthorRepository
import com.example.bookmanagementapi.domain.book.BookId
import com.example.bookmanagementapi.domain.book.BookRepository
import com.example.bookmanagementapi.infra.entity.BookEntity
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

/**
 * 書籍リポジトリ実装
 */
@Repository
class BookRepositoryImpl(
    private val dsl: DSLContext,
    private val bookAuthorRepository: BookAuthorRepository
) : BookRepository {
    
    companion object {
        private val BOOKS_TABLE = DSL.table("books")
        private val BOOK_ID = DSL.field("id", Long::class.java)
        private val TITLE = DSL.field("title", String::class.java)
        private val PRICE = DSL.field("price", Int::class.java)
        private val PUBLISHED = DSL.field("published", Boolean::class.java)
        private val CREATED_AT = DSL.field("created_at", java.sql.Timestamp::class.java)
        private val UPDATED_AT = DSL.field("updated_at", java.sql.Timestamp::class.java)
    }
    
    override fun save(book: Book): BookId {
        val now = LocalDateTime.now()
        
        // booksテーブルにINSERT
        val insertedRecord = dsl.insertInto(BOOKS_TABLE)
            .set(TITLE, book.title.value)
            .set(PRICE, book.price.value)
            .set(PUBLISHED, book.publishedStatus.value)
            .set(CREATED_AT, java.sql.Timestamp.valueOf(now))
            .set(UPDATED_AT, java.sql.Timestamp.valueOf(now))
            .returningResult(BOOK_ID)
            .fetchOne()
        
        val savedId = insertedRecord?.getValue(BOOK_ID)
            ?: throw IllegalStateException("Failed to get generated ID")
        
        return BookId.reconstruct(savedId)
    }

    override fun findById(id: BookId): Book? {
        // booksテーブルから取得
        val record = dsl.select(BOOK_ID, TITLE, PRICE, PUBLISHED, CREATED_AT, UPDATED_AT)
            .from(BOOKS_TABLE)
            .where(BOOK_ID.eq(id.value))
            .fetchOne()

        if (record == null) {
            return null
        }
        
        val entity = BookEntity(
            id = record.getValue(BOOK_ID),
            title = record.getValue(TITLE),
            price = record.getValue(PRICE),
            published = record.getValue(PUBLISHED),
            createdAt = record.getValue(CREATED_AT).toLocalDateTime(),
            updatedAt = record.getValue(UPDATED_AT).toLocalDateTime()
        )
        
        // books_authorsテーブルから著者IDを取得（BookAuthorRepositoryを使用）
        val authorIds = bookAuthorRepository.findByBookId(id)
        
        return entity.toDomain(authorIds)
    }

    override fun update(book: Book) {
        val now = LocalDateTime.now()
        val bookId = book.id ?: throw IllegalStateException("Book.id must not be null for update")

        // booksテーブルをUPDATE
        dsl.update(BOOKS_TABLE)
            .set(TITLE, book.title.value)
            .set(PRICE, book.price.value)
            .set(PUBLISHED, book.publishedStatus.value)
            .set(UPDATED_AT, java.sql.Timestamp.valueOf(now))
            .where(BOOK_ID.eq(bookId.value))
            .execute()
    }
}

