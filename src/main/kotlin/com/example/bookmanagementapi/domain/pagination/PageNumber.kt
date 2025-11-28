package com.example.bookmanagementapi.domain.pagination

import com.example.bookmanagementapi.domain.ValidationResult
import com.example.bookmanagementapi.domain.exception.ValidationError
import com.example.bookmanagementapi.domain.message.ValidationMessages

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
                return ValidationResult.Failure(ValidationError("page_number", ValidationMessages.REQUIRED))
            }
            if (value < 1) {
                return ValidationResult.Failure(ValidationError("page_number", ValidationMessages.PAGE_NUMBER_MUST_BE_AT_LEAST_ONE))
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

