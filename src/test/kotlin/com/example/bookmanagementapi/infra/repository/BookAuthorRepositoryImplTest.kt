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
import org.junit.jupiter.api.Assertions.assertTrue
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
@DisplayName("BookAuthorRepositoryImpl テスト")
class BookAuthorRepositoryImplTest {

    @Autowired
    private lateinit var bookAuthorRepository: BookAuthorRepositoryImpl

    @Autowired
    private lateinit var authorRepository: AuthorRepositoryImpl

    @Autowired
    private lateinit var bookRepository: BookRepositoryImpl

    @Nested
    @DisplayName("save")
    inner class Save {
        @Test
        @DisplayName("単一の著者IDを保存できること")
        fun save_success_singleAuthor() {
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

            // when
            bookAuthorRepository.save(savedBookId, listOf(savedAuthorId))

            // then
            val found = bookAuthorRepository.findByBookId(savedBookId)
            assertEquals(1, found.size)
            assertEquals(savedAuthorId.value, found[0].value)
        }

        @Test
        @DisplayName("複数の著者IDを保存できること")
        fun save_success_multipleAuthors() {
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

            // when
            bookAuthorRepository.save(savedBookId, listOf(savedAuthorId1, savedAuthorId2))

            // then
            val found = bookAuthorRepository.findByBookId(savedBookId)
            assertEquals(2, found.size)
            val foundAuthorIds = found.map { it.value }.toSet()
            assertEquals(setOf(savedAuthorId1.value, savedAuthorId2.value), foundAuthorIds)
        }

        @Test
        @DisplayName("空リストで何も保存されないこと")
        fun save_emptyList() {
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

            // when
            bookAuthorRepository.save(savedBookId, emptyList())

            // then
            val found = bookAuthorRepository.findByBookId(savedBookId)
            assertEquals(0, found.size)
        }
    }

    @Nested
    @DisplayName("findByBookId")
    inner class FindByBookId {
        @Test
        @DisplayName("存在するbookIdで著者IDリストが取得できること")
        fun findByBookId_success() {
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
            val found = bookAuthorRepository.findByBookId(savedBookId)

            // then
            assertEquals(2, found.size)
            val foundAuthorIds = found.map { it.value }.toSet()
            assertEquals(setOf(savedAuthorId1.value, savedAuthorId2.value), foundAuthorIds)
        }

        @Test
        @DisplayName("存在しないbookIdで空のリストが返されること")
        fun findByBookId_notFound() {
            // given
            val nonExistentBookId = BookId.reconstruct(999L)

            // when
            val found = bookAuthorRepository.findByBookId(nonExistentBookId)

            // then
            assertEquals(0, found.size)
        }
    }

    @Nested
    @DisplayName("deleteByBookId")
    inner class DeleteByBookId {
        @Test
        @DisplayName("存在するbookIdの関連が削除されること")
        fun deleteByBookId_success() {
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

            // 削除前に存在することを確認
            val beforeDelete = bookAuthorRepository.findByBookId(savedBookId)
            assertEquals(1, beforeDelete.size)

            // when
            bookAuthorRepository.deleteByBookId(savedBookId)

            // then
            val afterDelete = bookAuthorRepository.findByBookId(savedBookId)
            assertEquals(0, afterDelete.size)
        }

        @Test
        @DisplayName("存在しないbookIdで削除してもエラーが発生しないこと")
        fun deleteByBookId_notFound() {
            // given
            val nonExistentBookId = BookId.reconstruct(999L)

            // when & then
            bookAuthorRepository.deleteByBookId(nonExistentBookId)
            // エラーが発生しないことを確認
            assertTrue(true)
        }
    }
}

