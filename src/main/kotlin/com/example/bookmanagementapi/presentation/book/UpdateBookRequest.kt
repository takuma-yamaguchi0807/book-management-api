package com.example.bookmanagementapi.presentation.book

/**
 * 書籍更新リクエスト
 */
data class UpdateBookRequest(
    val title: String?,
    val price: Int?,
    val authorIds: List<Long>?,
    val published: Boolean?
)

