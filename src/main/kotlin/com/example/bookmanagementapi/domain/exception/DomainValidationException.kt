package com.example.bookmanagementapi.domain.exception

/**
 * ドメインルールのバリデーションエラー
 * 通常フィールドはString、配列フィールドはMap<String, String>（ネスト構造）として保持
 */
class DomainValidationException(
    val errors: Map<String, Any>
) : RuntimeException("Validation failed: $errors")

