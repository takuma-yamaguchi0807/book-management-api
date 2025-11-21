package com.example.bookmanagementapi.domain.book

import com.example.bookmanagementapi.domain.ValidationResult
import com.example.bookmanagementapi.domain.exception.ValidationError
import com.example.bookmanagementapi.domain.message.ValidationMessages
import com.example.bookmanagementapi.presentation.book.BookFields

/**
 * 書籍IDの値オブジェクト
 */
class BookId private constructor(val value: Long) {
    companion object {
        /**
         * リクエストから作成する際に使用（バリデーションあり）
         */
        fun create(value: Long?): ValidationResult<BookId> {
            if (value == null) {
                return ValidationResult.Failure(ValidationError(BookFields.ID, ValidationMessages.REQUIRED))
            }
            if (value <= 0) {
                return ValidationResult.Failure(ValidationError(BookFields.ID, ValidationMessages.AUTHOR_ID_MUST_BE_POSITIVE))
            }
            return ValidationResult.Success(BookId(value))
        }

        /**
         * DBから取得する際に使用（バリデーションなし）
         * 
         * DBの生値をそのままラップする。すでに不正な値が入っている可能性もある。
         * 不変条件は保証しないので、利用側で必要に応じてチェックすること。
         */
        fun reconstruct(value: Long): BookId {
            return BookId(value)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BookId) return false
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "BookId(value=$value)"
    }
}

