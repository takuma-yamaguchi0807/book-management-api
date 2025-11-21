package com.example.bookmanagementapi.domain

import com.example.bookmanagementapi.domain.exception.ValidationError

/**
 * バリデーション結果
 * 成功時は値オブジェクトを、失敗時はValidationErrorを保持
 */
sealed class ValidationResult<out T> {
    data class Success<T>(val value: T) : ValidationResult<T>()
    data class Failure(val error: ValidationError) : ValidationResult<Nothing>()
}

