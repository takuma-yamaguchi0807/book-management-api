package com.example.bookmanagementapi.presentation.book

import com.example.bookmanagementapi.presentation.book.BookFields
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 書籍更新リクエスト
 */
data class UpdateBookRequest(
    val title: String?,
    val price: Int?,
    @JsonProperty(BookFields.AUTHOR_IDS)
    val authorIds: List<Long>?,
    val published: Boolean?
)

