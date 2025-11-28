package com.example.bookmanagementapi.presentation.book

import com.example.bookmanagementapi.presentation.shared.CommonFields
import com.fasterxml.jackson.annotation.JsonProperty
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
    @JsonProperty(CommonFields.CREATED_AT)
    val createdAt: LocalDateTime,
    @JsonProperty(CommonFields.UPDATED_AT)
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
    @JsonProperty(CommonFields.PAGE_NUMBER)
    val pageNumber: Int,
    @JsonProperty(CommonFields.PAGE_SIZE)
    val pageSize: Int,
    val total: Long,
    @JsonProperty(CommonFields.TOTAL_PAGES)
    val totalPages: Int
)

