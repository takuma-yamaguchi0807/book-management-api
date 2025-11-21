package com.example.bookmanagementapi.domain.book

import com.example.bookmanagementapi.domain.ValidationResult
import com.example.bookmanagementapi.domain.exception.ValidationError
import com.example.bookmanagementapi.domain.message.ValidationMessages
import com.example.bookmanagementapi.presentation.book.BookFields

/**
 * 出版ステータスの値オブジェクト
 */
class PublishedStatus private constructor(val value: Boolean) {
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
         * DBから取得する際に使用（バリデーションなし）
         * 
         * DBの生値をそのままラップする。すでに不正な値が入っている可能性もある。
         * 不変条件は保証しないので、利用側で必要に応じてチェックすること。
         */
        fun reconstruct(value: Boolean): PublishedStatus {
            return PublishedStatus(value)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PublishedStatus) return false
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "PublishedStatus(value=$value)"
    }
}

