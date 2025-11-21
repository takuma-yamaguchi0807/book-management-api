package com.example.bookmanagementapi.usecase.book

/**
 * 書籍更新リクエストDTO
 */
data class UpdateBookRequestDto(
    val title: String?,
    val price: Int?,
    val authorIds: List<Long>?,
    val published: Boolean?
)

