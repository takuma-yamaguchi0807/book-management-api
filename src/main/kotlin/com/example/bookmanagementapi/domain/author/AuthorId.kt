package com.example.bookmanagementapi.domain.author

import com.example.bookmanagementapi.domain.ValidationResult
import com.example.bookmanagementapi.domain.exception.ValidationError
import com.example.bookmanagementapi.domain.message.ValidationMessages
import com.example.bookmanagementapi.presentation.author.AuthorFields

/**
 * 著者IDの値オブジェクト
 */
data class AuthorId private constructor(val value: Long) {
    companion object {
        /**
         * リクエストから作成する際に使用（バリデーションあり）
         */
        fun create(value: Long?): ValidationResult<AuthorId> {
            if (value == null) {
                return ValidationResult.Failure(ValidationError(AuthorFields.ID, ValidationMessages.REQUIRED))
            }
            if (value <= 0) {
                return ValidationResult.Failure(ValidationError(AuthorFields.ID, ValidationMessages.AUTHOR_ID_MUST_BE_POSITIVE))
            }
            return ValidationResult.Success(AuthorId(value))
        }

        /**
         * 再構築用
         *
         * 永続化された値を元にインスタンスを生成する。
         * 値は必須であり、nullの場合は例外をスローする。
         * 不変条件は保証しないので、利用側で必要に応じてチェックすること。
         */
        fun reconstruct(value: Long?): AuthorId {
            requireNotNull(value) { "AuthorId must not be null" }
            return AuthorId(value)
        }
    }
}

