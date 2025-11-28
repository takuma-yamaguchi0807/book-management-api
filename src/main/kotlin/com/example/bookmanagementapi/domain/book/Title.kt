package com.example.bookmanagementapi.domain.book

import com.example.bookmanagementapi.domain.ValidationResult
import com.example.bookmanagementapi.domain.exception.ValidationError
import com.example.bookmanagementapi.domain.message.ValidationMessages
import com.example.bookmanagementapi.presentation.book.BookFields

/**
 * 書籍タイトルの値オブジェクト
 */
data class Title private constructor(val value: String) {
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
         * 再構築用
         *
         * 永続化された値を元にインスタンスを生成する。
         * 値は必須であり、nullの場合は例外をスローする。
         * 不変条件は保証しないので、利用側で必要に応じてチェックすること。
         */
        fun reconstruct(value: String?): Title {
            requireNotNull(value) { "Title must not be null" }
            return Title(value)
        }
    }
}

