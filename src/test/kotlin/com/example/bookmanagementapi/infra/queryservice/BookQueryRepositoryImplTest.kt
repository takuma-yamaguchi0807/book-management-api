package com.example.bookmanagementapi.infra.queryservice

import com.example.bookmanagementapi.domain.author.Author
import com.example.bookmanagementapi.domain.author.AuthorId
import com.example.bookmanagementapi.domain.author.AuthorName
import com.example.bookmanagementapi.domain.author.BirthDate
import com.example.bookmanagementapi.domain.book.Book
import com.example.bookmanagementapi.domain.book.BookId
import com.example.bookmanagementapi.domain.book.Price
import com.example.bookmanagementapi.domain.book.PublishedStatus
import com.example.bookmanagementapi.domain.book.Title
import com.example.bookmanagementapi.domain.pagination.PageNumber
import com.example.bookmanagementapi.domain.pagination.PageSize
import com.example.bookmanagementapi.domain.pagination.Pagination
import com.example.bookmanagementapi.infra.repository.AuthorRepositoryImpl
import com.example.bookmanagementapi.infra.repository.BookAuthorRepositoryImpl
import com.example.bookmanagementapi.infra.repository.BookRepositoryImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
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
@DisplayName("BookQueryRepositoryImpl テスト")
class BookQueryRepositoryImplTest {

    @Autowired
    private lateinit var bookQueryRepository: BookQueryRepositoryImpl

    @Autowired
    private lateinit var authorRepository: AuthorRepositoryImpl

    @Autowired
    private lateinit var bookRepository: BookRepositoryImpl

    @Autowired
    private lateinit var bookAuthorRepository: BookAuthorRepositoryImpl

    @Nested
    @DisplayName("countByAuthorId")
    inner class CountByAuthorId {
        @Test
        @DisplayName("存在する著者IDで書籍が1件存在する場合、1が返されること")
        fun countByAuthorId_success_singleBook() {
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
            val count = bookQueryRepository.countByAuthorId(savedAuthorId)

            // then
            assertEquals(1L, count)
        }

        @Test
        @DisplayName("存在する著者IDで書籍が複数存在する場合、正しい件数が返されること")
        fun countByAuthorId_success_multipleBooks() {
            // given
            val author = Author.create(
                name = AuthorName.reconstruct("山田太郎"),
                birthDate = BirthDate.reconstruct(LocalDate.of(1980, 5, 15))
            )
            val savedAuthorId = authorRepository.save(author)

            val book1 = Book.create(
                title = Title.reconstruct("プログラミング入門"),
                price = Price.reconstruct(1980),
                publishedStatus = PublishedStatus.reconstruct(true),
                authorIds = listOf(savedAuthorId)
            )
            val book2 = Book.create(
                title = Title.reconstruct("システム設計の基礎"),
                price = Price.reconstruct(2500),
                publishedStatus = PublishedStatus.reconstruct(false),
                authorIds = listOf(savedAuthorId)
            )
            val savedBookId1 = bookRepository.save(book1)
            val savedBookId2 = bookRepository.save(book2)
            bookAuthorRepository.save(savedBookId1, listOf(savedAuthorId))
            bookAuthorRepository.save(savedBookId2, listOf(savedAuthorId))

            // when
            val count = bookQueryRepository.countByAuthorId(savedAuthorId)

            // then
            assertEquals(2L, count)
        }

        @Test
        @DisplayName("存在する著者IDで書籍が存在しない場合、0が返されること")
        fun countByAuthorId_success_noBooks() {
            // given
            val author = Author.create(
                name = AuthorName.reconstruct("山田太郎"),
                birthDate = BirthDate.reconstruct(LocalDate.of(1980, 5, 15))
            )
            val savedAuthorId = authorRepository.save(author)

            // when
            val count = bookQueryRepository.countByAuthorId(savedAuthorId)

            // then
            assertEquals(0L, count)
        }

        @Test
        @DisplayName("複数の著者が紐づく書籍がある場合、正しい件数が返されること")
        fun countByAuthorId_success_multipleAuthors() {
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

            // 著者1と著者2の両方に紐づく書籍
            val book1 = Book.create(
                title = Title.reconstruct("共著の本"),
                price = Price.reconstruct(3000),
                publishedStatus = PublishedStatus.reconstruct(true),
                authorIds = listOf(savedAuthorId1, savedAuthorId2)
            )
            val savedBookId1 = bookRepository.save(book1)
            bookAuthorRepository.save(savedBookId1, listOf(savedAuthorId1, savedAuthorId2))

            // 著者1のみに紐づく書籍
            val book2 = Book.create(
                title = Title.reconstruct("単著の本"),
                price = Price.reconstruct(2000),
                publishedStatus = PublishedStatus.reconstruct(false),
                authorIds = listOf(savedAuthorId1)
            )
            val savedBookId2 = bookRepository.save(book2)
            bookAuthorRepository.save(savedBookId2, listOf(savedAuthorId1))

            // when
            val count1 = bookQueryRepository.countByAuthorId(savedAuthorId1)
            val count2 = bookQueryRepository.countByAuthorId(savedAuthorId2)

            // then
            assertEquals(2L, count1) // 共著の本 + 単著の本
            assertEquals(1L, count2) // 共著の本のみ
        }

        @Test
        @DisplayName("存在しない著者IDで0が返されること")
        fun countByAuthorId_notFound() {
            // given
            val nonExistentAuthorId = AuthorId.reconstruct(999L)

            // when
            val count = bookQueryRepository.countByAuthorId(nonExistentAuthorId)

            // then
            assertEquals(0L, count)
        }
    }

    @Nested
    @DisplayName("findByAuthorId")
    inner class FindByAuthorId {
        @Test
        @DisplayName("存在する著者IDで書籍一覧が取得できること（単一書籍・単一著者）")
        fun findByAuthorId_success_singleBook() {
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

            val pagination = Pagination.create(
                PageNumber.reconstruct(1),
                PageSize.reconstruct(20)
            )

            // when
            val result = bookQueryRepository.findByAuthorId(savedAuthorId, pagination)

            // then
            assertEquals(1, result.books.size)
            assertEquals(savedBookId.value, result.books[0].id)
            assertEquals("プログラミング入門", result.books[0].title)
            assertEquals(1980, result.books[0].price)
            assertEquals(true, result.books[0].published)
            assertEquals(1, result.books[0].authors.size)
            assertEquals(savedAuthorId.value, result.books[0].authors[0].id)
            assertEquals("山田太郎", result.books[0].authors[0].name)
        }

        @Test
        @DisplayName("存在する著者IDで書籍一覧が取得できること（複数書籍・複数著者）")
        fun findByAuthorId_success_multipleBooks() {
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

            // 著者1と著者2の両方に紐づく書籍
            val book1 = Book.create(
                title = Title.reconstruct("共著の本"),
                price = Price.reconstruct(3000),
                publishedStatus = PublishedStatus.reconstruct(true),
                authorIds = listOf(savedAuthorId1, savedAuthorId2)
            )
            val savedBookId1 = bookRepository.save(book1)
            bookAuthorRepository.save(savedBookId1, listOf(savedAuthorId1, savedAuthorId2))

            // 著者1のみに紐づく書籍
            val book2 = Book.create(
                title = Title.reconstruct("単著の本"),
                price = Price.reconstruct(2000),
                publishedStatus = PublishedStatus.reconstruct(false),
                authorIds = listOf(savedAuthorId1)
            )
            val savedBookId2 = bookRepository.save(book2)
            bookAuthorRepository.save(savedBookId2, listOf(savedAuthorId1))

            val pagination = Pagination.create(
                PageNumber.reconstruct(1),
                PageSize.reconstruct(20)
            )

            // when
            val result = bookQueryRepository.findByAuthorId(savedAuthorId1, pagination)

            // then
            assertEquals(2, result.books.size)
            val bookIds = result.books.map { it.id }.toSet()
            assertEquals(setOf(savedBookId1.value, savedBookId2.value), bookIds)
            
            // 共著の本には2人の著者が紐づいている
            val coauthoredBook = result.books.find { it.id == savedBookId1.value }
            assertNotNull(coauthoredBook)
            assertEquals(2, coauthoredBook?.authors?.size)
            
            // 単著の本には1人の著者が紐づいている
            val soloBook = result.books.find { it.id == savedBookId2.value }
            assertNotNull(soloBook)
            assertEquals(1, soloBook?.authors?.size)
        }

        @Test
        @DisplayName("存在する著者IDで書籍が存在しない場合、空のリストが返されること")
        fun findByAuthorId_success_noBooks() {
            // given
            val author = Author.create(
                name = AuthorName.reconstruct("山田太郎"),
                birthDate = BirthDate.reconstruct(LocalDate.of(1980, 5, 15))
            )
            val savedAuthorId = authorRepository.save(author)

            val pagination = Pagination.create(
                PageNumber.reconstruct(1),
                PageSize.reconstruct(20)
            )

            // when
            val result = bookQueryRepository.findByAuthorId(savedAuthorId, pagination)

            // then
            assertEquals(0, result.books.size)
        }

        @Test
        @DisplayName("ページネーションが正しく動作すること")
        fun findByAuthorId_success_pagination() {
            // given
            val author = Author.create(
                name = AuthorName.reconstruct("山田太郎"),
                birthDate = BirthDate.reconstruct(LocalDate.of(1980, 5, 15))
            )
            val savedAuthorId = authorRepository.save(author)

            // 3件の書籍を作成
            repeat(3) { index ->
                val book = Book.create(
                    title = Title.reconstruct("本${index + 1}"),
                    price = Price.reconstruct(1000 + index * 100),
                    publishedStatus = PublishedStatus.reconstruct(true),
                    authorIds = listOf(savedAuthorId)
                )
                val savedBookId = bookRepository.save(book)
                bookAuthorRepository.save(savedBookId, listOf(savedAuthorId))
            }

            val pagination = Pagination.create(
                PageNumber.reconstruct(1),
                PageSize.reconstruct(2) // 1ページに2件
            )

            // when
            val result = bookQueryRepository.findByAuthorId(savedAuthorId, pagination)

            // then
            assertEquals(2, result.books.size) // 2件のみ取得される
        }
    }
}

