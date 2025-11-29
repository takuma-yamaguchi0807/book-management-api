package com.example.bookmanagementapi.usecase.book

/**
 * 著者に紐づく書籍一覧取得リクエストDTO
 */
data class GetBooksByAuthorRequestDto(
    val authorId: Long,
    val pageNumber: Int,
    val pageSize: Int
)

