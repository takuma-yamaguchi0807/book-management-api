package com.example.bookmanagementapi.domain.author

/**
 * 著者エンティティ
 */
data class Author(
    val id: AuthorId?,
    val name: AuthorName,
    val birthDate: BirthDate
) {
    companion object {
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
         * 更新用（値オブジェクトから作成）
         */
        fun update(id: AuthorId, name: AuthorName, birthDate: BirthDate): Author {
            return Author(
                id = id,
                name = name,
                birthDate = birthDate
            )
        }

        /**
         * DBから再構築用
         */
        fun reconstruct(id: AuthorId, name: AuthorName, birthDate: BirthDate): Author {
            return Author(
                id = id,
                name = name,
                birthDate = birthDate
            )
        }
    }
}

