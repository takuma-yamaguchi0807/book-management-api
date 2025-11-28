package com.example.bookmanagementapi.presentation.book

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 書籍更新リクエスト
 */
data class UpdateBookRequest(
    val title: String?,
    val price: Int?,
    @JsonProperty("author_ids")
    val authorIds: List<Long>?,
    val published: Boolean?
)

