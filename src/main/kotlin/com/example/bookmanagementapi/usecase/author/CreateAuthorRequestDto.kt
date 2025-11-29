package com.example.bookmanagementapi.usecase.author

/**
 * 著者登録リクエストDTO
 */
data class CreateAuthorRequestDto(
    val name: String?,
    val birthDate: String?
)

