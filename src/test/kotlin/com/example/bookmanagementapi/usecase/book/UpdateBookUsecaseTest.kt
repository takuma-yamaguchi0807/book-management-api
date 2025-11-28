package com.example.bookmanagementapi.usecase.book

import com.example.bookmanagementapi.domain.author.Author
import com.example.bookmanagementapi.domain.author.AuthorId
import com.example.bookmanagementapi.domain.author.AuthorName
import com.example.bookmanagementapi.domain.author.AuthorRepository
import com.example.bookmanagementapi.domain.author.BirthDate
import com.example.bookmanagementapi.domain.book.Book
import com.example.bookmanagementapi.domain.book.BookAuthorRepository
import com.example.bookmanagementapi.domain.book.BookId
import com.example.bookmanagementapi.domain.book.BookRepository
import com.example.bookmanagementapi.domain.book.Price
import com.example.bookmanagementapi.domain.book.PublishedStatus
import com.example.bookmanagementapi.domain.book.Title
import com.example.bookmanagementapi.domain.exception.BusinessRuleViolationException
import com.example.bookmanagementapi.domain.exception.DomainValidationException
import com.example.bookmanagementapi.domain.exception.ResourceNotFoundException
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.Mockito.verify
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
@DisplayName("UpdateBookUsecase テスト")
class UpdateBookUsecaseTest {

    @Mock
    private lateinit var bookRepository: BookRepository

    @Mock
    private lateinit var bookAuthorRepository: BookAuthorRepository

    @Mock
    private lateinit var authorRepository: AuthorRepository

    @InjectMocks
    private lateinit var usecase: UpdateBookUsecase

    @Nested
    @DisplayName("正常系")
    inner class Normal {
        @Test
        @DisplayName("有効なリクエストで書籍が更新されること")
        fun updateBook_success() {
            // given
            val bookId = 1L
            val request = UpdateBookRequestDto(
                title = "プログラミング入門（改訂版）",
                price = 2200,
                authorIds = listOf(1L, 2L),
                published = true
            )
            val existingBook = Book.reconstruct(
                id = BookId.reconstruct(bookId),
                title = Title.reconstruct("プログラミング入門"),
                price = Price.reconstruct(1980),
                publishedStatus = PublishedStatus.reconstruct(false),
                authorIds = listOf(AuthorId.reconstruct(1L))
            )
            val authors = listOf(
                Author.reconstruct(
                    id = AuthorId.reconstruct(1L),
                    name = AuthorName.reconstruct("山田太郎"),
                    birthDate = BirthDate.reconstruct(LocalDate.of(1980, 5, 15))
                ),
                Author.reconstruct(
                    id = AuthorId.reconstruct(2L),
                    name = AuthorName.reconstruct("佐藤花子"),
                    birthDate = BirthDate.reconstruct(LocalDate.of(1985, 3, 20))
                )
            )
            
            given(bookRepository.findById(any())).willReturn(existingBook)
            given(authorRepository.findByIds(any())).willReturn(authors)

            // when & then
            assertDoesNotThrow {
                usecase.execute(bookId, request)
            }
            verify(bookRepository).findById(any())
            verify(bookRepository).update(any())
            verify(bookAuthorRepository).deleteByBookId(any())
            verify(bookAuthorRepository).save(any(), any())
        }

        @Test
        @DisplayName("未出版から出版済みへの変更が可能なこと")
        fun updateBook_unpublishedToPublished() {
            // given
            val bookId = 1L
            val request = UpdateBookRequestDto(
                title = "プログラミング入門",
                price = 1980,
                authorIds = listOf(1L),
                published = true
            )
            val existingBook = Book.reconstruct(
                id = BookId.reconstruct(bookId),
                title = Title.reconstruct("プログラミング入門"),
                price = Price.reconstruct(1980),
                publishedStatus = PublishedStatus.reconstruct(false),
                authorIds = listOf(AuthorId.reconstruct(1L))
            )
            val author = Author.reconstruct(
                id = AuthorId.reconstruct(1L),
                name = AuthorName.reconstruct("山田太郎"),
                birthDate = BirthDate.reconstruct(LocalDate.of(1980, 5, 15))
            )
            
            given(bookRepository.findById(any())).willReturn(existingBook)
            given(authorRepository.findByIds(any())).willReturn(listOf(author))

            // when & then
            assertDoesNotThrow {
                usecase.execute(bookId, request)
            }
        }

        @Test
        @DisplayName("出版済みから出版済みへの変更が可能なこと")
        fun updateBook_publishedToPublished() {
            // given
            val bookId = 1L
            val request = UpdateBookRequestDto(
                title = "プログラミング入門（改訂版）",
                price = 2200,
                authorIds = listOf(1L),
                published = true
            )
            val existingBook = Book.reconstruct(
                id = BookId.reconstruct(bookId),
                title = Title.reconstruct("プログラミング入門"),
                price = Price.reconstruct(1980),
                publishedStatus = PublishedStatus.reconstruct(true),
                authorIds = listOf(AuthorId.reconstruct(1L))
            )
            val author = Author.reconstruct(
                id = AuthorId.reconstruct(1L),
                name = AuthorName.reconstruct("山田太郎"),
                birthDate = BirthDate.reconstruct(LocalDate.of(1980, 5, 15))
            )
            
            given(bookRepository.findById(any())).willReturn(existingBook)
            given(authorRepository.findByIds(any())).willReturn(listOf(author))

            // when & then
            assertDoesNotThrow {
                usecase.execute(bookId, request)
            }
        }
    }

    @Nested
    @DisplayName("異常系")
    inner class Abnormal {
        @ParameterizedTest
        @ValueSource(longs = [0, -1])
        @DisplayName("IDが0以下の場合、DomainValidationExceptionがスローされること")
        fun updateBook_idIsInvalid(bookId: Long) {
            // given
            val request = UpdateBookRequestDto(
                title = "プログラミング入門",
                price = 1980,
                authorIds = listOf(1L),
                published = true
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(bookId, request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("著者IDは1以上である必要があります", exception.errors["id"])
        }

        @Test
        @DisplayName("タイトルがnullの場合、DomainValidationExceptionがスローされること")
        fun updateBook_titleIsNull() {
            // given
            val bookId = 1L
            val request = UpdateBookRequestDto(
                title = null,
                price = 1980,
                authorIds = listOf(1L),
                published = true
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(bookId, request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("入力してください", exception.errors["title"])
        }

        @ParameterizedTest
        @ValueSource(ints = [-1, -100])
        @DisplayName("価格が0未満の場合、DomainValidationExceptionがスローされること")
        fun updateBook_priceIsNegative(price: Int) {
            // given
            val bookId = 1L
            val request = UpdateBookRequestDto(
                title = "プログラミング入門",
                price = price,
                authorIds = listOf(1L),
                published = true
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(bookId, request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("価格は0円以上である必要があります", exception.errors["price"])
        }

        @Test
        @DisplayName("著者IDリストがnullの場合、DomainValidationExceptionがスローされること")
        fun updateBook_authorIdsIsNull() {
            // given
            val bookId = 1L
            val request = UpdateBookRequestDto(
                title = "プログラミング入門",
                price = 1980,
                authorIds = null,
                published = true
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(bookId, request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("著者は1名以上必要です", exception.errors["author_ids"])
        }

        @Test
        @DisplayName("著者IDリストが空の場合、DomainValidationExceptionがスローされること")
        fun updateBook_authorIdsIsEmpty() {
            // given
            val bookId = 1L
            val request = UpdateBookRequestDto(
                title = "プログラミング入門",
                price = 1980,
                authorIds = emptyList(),
                published = true
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(bookId, request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("著者は1名以上必要です", exception.errors["author_ids"])
        }

        @Test
        @DisplayName("著者IDリストに重複がある場合、DomainValidationExceptionがスローされること")
        fun updateBook_authorIdsHasDuplicate() {
            // given
            val bookId = 1L
            val request = UpdateBookRequestDto(
                title = "プログラミング入門",
                price = 1980,
                authorIds = listOf(1L, 1L),
                published = true
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(bookId, request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("著者IDに重複があります", exception.errors["author_ids"])
        }

        @ParameterizedTest
        @ValueSource(longs = [0, -1, -100])
        @DisplayName("著者IDが0以下の場合、DomainValidationExceptionがスローされること")
        fun updateBook_authorIdIsInvalid(authorId: Long) {
            // given
            val bookId = 1L
            val request = UpdateBookRequestDto(
                title = "プログラミング入門",
                price = 1980,
                authorIds = listOf(authorId),
                published = true
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(bookId, request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("著者IDは1以上である必要があります", exception.errors["author_ids[0]"])
        }

        @Test
        @DisplayName("複数の著者IDのうち一部が0以下の場合、該当するIDのみエラーが返されること")
        fun updateBook_someAuthorIdsAreInvalid() {
            // given
            val bookId = 1L
            val request = UpdateBookRequestDto(
                title = "プログラミング入門",
                price = 1980,
                authorIds = listOf(1L, 0L, -1L),
                published = true
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(bookId, request)
            }
            assertEquals(2, exception.errors.size)
            assertEquals("著者IDは1以上である必要があります", exception.errors["author_ids[1]"])
            assertEquals("著者IDは1以上である必要があります", exception.errors["author_ids[2]"])
        }

        @Test
        @DisplayName("書籍が存在しない場合、ResourceNotFoundExceptionがスローされること")
        fun updateBook_bookNotFound() {
            // given
            val bookId = 1L
            val request = UpdateBookRequestDto(
                title = "プログラミング入門",
                price = 1980,
                authorIds = listOf(1L),
                published = true
            )
            
            given(bookRepository.findById(any())).willReturn(null)

            // when & then
            assertThrows(ResourceNotFoundException::class.java) {
                usecase.execute(bookId, request)
            }
            verify(bookRepository).findById(any())
        }

        @Test
        @DisplayName("出版済みから未出版への変更が不可なこと")
        fun updateBook_publishedToUnpublished() {
            // given
            val bookId = 1L
            val request = UpdateBookRequestDto(
                title = "プログラミング入門",
                price = 1980,
                authorIds = listOf(1L),
                published = false
            )
            val existingBook = Book.reconstruct(
                id = BookId.reconstruct(bookId),
                title = Title.reconstruct("プログラミング入門"),
                price = Price.reconstruct(1980),
                publishedStatus = PublishedStatus.reconstruct(true),
                authorIds = listOf(AuthorId.reconstruct(1L))
            )
            
            given(bookRepository.findById(any())).willReturn(existingBook)

            // when & then
            assertThrows(BusinessRuleViolationException::class.java) {
                usecase.execute(bookId, request)
            }
            verify(bookRepository).findById(any())
        }

        @Test
        @DisplayName("著者IDが存在しない場合、DomainValidationExceptionがスローされること")
        fun updateBook_authorIdNotFound() {
            // given
            val bookId = 1L
            val request = UpdateBookRequestDto(
                title = "プログラミング入門",
                price = 1980,
                authorIds = listOf(999L),
                published = true
            )
            val existingBook = Book.reconstruct(
                id = BookId.reconstruct(bookId),
                title = Title.reconstruct("プログラミング入門"),
                price = Price.reconstruct(1980),
                publishedStatus = PublishedStatus.reconstruct(false),
                authorIds = listOf(AuthorId.reconstruct(1L))
            )
            
            given(bookRepository.findById(any())).willReturn(existingBook)
            given(authorRepository.findByIds(any())).willReturn(emptyList())

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(bookId, request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("指定された著者は存在しません", exception.errors["author_ids[0]"])
        }

        @Test
        @DisplayName("複数の著者IDのうち一部が存在しない場合、存在しないIDのみエラーが返されること")
        fun updateBook_someAuthorIdsNotFound() {
            // given
            val bookId = 1L
            val request = UpdateBookRequestDto(
                title = "プログラミング入門",
                price = 1980,
                authorIds = listOf(1L, 999L, 998L),
                published = true
            )
            val existingBook = Book.reconstruct(
                id = BookId.reconstruct(bookId),
                title = Title.reconstruct("プログラミング入門"),
                price = Price.reconstruct(1980),
                publishedStatus = PublishedStatus.reconstruct(false),
                authorIds = listOf(AuthorId.reconstruct(1L))
            )
            val existingAuthor = Author.reconstruct(
                id = AuthorId.reconstruct(1L),
                name = AuthorName.reconstruct("山田太郎"),
                birthDate = BirthDate.reconstruct(LocalDate.of(1980, 5, 15))
            )
            
            given(bookRepository.findById(any())).willReturn(existingBook)
            given(authorRepository.findByIds(any())).willReturn(listOf(existingAuthor))

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(bookId, request)
            }
            assertEquals(2, exception.errors.size)
            assertEquals("指定された著者は存在しません", exception.errors["author_ids[1]"])
            assertEquals("指定された著者は存在しません", exception.errors["author_ids[2]"])
        }
    }
}

