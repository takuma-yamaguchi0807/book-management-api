package com.example.bookmanagementapi.domain.pagination

import com.example.bookmanagementapi.domain.ValidationResult
import com.example.bookmanagementapi.domain.exception.ValidationError
import com.example.bookmanagementapi.domain.message.ValidationMessages
import com.example.bookmanagementapi.presentation.shared.CommonFields

/**
 * ページ番号の値オブジェクト
 */
data class PageNumber(val value: Int) {
    companion object {
        /**
         * リクエストから作成する際に使用（バリデーションあり）
         */
        fun create(value: Int?): ValidationResult<PageNumber> {
            if (value == null) {
                return ValidationResult.Failure(ValidationError(CommonFields.PAGE_NUMBER, ValidationMessages.REQUIRED))
            }
            if (value < 1) {
                return ValidationResult.Failure(ValidationError(CommonFields.PAGE_NUMBER, ValidationMessages.MUST_BE_POSITIVE))
            }
            return ValidationResult.Success(PageNumber(value))
        }

        /**
         * 再構築用
         */
        fun reconstruct(value: Int): PageNumber {
            return PageNumber(value)
        }
    }
}

