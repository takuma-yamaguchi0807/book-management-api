package com.example.bookmanagementapi.infra.repository

import com.example.bookmanagementapi.domain.author.Author
import com.example.bookmanagementapi.domain.author.AuthorId
import com.example.bookmanagementapi.domain.author.AuthorName
import com.example.bookmanagementapi.domain.author.BirthDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("AuthorRepositoryImpl テスト")
class AuthorRepositoryImplTest {

    @Autowired
    private lateinit var authorRepository: AuthorRepositoryImpl

    @Nested
    @DisplayName("save")
    inner class Save {
        @Test
        @DisplayName("有効な著者を保存すると、IDが生成されて返されること")
        fun save_success() {
            // given
            val author = Author.create(
                name = AuthorName.reconstruct("山田太郎"),
                birthDate = BirthDate.reconstruct(LocalDate.of(1980, 5, 15))
            )

            // when
            val savedId = authorRepository.save(author)

            // then
            assertNotNull(savedId)
            assertNotNull(savedId.value)
            assert(savedId.value > 0)
        }
    }

    @Nested
    @DisplayName("findById")
    inner class FindById {
        @Test
        @DisplayName("存在するIDで著者が取得できること")
        fun findById_success() {
            // given
            val author = Author.create(
                name = AuthorName.reconstruct("山田太郎"),
                birthDate = BirthDate.reconstruct(LocalDate.of(1980, 5, 15))
            )
            val savedId = authorRepository.save(author)

            // when
            val found = authorRepository.findById(savedId)

            // then
            assertNotNull(found)
            assertEquals(savedId.value, found?.id?.value)
            assertEquals("山田太郎", found?.name?.value)
            assertEquals(LocalDate.of(1980, 5, 15), found?.birthDate?.value)
        }

        @Test
        @DisplayName("存在しないIDでnullが返されること")
        fun findById_notFound() {
            // given
            val nonExistentId = AuthorId.reconstruct(999L)

            // when
            val found = authorRepository.findById(nonExistentId)

            // then
            assertNull(found)
        }
    }

    @Nested
    @DisplayName("findByIds")
    inner class FindByIds {
        @Test
        @DisplayName("複数のIDで著者が取得できること")
        fun findByIds_success_multiple() {
            // given
            val author1 = Author.create(
                name = AuthorName.reconstruct("山田太郎"),
                birthDate = BirthDate.reconstruct(LocalDate.of(1980, 5, 15))
            )
            val author2 = Author.create(
                name = AuthorName.reconstruct("佐藤花子"),
                birthDate = BirthDate.reconstruct(LocalDate.of(1985, 3, 20))
            )
            val savedId1 = authorRepository.save(author1)
            val savedId2 = authorRepository.save(author2)

            // when
            val found = authorRepository.findByIds(listOf(savedId1, savedId2))

            // then
            assertEquals(2, found.size)
            val foundIds = found.map { it.id?.value }.toSet()
            assertEquals(setOf(savedId1.value, savedId2.value), foundIds)
        }

        @Test
        @DisplayName("単一のIDで著者が取得できること")
        fun findByIds_success_single() {
            // given
            val author = Author.create(
                name = AuthorName.reconstruct("山田太郎"),
                birthDate = BirthDate.reconstruct(LocalDate.of(1980, 5, 15))
            )
            val savedId = authorRepository.save(author)

            // when
            val found = authorRepository.findByIds(listOf(savedId))

            // then
            assertEquals(1, found.size)
            assertEquals(savedId.value, found[0].id?.value)
        }

        @Test
        @DisplayName("空リストで空のリストが返されること")
        fun findByIds_emptyList() {
            // when
            val found = authorRepository.findByIds(emptyList())

            // then
            assertEquals(0, found.size)
        }

        @Test
        @DisplayName("存在しないIDが含まれている場合、存在するIDのみが返されること")
        fun findByIds_partialNotFound() {
            // given
            val author = Author.create(
                name = AuthorName.reconstruct("山田太郎"),
                birthDate = BirthDate.reconstruct(LocalDate.of(1980, 5, 15))
            )
            val savedId = authorRepository.save(author)
            val nonExistentId = AuthorId.reconstruct(999L)

            // when
            val found = authorRepository.findByIds(listOf(savedId, nonExistentId))

            // then
            assertEquals(1, found.size)
            assertEquals(savedId.value, found[0].id?.value)
        }
    }

    @Nested
    @DisplayName("update")
    inner class Update {
        @Test
        @DisplayName("存在する著者の更新が成功すること")
        fun update_success() {
            // given
            val author = Author.create(
                name = AuthorName.reconstruct("山田太郎"),
                birthDate = BirthDate.reconstruct(LocalDate.of(1980, 5, 15))
            )
            val savedId = authorRepository.save(author)
            val savedAuthor = authorRepository.findById(savedId)!!
            
            val updatedAuthor = Author.update(
                id = savedAuthor.id!!,
                name = AuthorName.reconstruct("山田次郎"),
                birthDate = BirthDate.reconstruct(LocalDate.of(1981, 6, 20))
            )

            // when
            authorRepository.update(updatedAuthor)

            // then
            val found = authorRepository.findById(savedId)
            assertNotNull(found)
            assertEquals("山田次郎", found?.name?.value)
            assertEquals(LocalDate.of(1981, 6, 20), found?.birthDate?.value)
        }
    }
}


