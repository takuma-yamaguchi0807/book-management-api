package com.example.bookmanagementapi.infra.repository

import com.example.bookmanagementapi.domain.author.Author
import com.example.bookmanagementapi.domain.author.AuthorId
import com.example.bookmanagementapi.domain.author.AuthorRepository
import com.example.bookmanagementapi.infra.entity.AuthorEntity
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

/**
 * 著者リポジトリ実装
 */
@Repository
class AuthorRepositoryImpl(
    private val dsl: DSLContext
) : AuthorRepository {
    
    companion object {
        private val TABLE = DSL.table("authors")
        private val ID = DSL.field("id", Long::class.java)
        private val NAME = DSL.field("name", String::class.java)
        private val BIRTH_DATE = DSL.field("birth_date", java.sql.Date::class.java)
        private val CREATED_AT = DSL.field("created_at", java.sql.Timestamp::class.java)
        private val UPDATED_AT = DSL.field("updated_at", java.sql.Timestamp::class.java)
    }
    
    override fun save(author: Author): AuthorId {
        val now = LocalDateTime.now()
        
        // INSERT実行
        val insertedRecord = dsl.insertInto(TABLE)
            .set(NAME, author.name.value)
            .set(BIRTH_DATE, java.sql.Date.valueOf(author.birthDate.value))
            .set(CREATED_AT, java.sql.Timestamp.valueOf(now))
            .set(UPDATED_AT, java.sql.Timestamp.valueOf(now))
            .returningResult(ID)
            .fetchOne()
        
        val savedId = insertedRecord?.getValue(ID)
            ?: throw IllegalStateException("Failed to get generated ID")
        
        // IDだけが必要な場合は、直接AuthorIdを返す
        return AuthorId.reconstruct(savedId)
    }

    override fun findById(id: AuthorId): Author? {
        val record = dsl.select(ID, NAME, BIRTH_DATE, CREATED_AT, UPDATED_AT)
            .from(TABLE)
            .where(ID.eq(id.value))
            .fetchOne()

        return record?.let {
            val entity = AuthorEntity(
                id = it.getValue(ID),
                name = it.getValue(NAME),
                birthDate = it.getValue(BIRTH_DATE).toLocalDate(),
                createdAt = it.getValue(CREATED_AT).toLocalDateTime(),
                updatedAt = it.getValue(UPDATED_AT).toLocalDateTime()
            )
            entity.toDomain()
        }
    }

    override fun findByIds(ids: List<AuthorId>): List<Author> {
        if (ids.isEmpty()) {
            return emptyList()
        }
        
        val idValues = ids.map { it.value }
        
        val records = dsl.select(ID, NAME, BIRTH_DATE, CREATED_AT, UPDATED_AT)
            .from(TABLE)
            .where(ID.`in`(idValues))
            .fetch()

        return records.map { record ->
            val entity = AuthorEntity(
                id = record.getValue(ID),
                name = record.getValue(NAME),
                birthDate = record.getValue(BIRTH_DATE).toLocalDate(),
                createdAt = record.getValue(CREATED_AT).toLocalDateTime(),
                updatedAt = record.getValue(UPDATED_AT).toLocalDateTime()
            )
            entity.toDomain()
        }
    }

    override fun update(author: Author) {
        val now = LocalDateTime.now()
        val authorId = author.id ?: throw IllegalStateException("Author.id must not be null for update")

        dsl.update(TABLE)
            .set(NAME, author.name.value)
            .set(BIRTH_DATE, java.sql.Date.valueOf(author.birthDate.value))
            .set(UPDATED_AT, java.sql.Timestamp.valueOf(now))
            .where(ID.eq(authorId.value))
            .execute()
    }
}

