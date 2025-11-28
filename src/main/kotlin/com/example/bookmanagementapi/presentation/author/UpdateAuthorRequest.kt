package com.example.bookmanagementapi.presentation.author

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 著者更新リクエスト
 */
data class UpdateAuthorRequest(
    val name: String?,
    @JsonProperty("birth_date")
    val birthDate: String?
)

