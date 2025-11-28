package com.example.bookmanagementapi.presentation.author

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 著者登録リクエスト
 */
data class CreateAuthorRequest(
    val name: String?,
    @JsonProperty("birth_date")
    val birthDate: String?
)

