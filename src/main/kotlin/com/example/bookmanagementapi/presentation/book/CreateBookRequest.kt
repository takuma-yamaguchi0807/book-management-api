package com.example.bookmanagementapi.presentation.book

import com.example.bookmanagementapi.presentation.book.BookFields
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 書籍登録リクエスト
 */
data class CreateBookRequest(
    val title: String?,
    val price: Int?,
    @JsonProperty(BookFields.AUTHOR_IDS)
    val authorIds: List<Long>?,
    val published: Boolean?
)

