package com.example.bookmanagementapi.usecase.book

import com.example.bookmanagementapi.domain.author.Author
import com.example.bookmanagementapi.domain.author.AuthorId
import com.example.bookmanagementapi.domain.author.AuthorName
import com.example.bookmanagementapi.domain.author.AuthorRepository
import com.example.bookmanagementapi.domain.author.BirthDate
import com.example.bookmanagementapi.domain.book.BookAuthorRepository
import com.example.bookmanagementapi.domain.book.BookId
import com.example.bookmanagementapi.domain.book.BookRepository
import com.example.bookmanagementapi.domain.exception.DomainValidationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.Mockito.verify
import com.example.bookmanagementapi.domain.book.Book
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
@DisplayName("CreateBookUsecase テスト")
class CreateBookUsecaseTest {

    @Mock
    private lateinit var bookRepository: BookRepository

    @Mock
    private lateinit var bookAuthorRepository: BookAuthorRepository

    @Mock
    private lateinit var authorRepository: AuthorRepository

    @InjectMocks
    private lateinit var usecase: CreateBookUsecase

    @Nested
    @DisplayName("正常系")
    inner class Normal {
        @Test
        @DisplayName("有効なリクエストで書籍が作成されること（単一著者）")
        fun createBook_success_singleAuthor() {
            // given
            val request = CreateBookRequestDto(
                title = "プログラミング入門",
                price = 1980,
                authorIds = listOf(1L),
                published = true
            )
            val expectedBookId = BookId.reconstruct(1L)
            val author = Author.reconstruct(
                id = AuthorId.reconstruct(1L),
                name = AuthorName.reconstruct("山田太郎"),
                birthDate = BirthDate.reconstruct(LocalDate.of(1980, 5, 15))
            )
            
            given(bookRepository.save(any())).willReturn(expectedBookId)
            given(authorRepository.findByIds(any())).willReturn(listOf(author))

            // when
            val result = usecase.execute(request)

            // then
            assertEquals(expectedBookId, result)
            verify(bookRepository).save(any())
            verify(bookAuthorRepository).save(any(), any())
        }

        @Test
        @DisplayName("有効なリクエストで書籍が作成されること（複数著者）")
        fun createBook_success_multipleAuthors() {
            // given
            val request = CreateBookRequestDto(
                title = "システム設計の基礎",
                price = 2500,
                authorIds = listOf(1L, 2L),
                published = false
            )
            val expectedBookId = BookId.reconstruct(1L)
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
            
            given(bookRepository.save(any())).willReturn(expectedBookId)
            given(authorRepository.findByIds(any())).willReturn(authors)

            // when
            val result = usecase.execute(request)

            // then
            assertEquals(expectedBookId, result)
            verify(bookRepository).save(any())
            verify(bookAuthorRepository).save(any(), any())
        }
    }

    @Nested
    @DisplayName("異常系")
    inner class Abnormal {
        @Test
        @DisplayName("タイトルがnullの場合、DomainValidationExceptionがスローされること")
        fun createBook_titleIsNull() {
            // given
            val request = CreateBookRequestDto(
                title = null,
                price = 1980,
                authorIds = listOf(1L),
                published = true
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("入力してください", exception.errors["title"])
        }

        @ParameterizedTest
        @ValueSource(ints = [-1, -100])
        @DisplayName("価格が0未満の場合、DomainValidationExceptionがスローされること")
        fun createBook_priceIsNegative(price: Int) {
            // given
            val request = CreateBookRequestDto(
                title = "プログラミング入門",
                price = price,
                authorIds = listOf(1L),
                published = true
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("価格は0円以上である必要があります", exception.errors["price"])
        }

        @Test
        @DisplayName("著者IDリストがnullの場合、DomainValidationExceptionがスローされること")
        fun createBook_authorIdsIsNull() {
            // given
            val request = CreateBookRequestDto(
                title = "プログラミング入門",
                price = 1980,
                authorIds = null,
                published = true
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("著者は1名以上必要です", exception.errors["author_ids"])
        }

        @Test
        @DisplayName("著者IDリストが空の場合、DomainValidationExceptionがスローされること")
        fun createBook_authorIdsIsEmpty() {
            // given
            val request = CreateBookRequestDto(
                title = "プログラミング入門",
                price = 1980,
                authorIds = emptyList(),
                published = true
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("著者は1名以上必要です", exception.errors["author_ids"])
        }

        @Test
        @DisplayName("著者IDリストに重複がある場合、DomainValidationExceptionがスローされること")
        fun createBook_authorIdsHasDuplicate() {
            // given
            val request = CreateBookRequestDto(
                title = "プログラミング入門",
                price = 1980,
                authorIds = listOf(1L, 1L),
                published = true
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("著者IDに重複があります", exception.errors["author_ids"])
        }

        @Test
        @DisplayName("著者IDが存在しない場合、DomainValidationExceptionがスローされること")
        fun createBook_authorIdNotFound() {
            // given
            val request = CreateBookRequestDto(
                title = "プログラミング入門",
                price = 1980,
                authorIds = listOf(999L),
                published = true
            )
            
            given(authorRepository.findByIds(any())).willReturn(emptyList())

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("指定されたIDは存在しません", exception.errors["author_ids[0]"])
        }

        @Test
        @DisplayName("複数の著者IDのうち一部が存在しない場合、存在しないIDのみエラーが返されること")
        fun createBook_someAuthorIdsNotFound() {
            // given
            val request = CreateBookRequestDto(
                title = "プログラミング入門",
                price = 1980,
                authorIds = listOf(1L, 999L, 998L),
                published = true
            )
            val existingAuthor = Author.reconstruct(
                id = AuthorId.reconstruct(1L),
                name = AuthorName.reconstruct("山田太郎"),
                birthDate = BirthDate.reconstruct(LocalDate.of(1980, 5, 15))
            )
            
            given(authorRepository.findByIds(any())).willReturn(listOf(existingAuthor))

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(request)
            }
            assertEquals(2, exception.errors.size)
            assertEquals("指定されたIDは存在しません", exception.errors["author_ids[1]"])
            assertEquals("指定されたIDは存在しません", exception.errors["author_ids[2]"])
        }

        @ParameterizedTest
        @ValueSource(longs = [0, -1, -100])
        @DisplayName("著者IDが0以下の場合、DomainValidationExceptionがスローされること")
        fun createBook_authorIdIsInvalid(authorId: Long) {
            // given
            val request = CreateBookRequestDto(
                title = "プログラミング入門",
                price = 1980,
                authorIds = listOf(authorId),
                published = true
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("著者IDは1以上である必要があります", exception.errors["author_ids[0]"])
        }

        @Test
        @DisplayName("複数の著者IDのうち一部が0以下の場合、該当するIDのみエラーが返されること")
        fun createBook_someAuthorIdsAreInvalid() {
            // given
            val request = CreateBookRequestDto(
                title = "プログラミング入門",
                price = 1980,
                authorIds = listOf(1L, 0L, -1L),
                published = true
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(request)
            }
            assertEquals(2, exception.errors.size)
            assertEquals("著者IDは1以上である必要があります", exception.errors["author_ids[1]"])
            assertEquals("著者IDは1以上である必要があります", exception.errors["author_ids[2]"])
        }

        @Test
        @DisplayName("出版ステータスがnullの場合、DomainValidationExceptionがスローされること")
        fun createBook_publishedIsNull() {
            // given
            val request = CreateBookRequestDto(
                title = "プログラミング入門",
                price = 1980,
                authorIds = listOf(1L),
                published = null
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("入力してください", exception.errors["published"])
        }
    }
}

