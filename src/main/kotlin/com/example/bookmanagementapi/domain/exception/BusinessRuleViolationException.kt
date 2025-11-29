package com.example.bookmanagementapi.domain.exception

/**
 * ビジネスルール違反の例外
 * ValidationErrorResponse形式（details）で返す
 */
class BusinessRuleViolationException(
    val errors: Map<String, String>
) : RuntimeException("Business rule violation: $errors")

