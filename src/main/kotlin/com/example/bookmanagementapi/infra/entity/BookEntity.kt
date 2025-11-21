package com.example.bookmanagementapi.infra.entity

import com.example.bookmanagementapi.domain.author.AuthorId
import com.example.bookmanagementapi.domain.book.Book
import com.example.bookmanagementapi.domain.book.BookId
import com.example.bookmanagementapi.domain.book.Price
import com.example.bookmanagementapi.domain.book.PublishedStatus
import com.example.bookmanagementapi.domain.book.Title
import java.time.LocalDateTime

/**
 * 書籍エンティティ（DB用）
 * booksテーブルに対応
 */
data class BookEntity(
    val id: Long?,
    val title: String,
    val price: Int,
    val published: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    /**
     * ドメインオブジェクトに変換
     * 
     * BookEntityのプロパティ（id, title, price, published）から値オブジェクトを作成し、
     * 引数で受け取ったauthorIdsと組み合わせてBookエンティティを再構築する
     * 
     * @param authorIds 著者IDリスト（books_authorsテーブルから取得）
     */
    fun toDomain(authorIds: List<AuthorId>): Book {
        return Book.reconstruct(
            id = id?.let { BookId.reconstruct(it) }
                ?: throw IllegalStateException("BookEntity.id must not be null"),
            title = Title.reconstruct(title),  // BookEntityのtitleプロパティから取得
            price = Price.reconstruct(price),  // BookEntityのpriceプロパティから取得
            publishedStatus = PublishedStatus.reconstruct(published),  // BookEntityのpublishedプロパティから取得
            authorIds = authorIds  // 引数から取得（別テーブルから取得した値）
        )
    }
}

