package com.example.bookmanagementapi.domain.author

import java.time.LocalDate
import java.time.format.DateTimeParseException

/**
 * 生年月日の値オブジェクト
 */
data class BirthDate private constructor(val value: LocalDate) {
    companion object {
        /**
         * リクエストから作成する際に使用（バリデーションあり）
         */
        fun create(value: String?): BirthDate {
            require(!value.isNullOrBlank()) { "入力してください" }
            val parsedDate = try {
                LocalDate.parse(value)
            } catch (e: DateTimeParseException) {
                throw IllegalArgumentException("日付形式が不正です", e)
            }
            require(parsedDate.isBefore(LocalDate.now())) {
                "生年月日は現在より過去である必要があります"
            }
            return BirthDate(parsedDate)
        }

        /**
         * DBから取得する際に使用（バリデーションなし）
         */
        fun reconstruct(value: LocalDate): BirthDate {
            return BirthDate(value)
        }
    }
}

