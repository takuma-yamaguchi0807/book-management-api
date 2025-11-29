package com.example.bookmanagementapi.domain.pagination

import com.example.bookmanagementapi.domain.ValidationResult
import com.example.bookmanagementapi.domain.exception.ValidationError
import com.example.bookmanagementapi.domain.message.ValidationMessages
import com.example.bookmanagementapi.presentation.shared.CommonFields

/**
 * ページサイズの値オブジェクト
 */
data class PageSize(val value: Int) {
    companion object {
        /**
         * リクエストから作成する際に使用（バリデーションあり）
         */
        fun create(value: Int?): ValidationResult<PageSize> {
            if (value == null) {
                return ValidationResult.Failure(ValidationError(CommonFields.PAGE_SIZE, ValidationMessages.REQUIRED))
            }
            if (value < 1 || value >= 100) {
                return ValidationResult.Failure(ValidationError(CommonFields.PAGE_SIZE, ValidationMessages.MUST_BE_BETWEEN_ONE_AND_NINETY_NINE))
            }
            return ValidationResult.Success(PageSize(value))
        }

        /**
         * 再構築用
         */
        fun reconstruct(value: Int): PageSize {
            return PageSize(value)
        }
    }
}

