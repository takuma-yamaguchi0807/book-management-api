package com.example.bookmanagementapi.domain.book

import com.example.bookmanagementapi.domain.ValidationResult
import com.example.bookmanagementapi.domain.exception.ValidationError
import com.example.bookmanagementapi.domain.message.ValidationMessages
import com.example.bookmanagementapi.presentation.book.BookFields

/**
 * 出版ステータスの値オブジェクト
 */
data class PublishedStatus private constructor(val value: Boolean) {
    companion object {
        /**
         * リクエストから作成する際に使用（バリデーションあり）
         */
        fun create(value: Boolean?): ValidationResult<PublishedStatus> {
            if (value == null) {
                return ValidationResult.Failure(ValidationError(BookFields.PUBLISHED, ValidationMessages.REQUIRED))
            }
            return ValidationResult.Success(PublishedStatus(value))
        }

        /**
         * 再構築用
         *
         * 永続化された値を元にインスタンスを生成する。
         * 値は必須であり、nullの場合は例外をスローする。
         * 不変条件は保証しないので、利用側で必要に応じてチェックすること。
         */
        fun reconstruct(value: Boolean?): PublishedStatus {
            requireNotNull(value) { "PublishedStatus must not be null" }
            return PublishedStatus(value)
        }
    }
}

