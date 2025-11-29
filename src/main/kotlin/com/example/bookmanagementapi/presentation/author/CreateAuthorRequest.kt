package com.example.bookmanagementapi.presentation.author

import com.example.bookmanagementapi.presentation.author.AuthorFields
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 著者登録リクエスト
 */
data class CreateAuthorRequest(
    val name: String?,
    @JsonProperty(AuthorFields.BIRTH_DATE)
    val birthDate: String?
)

