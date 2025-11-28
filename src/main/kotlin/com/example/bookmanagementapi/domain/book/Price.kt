package com.example.bookmanagementapi.domain.book

import com.example.bookmanagementapi.domain.ValidationResult
import com.example.bookmanagementapi.domain.exception.ValidationError
import com.example.bookmanagementapi.domain.message.ValidationMessages
import com.example.bookmanagementapi.presentation.book.BookFields

/**
 * 書籍価格の値オブジェクト
 */
data class Price private constructor(val value: Int) {
    companion object {
        /**
         * リクエストから作成する際に使用（バリデーションあり）
         */
        fun create(value: Int?): ValidationResult<Price> {
            if (value == null) {
                return ValidationResult.Failure(ValidationError(BookFields.PRICE, ValidationMessages.REQUIRED))
            }
            if (value < 0) {
                return ValidationResult.Failure(ValidationError(BookFields.PRICE, ValidationMessages.PRICE_MUST_BE_NON_NEGATIVE))
            }
            return ValidationResult.Success(Price(value))
        }

        /**
         * 再構築用
         *
         * 永続化された値を元にインスタンスを生成する。
         * 値は必須であり、nullの場合は例外をスローする。
         * 不変条件は保証しないので、利用側で必要に応じてチェックすること。
         */
        fun reconstruct(value: Int?): Price {
            requireNotNull(value) { "Price must not be null" }
            return Price(value)
        }
    }
}

