package com.example.bookmanagementapi.domain.author

import com.example.bookmanagementapi.domain.ValidationResult
import com.example.bookmanagementapi.domain.exception.ValidationError
import com.example.bookmanagementapi.domain.message.ValidationMessages
import com.example.bookmanagementapi.presentation.author.AuthorFields

/**
 * 著者名の値オブジェクト
 */
data class AuthorName private constructor(val value: String) {
    companion object {
        /**
         * リクエストから作成する際に使用（バリデーションあり）
         */
        fun create(value: String?): ValidationResult<AuthorName> {
            return if (value.isNullOrBlank()) {
                ValidationResult.Failure(ValidationError(AuthorFields.NAME, ValidationMessages.REQUIRED))
            } else {
                ValidationResult.Success(AuthorName(value))
            }
        }

        /**
         * 再構築用
         *
         * 永続化された値を元にインスタンスを生成する。
         * 値は必須であり、nullの場合は例外をスローする。
         * 不変条件は保証しないので、利用側で必要に応じてチェックすること。
         */
        fun reconstruct(value: String?): AuthorName {
            requireNotNull(value) { "AuthorName must not be null" }
            return AuthorName(value)
        }
    }
}

