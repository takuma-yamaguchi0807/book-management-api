package com.example.bookmanagementapi.domain.exception

import com.example.bookmanagementapi.domain.message.ErrorMessages

/**
 * リソースが存在しない場合の例外
 */
class ResourceNotFoundException(
    message: String = ErrorMessages.RESOURCE_NOT_FOUND
) : RuntimeException(message)

