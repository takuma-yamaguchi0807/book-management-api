package com.example.bookmanagementapi.domain.book

import com.example.bookmanagementapi.domain.ValidationResult
import com.example.bookmanagementapi.domain.exception.ValidationError
import com.example.bookmanagementapi.domain.message.ValidationMessages
import com.example.bookmanagementapi.presentation.book.BookFields

/**
 * 書籍タイトルの値オブジェクト
 */
class Title private constructor(val value: String) {
    companion object {
        /**
         * リクエストから作成する際に使用（バリデーションあり）
         */
        fun create(value: String?): ValidationResult<Title> {
            return if (value.isNullOrBlank()) {
                ValidationResult.Failure(ValidationError(BookFields.TITLE, ValidationMessages.REQUIRED))
            } else {
                ValidationResult.Success(Title(value))
            }
        }

        /**
         * DBから取得する際に使用（バリデーションなし）
         * 
         * DBの生値をそのままラップする。すでに不正な値が入っている可能性もある。
         * 不変条件は保証しないので、利用側で必要に応じてチェックすること。
         */
        fun reconstruct(value: String): Title {
            return Title(value)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Title) return false
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "Title(value='$value')"
    }
}

