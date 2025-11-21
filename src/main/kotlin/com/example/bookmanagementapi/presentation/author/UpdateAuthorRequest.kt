package com.example.bookmanagementapi.presentation.author

/**
 * 著者更新リクエスト
 */
data class UpdateAuthorRequest(
    val name: String?,
    val birthDate: String?
)

