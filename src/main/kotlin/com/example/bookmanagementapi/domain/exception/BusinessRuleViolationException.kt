package com.example.bookmanagementapi.domain.exception

/**
 * ビジネスルール違反の例外
 * ValidationErrorResponse形式（details）で返す
 * 通常フィールドはString、配列フィールドはMap<String, String>（ネスト構造）として保持
 */
class BusinessRuleViolationException(
    val errors: Map<String, Any>
) : RuntimeException("Business rule violation: $errors")

