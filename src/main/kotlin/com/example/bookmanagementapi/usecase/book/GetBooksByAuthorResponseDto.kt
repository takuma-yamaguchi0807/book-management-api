package com.example.bookmanagementapi.usecase.book

import com.example.bookmanagementapi.domain.author.AuthorId
import com.example.bookmanagementapi.domain.author.AuthorName
import com.example.bookmanagementapi.domain.book.BookId
import com.example.bookmanagementapi.domain.book.Price
import com.example.bookmanagementapi.domain.book.PublishedStatus
import com.example.bookmanagementapi.domain.book.Title
import java.time.LocalDateTime

/**
 * 著者に紐づく書籍一覧取得レスポンスDTO
 */
data class GetBooksByAuthorResponseDto(
    val items: List<BookItemDto>,
    val pagination: PaginationDto
)

/**
 * 書籍アイテムDTO
 */
data class BookItemDto(
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

/**
 * ページネーションDTO
 */
data class PaginationDto(
    val pageNumber: Int,
    val pageSize: Int,
    val total: Long,
    val totalPages: Int
)

