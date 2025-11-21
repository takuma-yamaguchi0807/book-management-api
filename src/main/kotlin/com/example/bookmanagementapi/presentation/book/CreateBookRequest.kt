package com.example.bookmanagementapi.presentation.book

/**
 * 書籍登録リクエスト
 */
data class CreateBookRequest(
    val title: String?,
    val price: Int?,
    val authorIds: List<Long>?,
    val published: Boolean?
)

