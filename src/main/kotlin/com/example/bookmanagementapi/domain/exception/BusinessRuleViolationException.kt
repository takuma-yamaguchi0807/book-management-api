package com.example.bookmanagementapi.domain.exception

/**
 * ビジネスルール違反の例外
 */
class BusinessRuleViolationException(
    message: String
) : RuntimeException(message)

