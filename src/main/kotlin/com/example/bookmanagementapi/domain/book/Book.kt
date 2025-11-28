package com.example.bookmanagementapi.domain.book

import com.example.bookmanagementapi.domain.author.AuthorId

/**
 * 書籍エンティティ
 */
data class Book private constructor(
    val id: BookId?,
    val title: Title,
    val price: Price,
    val publishedStatus: PublishedStatus,
    val authorIds: List<AuthorId>
) {
    companion object {
        /**
         * 新規作成用（値オブジェクトから作成）
         */
        fun create(
            title: Title,
            price: Price,
            publishedStatus: PublishedStatus,
            authorIds: List<AuthorId>
        ): Book {
            return Book(
                id = null,
                title = title,
                price = price,
                publishedStatus = publishedStatus,
                authorIds = authorIds
            )
        }

        /**
         * 更新用（値オブジェクトから作成）
         */
        fun update(
            id: BookId,
            title: Title,
            price: Price,
            publishedStatus: PublishedStatus,
            authorIds: List<AuthorId>
        ): Book {
            return Book(
                id = id,
                title = title,
                price = price,
                publishedStatus = publishedStatus,
                authorIds = authorIds
            )
        }

        /**
         * DBから再構築用
         */
        fun reconstruct(
            id: BookId,
            title: Title,
            price: Price,
            publishedStatus: PublishedStatus,
            authorIds: List<AuthorId>
        ): Book {
            return Book(
                id = id,
                title = title,
                price = price,
                publishedStatus = publishedStatus,
                authorIds = authorIds
            )
        }
    }
}

