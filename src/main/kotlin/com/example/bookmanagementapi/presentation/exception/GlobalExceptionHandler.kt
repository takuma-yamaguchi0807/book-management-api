package com.example.bookmanagementapi.presentation.exception

import com.example.bookmanagementapi.domain.exception.DomainValidationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * グローバル例外ハンドラー
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    /**
     * ドメインルールのバリデーションエラーを処理
     */
    @ExceptionHandler(DomainValidationException::class)
    fun handleDomainValidationException(e: DomainValidationException): ResponseEntity<ValidationErrorResponse> {
        val response = ValidationErrorResponse(
            code = "VALIDATION_ERROR",
            details = e.errors
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }
}

/**
 * バリデーションエラーレスポンス
 */
data class ValidationErrorResponse(
    val code: String,
    val details: Map<String, String>
)

