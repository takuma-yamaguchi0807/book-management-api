package com.example.bookmanagementapi.presentation.book

import java.time.LocalDateTime

/**
 * 書籍一覧レスポンス
 */
data class BookListResponse(
    val items: List<BookItemResponse>,
    val pagination: PaginationResponse
)

/**
 * 書籍アイテムレスポンス
 */
data class BookItemResponse(
    val id: Long,
    val title: String,
    val price: Int,
    val published: Boolean,
    val authors: List<AuthorSummaryResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

/**
 * 著者サマリーレスポンス
 */
data class AuthorSummaryResponse(
    val id: Long,
    val name: String
)

/**
 * ページネーションレスポンス
 */
data class PaginationResponse(
    val pageNumber: Int,
    val pageSize: Int,
    val total: Long,
    val totalPages: Int
)

