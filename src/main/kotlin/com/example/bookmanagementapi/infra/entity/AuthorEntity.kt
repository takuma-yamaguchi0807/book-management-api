package com.example.bookmanagementapi.infra.entity

import com.example.bookmanagementapi.domain.author.Author
import com.example.bookmanagementapi.domain.author.AuthorId
import com.example.bookmanagementapi.domain.author.AuthorName
import com.example.bookmanagementapi.domain.author.BirthDate
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 著者エンティティ（DB用）
 * authorsテーブルに対応
 */
data class AuthorEntity(
    val id: Long?,
    val name: String,
    val birthDate: LocalDate,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    /**
     * ドメインオブジェクトに変換
     */
    fun toDomain(): Author {
        return Author.reconstruct(
            id = id?.let { AuthorId.reconstruct(it) }
                ?: throw IllegalStateException("AuthorEntity.id must not be null"),
            name = AuthorName.reconstruct(name),
            birthDate = BirthDate.reconstruct(birthDate)
        )
    }
}

