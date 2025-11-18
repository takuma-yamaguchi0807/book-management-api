package com.example.bookmanagementapi.domain.exception

/**
 * ドメインルールのバリデーションエラー
 * フィールド名をキー、エラーメッセージを値とするマップを保持
 */
class DomainValidationException(
    val errors: Map<String, String>
) : RuntimeException("Validation failed: $errors")

