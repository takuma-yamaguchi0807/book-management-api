package com.example.bookmanagementapi.domain.author

/**
 * 著者名の値オブジェクト
 */
data class AuthorName private constructor(val value: String) {
    companion object {
        /**
         * リクエストから作成する際に使用（バリデーションあり）
         */
        fun create(value: String?): AuthorName {
            require(!value.isNullOrBlank()) { "入力してください" }
            return AuthorName(value)
        }

        /**
         * DBから取得する際に使用（バリデーションなし）
         */
        fun reconstruct(value: String): AuthorName {
            return AuthorName(value)
        }
    }
}

