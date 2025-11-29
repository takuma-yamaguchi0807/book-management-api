package com.example.bookmanagementapi.infra.repository

import com.example.bookmanagementapi.domain.author.Author
import com.example.bookmanagementapi.domain.author.AuthorId
import com.example.bookmanagementapi.domain.author.AuthorName
import com.example.bookmanagementapi.domain.author.BirthDate
import com.example.bookmanagementapi.domain.book.Book
import com.example.bookmanagementapi.domain.book.BookId
import com.example.bookmanagementapi.domain.book.Price
import com.example.bookmanagementapi.domain.book.PublishedStatus
import com.example.bookmanagementapi.domain.book.Title
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
@DisplayName("BookRepositoryImpl テスト")
class BookRepositoryImplTest {

    @Autowired
    private lateinit var bookRepository: BookRepositoryImpl

    @Autowired
    private lateinit var authorRepository: AuthorRepositoryImpl

    @Autowired
    private lateinit var bookAuthorRepository: BookAuthorRepositoryImpl

    @Nested
    @DisplayName("save")
    inner class Save {
        @Test
        @DisplayName("有効な書籍を保存すると、IDが生成されて返されること")
        fun save_success() {
            // given
            val author = Author.create(
                name = AuthorName.reconstruct("山田太郎"),
                birthDate = BirthDate.reconstruct(LocalDate.of(1980, 5, 15))
            )
            val savedAuthorId = authorRepository.save(author)

            val book = Book.create(
                title = Title.reconstruct("プログラミング入門"),
                price = Price.reconstruct(1980),
                publishedStatus = PublishedStatus.reconstruct(true),
                authorIds = listOf(savedAuthorId)
            )

            // when
            val savedId = bookRepository.save(book)

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
        @DisplayName("存在するIDで書籍が取得できること（著者情報も含む）")
        fun findById_success() {
            // given
            val author = Author.create(
                name = AuthorName.reconstruct("山田太郎"),
                birthDate = BirthDate.reconstruct(LocalDate.of(1980, 5, 15))
            )
            val savedAuthorId = authorRepository.save(author)

            val book = Book.create(
                title = Title.reconstruct("プログラミング入門"),
                price = Price.reconstruct(1980),
                publishedStatus = PublishedStatus.reconstruct(true),
                authorIds = listOf(savedAuthorId)
            )
            val savedBookId = bookRepository.save(book)
            bookAuthorRepository.save(savedBookId, listOf(savedAuthorId))

            // when
            val found = bookRepository.findById(savedBookId)

            // then
            assertNotNull(found)
            assertEquals(savedBookId.value, found?.id?.value)
            assertEquals("プログラミング入門", found?.title?.value)
            assertEquals(1980, found?.price?.value)
            assertEquals(true, found?.publishedStatus?.value)
            assertEquals(1, found?.authorIds?.size)
            assertEquals(savedAuthorId.value, found?.authorIds?.get(0)?.value)
        }

        @Test
        @DisplayName("複数の著者が紐づく書籍が取得できること")
        fun findById_success_multipleAuthors() {
            // given
            val author1 = Author.create(
                name = AuthorName.reconstruct("山田太郎"),
                birthDate = BirthDate.reconstruct(LocalDate.of(1980, 5, 15))
            )
            val author2 = Author.create(
                name = AuthorName.reconstruct("佐藤花子"),
                birthDate = BirthDate.reconstruct(LocalDate.of(1985, 3, 20))
            )
            val savedAuthorId1 = authorRepository.save(author1)
            val savedAuthorId2 = authorRepository.save(author2)

            val book = Book.create(
                title = Title.reconstruct("システム設計の基礎"),
                price = Price.reconstruct(2500),
                publishedStatus = PublishedStatus.reconstruct(false),
                authorIds = listOf(savedAuthorId1, savedAuthorId2)
            )
            val savedBookId = bookRepository.save(book)
            bookAuthorRepository.save(savedBookId, listOf(savedAuthorId1, savedAuthorId2))

            // when
            val found = bookRepository.findById(savedBookId)

            // then
            assertNotNull(found)
            assertEquals(2, found?.authorIds?.size)
            val foundAuthorIds = found?.authorIds?.map { it.value }?.toSet()
            assertEquals(setOf(savedAuthorId1.value, savedAuthorId2.value), foundAuthorIds)
        }

        @Test
        @DisplayName("存在しないIDでnullが返されること")
        fun findById_notFound() {
            // given
            val nonExistentId = BookId.reconstruct(999L)

            // when
            val found = bookRepository.findById(nonExistentId)

            // then
            assertNull(found)
        }
    }

    @Nested
    @DisplayName("update")
    inner class Update {
        @Test
        @DisplayName("存在する書籍の更新が成功すること")
        fun update_success() {
            // given
            val author = Author.create(
                name = AuthorName.reconstruct("山田太郎"),
                birthDate = BirthDate.reconstruct(LocalDate.of(1980, 5, 15))
            )
            val savedAuthorId = authorRepository.save(author)

            val book = Book.create(
                title = Title.reconstruct("プログラミング入門"),
                price = Price.reconstruct(1980),
                publishedStatus = PublishedStatus.reconstruct(false),
                authorIds = listOf(savedAuthorId)
            )
            val savedBookId = bookRepository.save(book)
            bookAuthorRepository.save(savedBookId, listOf(savedAuthorId))

            val updatedBook = Book.update(
                id = savedBookId,
                title = Title.reconstruct("プログラミング入門（改訂版）"),
                price = Price.reconstruct(2200),
                publishedStatus = PublishedStatus.reconstruct(true),
                authorIds = listOf(savedAuthorId)
            )

            // when
            bookRepository.update(updatedBook)

            // then
            val found = bookRepository.findById(savedBookId)
            assertNotNull(found)
            assertEquals("プログラミング入門（改訂版）", found?.title?.value)
            assertEquals(2200, found?.price?.value)
            assertEquals(true, found?.publishedStatus?.value)
        }
    }
}

