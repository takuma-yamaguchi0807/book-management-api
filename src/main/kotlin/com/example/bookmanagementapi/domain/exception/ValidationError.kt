package com.example.bookmanagementapi.domain.exception

/**
 * バリデーションエラー
 * フィールド名とエラーメッセージを保持
 */
data class ValidationError(
    val field: String,
    val message: String
)

