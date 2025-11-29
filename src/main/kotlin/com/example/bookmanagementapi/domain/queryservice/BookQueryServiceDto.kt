package com.example.bookmanagementapi.domain.queryservice

import java.time.LocalDateTime

/**
 * 書籍クエリ結果DTO
 * QueryService用の読み取り専用DTO（CQRSのQuery側）
 */
data class BookQueryResultDto(
    val books: List<BookWithAuthorsDto>
)

/**
 * 著者情報付き書籍DTO
 * JOINクエリで取得した書籍と著者の情報を含む
 */
data class BookWithAuthorsDto(
    val id: Long,
    val title: String,
    val price: Int,
    val published: Boolean,
    val authors: List<AuthorSummaryDto>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

/**
 * 著者サマリーDTO
 */
data class AuthorSummaryDto(
    val id: Long,
    val name: String
)

