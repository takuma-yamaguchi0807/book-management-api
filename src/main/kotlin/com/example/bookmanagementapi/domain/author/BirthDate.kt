package com.example.bookmanagementapi.domain.author

import com.example.bookmanagementapi.domain.ValidationResult
import com.example.bookmanagementapi.domain.exception.ValidationError
import com.example.bookmanagementapi.domain.message.ValidationMessages
import com.example.bookmanagementapi.presentation.author.AuthorFields
import java.time.LocalDate
import java.time.format.DateTimeParseException

/**
 * 生年月日の値オブジェクト
 */
class BirthDate private constructor(val value: LocalDate) {
    companion object {
        /**
         * リクエストから作成する際に使用（バリデーションあり）
         */
        fun create(value: String?): ValidationResult<BirthDate> {
            if (value.isNullOrBlank()) {
                return ValidationResult.Failure(ValidationError(AuthorFields.BIRTH_DATE, ValidationMessages.REQUIRED))
            }
            
            val parsedDate = try {
                LocalDate.parse(value)
            } catch (e: DateTimeParseException) {
                return ValidationResult.Failure(ValidationError(AuthorFields.BIRTH_DATE, ValidationMessages.INVALID_DATE_FORMAT))
            }
            
            if (!parsedDate.isBefore(LocalDate.now())) {
                return ValidationResult.Failure(ValidationError(AuthorFields.BIRTH_DATE, ValidationMessages.BIRTH_DATE_MUST_BE_PAST))
            }
            
            return ValidationResult.Success(BirthDate(parsedDate))
        }

        /**
         * DBから取得する際に使用（バリデーションなし）
         * 
         * DBの生値をそのままラップする。すでに不正な値が入っている可能性もある。
         * 不変条件は保証しないので、利用側で必要に応じてチェックすること。
         */
        fun reconstruct(value: LocalDate): BirthDate {
            return BirthDate(value)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BirthDate) return false
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "BirthDate(value=$value)"
    }
}

