package com.example.bookmanagementapi.usecase.author

/**
 * 著者更新リクエストDTO
 */
data class UpdateAuthorRequestDto(
    val name: String?,
    val birthDate: String?
)

