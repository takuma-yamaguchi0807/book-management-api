package com.example.bookmanagementapi.presentation.book

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 書籍登録リクエスト
 */
data class CreateBookRequest(
    val title: String?,
    val price: Int?,
    @JsonProperty("author_ids")
    val authorIds: List<Long>?,
    val published: Boolean?
)

