package com.example.bookmanagementapi.domain.author

import com.example.bookmanagementapi.domain.ValidationResult
import com.example.bookmanagementapi.domain.exception.ValidationError
import com.example.bookmanagementapi.domain.message.ValidationMessages
import com.example.bookmanagementapi.presentation.author.AuthorFields

/**
 * 著者IDの値オブジェクト
 */
class AuthorId private constructor(val value: Long) {
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
         * DBから取得する際に使用（バリデーションなし）
         * 
         * DBの生値をそのままラップする。すでに不正な値が入っている可能性もある。
         * 不変条件は保証しないので、利用側で必要に応じてチェックすること。
         */
        fun reconstruct(value: Long): AuthorId {
            return AuthorId(value)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AuthorId) return false
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "AuthorId(value=$value)"
    }
}

