package com.example.bookmanagementapi.presentation.exception

import com.example.bookmanagementapi.domain.exception.BusinessRuleViolationException
import com.example.bookmanagementapi.domain.exception.DomainValidationException
import com.example.bookmanagementapi.domain.exception.ResourceNotFoundException
import com.example.bookmanagementapi.domain.message.ErrorMessages
import com.example.bookmanagementapi.domain.message.ValidationMessages
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
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

    /**
     * リソースが存在しない場合のエラーを処理
     */
    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(e: ResourceNotFoundException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            code = "RESOURCE_NOT_FOUND",
            message = e.message ?: ErrorMessages.RESOURCE_NOT_FOUND
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
    }

    /**
     * ビジネスルール違反のエラーを処理
     * errorsが指定された場合はValidationErrorResponse形式（details）で返す
     * errorsが指定されない場合はErrorResponse形式（message）で返す
     */
    @ExceptionHandler(BusinessRuleViolationException::class)
    fun handleBusinessRuleViolationException(e: BusinessRuleViolationException): ResponseEntity<ValidationErrorResponse> {
        val response = ValidationErrorResponse(
            code = "BUSINESS_RULE_VIOLATION",
            details = e.errors
        )
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response)
    }

    /**
     * 必須リクエストパラメータが欠如している場合のエラーを処理
     * 例: GET /api/v1/books で author_id が指定されていない場合
     */
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(e: MissingServletRequestParameterException): ResponseEntity<ValidationErrorResponse> {
        val response = ValidationErrorResponse(
            code = "VALIDATION_ERROR",
            details = mapOf(e.parameterName to ValidationMessages.REQUIRED)
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    /**
     * パス変数の型変換エラーを処理
     * 例: /authors/abc のように数値以外の文字列が渡された場合
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(e: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            code = "INVALID_REQUEST",
            message = ErrorMessages.invalidPathParameter(e.name ?: "unknown")
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

/**
 * エラーレスポンス
 */
data class ErrorResponse(
    val code: String,
    val message: String
)

