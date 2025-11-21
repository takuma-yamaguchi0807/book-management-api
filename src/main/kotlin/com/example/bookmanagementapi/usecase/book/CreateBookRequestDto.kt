package com.example.bookmanagementapi.usecase.book

/**
 * 書籍登録リクエストDTO
 */
data class CreateBookRequestDto(
    val title: String?,
    val price: Int?,
    val authorIds: List<Long>?,
    val published: Boolean?
)

