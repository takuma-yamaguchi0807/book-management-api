package com.example.bookmanagementapi.usecase.book

import com.example.bookmanagementapi.domain.author.AuthorId
import com.example.bookmanagementapi.domain.exception.DomainValidationException
import com.example.bookmanagementapi.domain.pagination.PageNumber
import com.example.bookmanagementapi.domain.pagination.PageSize
import com.example.bookmanagementapi.domain.pagination.Pagination
import com.example.bookmanagementapi.domain.queryservice.BookQueryRepository
import com.example.bookmanagementapi.domain.queryservice.BookQueryResultDto
import com.example.bookmanagementapi.domain.queryservice.BookWithAuthorsDto
import com.example.bookmanagementapi.domain.queryservice.AuthorSummaryDto as QueryAuthorSummaryDto
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
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
@DisplayName("GetBooksByAuthorUsecase テスト")
class GetBooksByAuthorUsecaseTest {

    @Mock
    private lateinit var bookQueryRepository: BookQueryRepository

    @InjectMocks
    private lateinit var usecase: GetBooksByAuthorUsecase

    @Nested
    @DisplayName("正常系")
    inner class Normal {
        @Test
        @DisplayName("有効なリクエストで書籍一覧が取得されること（単一書籍）")
        fun getBooks_success_singleBook() {
            // given
            val authorId = 1L
            val pageNumber = 1
            val pageSize = 20
            val request = GetBooksByAuthorRequestDto(
                authorId = authorId,
                pageNumber = pageNumber,
                pageSize = pageSize
            )
            val now = LocalDateTime.now()
            val queryResult = BookQueryResultDto(
                books = listOf(
                    BookWithAuthorsDto(
                        id = 1L,
                        title = "プログラミング入門",
                        price = 1980,
                        published = true,
                        authors = listOf(
                            QueryAuthorSummaryDto(
                                id = 1L,
                                name = "山田太郎"
                            )
                        ),
                        createdAt = now,
                        updatedAt = now
                    )
                )
            )
            val total = 1L

            given(bookQueryRepository.countByAuthorId(any())).willReturn(total)
            given(bookQueryRepository.findByAuthorId(any(), any())).willReturn(queryResult)

            // when
            val result = usecase.execute(request)

            // then
            assertEquals(1, result.items.size)
            assertEquals(1L, result.items[0].id)
            assertEquals("プログラミング入門", result.items[0].title)
            assertEquals(1980, result.items[0].price)
            assertEquals(true, result.items[0].published)
            assertEquals(1, result.items[0].authors.size)
            assertEquals(1L, result.items[0].authors[0].id)
            assertEquals("山田太郎", result.items[0].authors[0].name)
            assertEquals(1, result.pagination.pageNumber)
            assertEquals(20, result.pagination.pageSize)
            assertEquals(1L, result.pagination.total)
            assertEquals(1, result.pagination.totalPages)
            verify(bookQueryRepository).countByAuthorId(any())
                verify(bookQueryRepository).countByAuthorId(any())
                verify(bookQueryRepository).findByAuthorId(any(), any())
        }

        @Test
        @DisplayName("有効なリクエストで書籍一覧が取得されること（複数書籍・複数著者）")
        fun getBooks_success_multipleBooks() {
            // given
            val authorId = 1L
            val pageNumber = 1
            val pageSize = 20
            val request = GetBooksByAuthorRequestDto(
                authorId = authorId,
                pageNumber = pageNumber,
                pageSize = pageSize
            )
            val now = LocalDateTime.now()
            val queryResult = BookQueryResultDto(
                books = listOf(
                    BookWithAuthorsDto(
                        id = 1L,
                        title = "プログラミング入門",
                        price = 1980,
                        published = true,
                        authors = listOf(
                            QueryAuthorSummaryDto(id = 1L, name = "山田太郎"),
                            QueryAuthorSummaryDto(id = 2L, name = "佐藤花子")
                        ),
                        createdAt = now,
                        updatedAt = now
                    ),
                    BookWithAuthorsDto(
                        id = 2L,
                        title = "システム設計の基礎",
                        price = 2500,
                        published = false,
                        authors = listOf(
                            QueryAuthorSummaryDto(id = 1L, name = "山田太郎")
                        ),
                        createdAt = now,
                        updatedAt = now
                    )
                )
            )
            val total = 2L

            given(bookQueryRepository.countByAuthorId(any())).willReturn(total)
            given(bookQueryRepository.findByAuthorId(any(), any())).willReturn(queryResult)

            // when
            val result = usecase.execute(request)

            // then
            assertEquals(2, result.items.size)
            assertEquals(1L, result.items[0].id)
            assertEquals(2, result.items[0].authors.size)
            assertEquals(2L, result.items[1].id)
            assertEquals(1, result.items[1].authors.size)
            assertEquals(2L, result.pagination.total)
            assertEquals(1, result.pagination.totalPages)
        }

        @Test
        @DisplayName("書籍が存在しない場合、空のリストが返されること")
        fun getBooks_success_emptyList() {
            // given
            val authorId = 1L
            val pageNumber = 1
            val pageSize = 20
            val request = GetBooksByAuthorRequestDto(
                authorId = authorId,
                pageNumber = pageNumber,
                pageSize = pageSize
            )
            val queryResult = BookQueryResultDto(books = emptyList())
            val total = 0L

            given(bookQueryRepository.countByAuthorId(any())).willReturn(total)
            given(bookQueryRepository.findByAuthorId(any(), any())).willReturn(queryResult)

            // when
            val result = usecase.execute(request)

            // then
            assertEquals(0, result.items.size)
            assertEquals(0L, result.pagination.total)
            assertEquals(0, result.pagination.totalPages)
        }

        @Test
        @DisplayName("ページネーションが正しく計算されること（複数ページ）")
        fun getBooks_success_pagination() {
            // given
            val authorId = 1L
            val pageNumber = 2
            val pageSize = 10
            val request = GetBooksByAuthorRequestDto(
                authorId = authorId,
                pageNumber = pageNumber,
                pageSize = pageSize
            )
            val queryResult = BookQueryResultDto(books = emptyList())
            val total = 25L // 25件の総件数、10件/ページなので3ページ

            given(bookQueryRepository.countByAuthorId(any())).willReturn(total)
            given(bookQueryRepository.findByAuthorId(any(), any())).willReturn(queryResult)

            // when
            val result = usecase.execute(request)

            // then
            assertEquals(2, result.pagination.pageNumber)
            assertEquals(10, result.pagination.pageSize)
            assertEquals(25L, result.pagination.total)
            assertEquals(3, result.pagination.totalPages) // 25件 ÷ 10件/ページ = 3ページ
        }
    }

    @Nested
    @DisplayName("異常系")
    inner class Abnormal {
        @ParameterizedTest
        @ValueSource(longs = [0, -1, -100])
        @DisplayName("著者IDが0以下の場合、DomainValidationExceptionがスローされること")
        fun getBooks_authorIdIsInvalid(authorId: Long) {
            // given
            val request = GetBooksByAuthorRequestDto(
                authorId = authorId,
                pageNumber = 1,
                pageSize = 20
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("1以上である必要があります", exception.errors["author_id"])
        }


        @ParameterizedTest
        @ValueSource(ints = [0, -1, -100])
        @DisplayName("ページ番号が0以下の場合、DomainValidationExceptionがスローされること")
        fun getBooks_pageNumberIsInvalid(pageNumber: Int) {
            // given
            val request = GetBooksByAuthorRequestDto(
                authorId = 1L,
                pageNumber = pageNumber,
                pageSize = 20
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("1以上である必要があります", exception.errors["page_number"])
        }


        @ParameterizedTest
        @ValueSource(ints = [0, -1, -100])
        @DisplayName("ページサイズが0以下の場合、DomainValidationExceptionがスローされること")
        fun getBooks_pageSizeIsInvalid_negative(pageSize: Int) {
            // given
            val request = GetBooksByAuthorRequestDto(
                authorId = 1L,
                pageNumber = 1,
                pageSize = pageSize
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("1以上100未満である必要があります", exception.errors["page_size"])
        }

        @ParameterizedTest
        @ValueSource(ints = [100, 101, 200])
        @DisplayName("ページサイズが100以上の場合、DomainValidationExceptionがスローされること")
        fun getBooks_pageSizeIsInvalid_tooLarge(pageSize: Int) {
            // given
            val request = GetBooksByAuthorRequestDto(
                authorId = 1L,
                pageNumber = 1,
                pageSize = pageSize
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("1以上100未満である必要があります", exception.errors["page_size"])
        }


        @Test
        @DisplayName("複数のバリデーションエラーが同時に発生する場合、すべてのエラーが返されること")
        fun getBooks_multipleValidationErrors() {
            // given
            val request = GetBooksByAuthorRequestDto(
                authorId = 0L,
                pageNumber = 0,
                pageSize = 100
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(request)
            }
            assertEquals(3, exception.errors.size)
            assertEquals("1以上である必要があります", exception.errors["author_id"])
            assertEquals("1以上である必要があります", exception.errors["page_number"])
            assertEquals("1以上100未満である必要があります", exception.errors["page_size"])
        }
    }
}

