package com.example.bookmanagementapi.infra.repository

import com.example.bookmanagementapi.domain.author.AuthorId
import com.example.bookmanagementapi.domain.book.BookAuthorRepository
import com.example.bookmanagementapi.domain.book.BookId
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

/**
 * 書籍-著者関連リポジトリ実装
 */
@Repository
class BookAuthorRepositoryImpl(
    private val dsl: DSLContext
) : BookAuthorRepository {
    
    companion object {
        private val BOOKS_AUTHORS_TABLE = DSL.table("books_authors")
        private val BOOK_ID_FK = DSL.field("book_id", Long::class.java)
        private val AUTHOR_ID_FK = DSL.field("author_id", Long::class.java)
    }
    
    override fun save(bookId: BookId, authorIds: List<AuthorId>) {
        if (authorIds.isEmpty()) {
            return
        }
        
        var insertQuery = dsl.insertInto(BOOKS_AUTHORS_TABLE, BOOK_ID_FK, AUTHOR_ID_FK)
            .values(bookId.value, authorIds.first().value)
        
        authorIds.drop(1).forEach { authorId ->
            insertQuery = insertQuery.values(bookId.value, authorId.value)
        }
        
        insertQuery.execute()
    }

    override fun findByBookId(bookId: BookId): List<AuthorId> {
        val records = dsl.select(AUTHOR_ID_FK)
            .from(BOOKS_AUTHORS_TABLE)
            .where(BOOK_ID_FK.eq(bookId.value))
            .fetch()
        
        return records.map { 
            AuthorId.reconstruct(it.getValue(AUTHOR_ID_FK))
        }
    }

    override fun deleteByBookId(bookId: BookId) {
        dsl.deleteFrom(BOOKS_AUTHORS_TABLE)
            .where(BOOK_ID_FK.eq(bookId.value))
            .execute()
    }
}

