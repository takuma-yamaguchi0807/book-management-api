package com.example.bookmanagementapi.domain.author

import com.example.bookmanagementapi.domain.exception.DomainValidationException

/**
 * 著者エンティティ
 */
data class Author(
    val id: Long?,
    val name: AuthorName,
    val birthDate: BirthDate
) {
    companion object {
        /**
         * リクエストから作成（バリデーションあり、エラーを集めて返す）
         */
        fun createFromRequest(name: String?, birthDate: String?): Author {
            val errors = mutableMapOf<String, String>()
            
            // name のバリデーション
            val authorName = try {
                AuthorName.create(name)
            } catch (e: IllegalArgumentException) {
                errors["name"] = e.message ?: "入力してください"
                null
            }
            
            // birthDate のバリデーション
            val authorBirthDate = try {
                BirthDate.create(birthDate)
            } catch (e: IllegalArgumentException) {
                errors["birthDate"] = e.message ?: "入力してください"
                null
            }
            
            // エラーがある場合は例外を投げる
            if (errors.isNotEmpty()) {
                throw DomainValidationException(errors)
            }
            
            return Author(
                id = null,
                name = authorName!!,
                birthDate = authorBirthDate!!
            )
        }

        /**
         * 新規作成用（値オブジェクトから作成）
         */
        fun create(name: AuthorName, birthDate: BirthDate): Author {
            return Author(
                id = null,
                name = name,
                birthDate = birthDate
            )
        }

        /**
         * DBから再構築用
         */
        fun reconstruct(id: Long, name: AuthorName, birthDate: BirthDate): Author {
            return Author(
                id = id,
                name = name,
                birthDate = birthDate
            )
        }
    }
}

