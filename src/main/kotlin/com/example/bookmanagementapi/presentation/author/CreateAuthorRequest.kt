package com.example.bookmanagementapi.presentation.author

/**
 * 著者登録リクエスト
 */
data class CreateAuthorRequest(
    val name: String?,
    val birthDate: String?
)

