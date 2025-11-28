package com.example.bookmanagementapi.presentation.book

import com.example.bookmanagementapi.domain.book.BookId
import com.example.bookmanagementapi.domain.exception.BusinessRuleViolationException
import com.example.bookmanagementapi.domain.exception.DomainValidationException
import com.example.bookmanagementapi.domain.exception.ResourceNotFoundException
import com.example.bookmanagementapi.usecase.book.AuthorSummaryDto
import com.example.bookmanagementapi.usecase.book.BookItemDto
import com.example.bookmanagementapi.usecase.book.CreateBookUsecase
import com.example.bookmanagementapi.usecase.book.GetBooksByAuthorResponseDto
import com.example.bookmanagementapi.usecase.book.GetBooksByAuthorUsecase
import com.example.bookmanagementapi.usecase.book.PaginationDto
import com.example.bookmanagementapi.usecase.book.UpdateBookUsecase
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.mockito.BDDMockito.given
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.Mockito.verify
import java.time.LocalDateTime

@WebMvcTest(BookController::class)
@DisplayName("BookController テスト")
class BookControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var createBookUsecase: CreateBookUsecase

    @MockitoBean
    private lateinit var updateBookUsecase: UpdateBookUsecase

    @MockitoBean
    private lateinit var getBooksByAuthorUsecase: GetBooksByAuthorUsecase

    @Nested
    @DisplayName("GET /api/v1/books")
    inner class GetBooks {
        @Nested
        @DisplayName("正常系")
        inner class Normal {
            @Test
            @DisplayName("有効なリクエストで200 OKと書籍一覧が返されること")
            fun getBooks_success() {
                // given
                val authorId = 1L
                val now = LocalDateTime.now()
                val responseDto = GetBooksByAuthorResponseDto(
                    items = listOf(
                        BookItemDto(
                            id = 1L,
                            title = "プログラミング入門",
                            price = 1980,
                            published = true,
                            authors = listOf(
                                AuthorSummaryDto(
                                    id = 1L,
                                    name = "山田太郎"
                                )
                            ),
                            createdAt = now,
                            updatedAt = now
                        )
                    ),
                    pagination = PaginationDto(
                        pageNumber = 1,
                        pageSize = 20,
                        total = 1L,
                        totalPages = 1
                    )
                )
                
                given(getBooksByAuthorUsecase.execute(any())).willReturn(responseDto)

                // when & then
                mockMvc.perform(
                    get("/api/v1/books")
                        .param("authorId", authorId.toString())
                        .param("pageNumber", "1")
                        .param("pageSize", "20")
                )
                    .andExpect(status().isOk)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.items").isArray)
                    .andExpect(jsonPath("$.items[0].id").value(1L))
                    .andExpect(jsonPath("$.items[0].title").value("プログラミング入門"))
                    .andExpect(jsonPath("$.items[0].price").value(1980))
                    .andExpect(jsonPath("$.items[0].published").value(true))
                    .andExpect(jsonPath("$.items[0].authors[0].id").value(1L))
                    .andExpect(jsonPath("$.items[0].authors[0].name").value("山田太郎"))
                    .andExpect(jsonPath("$.pagination.pageNumber").value(1))
                    .andExpect(jsonPath("$.pagination.pageSize").value(20))
                    .andExpect(jsonPath("$.pagination.total").value(1L))
                    .andExpect(jsonPath("$.pagination.totalPages").value(1))
                
                verify(getBooksByAuthorUsecase).execute(any())
            }

            @Test
            @DisplayName("ページネーションパラメータがデフォルト値で動作すること")
            fun getBooks_withDefaultPagination() {
                // given
                val authorId = 1L
                val responseDto = GetBooksByAuthorResponseDto(
                    items = emptyList(),
                    pagination = PaginationDto(
                        pageNumber = 1,
                        pageSize = 20,
                        total = 0L,
                        totalPages = 0
                    )
                )
                
                given(getBooksByAuthorUsecase.execute(any())).willReturn(responseDto)

                // when & then
                mockMvc.perform(
                    get("/api/v1/books")
                        .param("authorId", authorId.toString())
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.items").isEmpty)
                    .andExpect(jsonPath("$.pagination.pageNumber").value(1))
                    .andExpect(jsonPath("$.pagination.pageSize").value(20))
            }
        }

        @Nested
        @DisplayName("異常系")
        inner class Abnormal {
            @Test
            @DisplayName("authorIdパラメータが欠如している場合、400 Bad Requestが返されること")
            fun getBooks_missingAuthorId() {
                // when & then
                mockMvc.perform(
                    get("/api/v1/books")
                )
                    .andExpect(status().isBadRequest)
            }
        }
    }

    @Nested
    @DisplayName("POST /api/v1/books")
    inner class CreateBook {
        @Nested
        @DisplayName("正常系")
        inner class Normal {
            @Test
            @DisplayName("有効なリクエストで201 CreatedとIDが返されること")
            fun createBook_success() {
                // given
                val request = CreateBookRequest(
                    title = "プログラミング入門",
                    price = 1980,
                    authorIds = listOf(1L),
                    published = true
                )
                val expectedBookId = BookId.reconstruct(1L)
                
                given(createBookUsecase.execute(any())).willReturn(expectedBookId)

                // when & then
                mockMvc.perform(
                    post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isCreated)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1L))
                
                verify(createBookUsecase).execute(any())
            }
        }

        @Nested
        @DisplayName("異常系")
        inner class Abnormal {
            @Test
            @DisplayName("バリデーションエラーの場合、400 Bad Requestが返されること")
            fun createBook_validationError() {
                // given
                val request = CreateBookRequest(
                    title = null,
                    price = 1980,
                    authorIds = listOf(1L),
                    published = true
                )
                val errors = mapOf("title" to "入力してください")
                
                given(createBookUsecase.execute(any()))
                    .willThrow(DomainValidationException(errors))

                // when & then
                mockMvc.perform(
                    post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.details.title").value("入力してください"))
            }
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/books/{id}")
    inner class UpdateBook {
        @Nested
        @DisplayName("正常系")
        inner class Normal {
            @Test
            @DisplayName("有効なリクエストで204 No Contentが返されること")
            fun updateBook_success() {
                // given
                val bookId = 1L
                val request = UpdateBookRequest(
                    title = "プログラミング入門（改訂版）",
                    price = 2200,
                    authorIds = listOf(1L, 2L),
                    published = true
                )

                // when & then
                mockMvc.perform(
                    put("/api/v1/books/$bookId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isNoContent)
                
                verify(updateBookUsecase).execute(eq(bookId), any())
            }
        }

        @Nested
        @DisplayName("異常系")
        inner class Abnormal {
            @Test
            @DisplayName("書籍が存在しない場合、404 Not Foundが返されること")
            fun updateBook_notFound() {
                // given
                val bookId = 999L
                val request = UpdateBookRequest(
                    title = "プログラミング入門",
                    price = 1980,
                    authorIds = listOf(1L),
                    published = true
                )
                
                given(updateBookUsecase.execute(any(), any()))
                    .willThrow(ResourceNotFoundException())

                // when & then
                mockMvc.perform(
                    put("/api/v1/books/$bookId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isNotFound)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
            }

            @Test
            @DisplayName("バリデーションエラーの場合、400 Bad Requestが返されること")
            fun updateBook_validationError() {
                // given
                val bookId = 1L
                val request = UpdateBookRequest(
                    title = null,
                    price = 1980,
                    authorIds = listOf(1L),
                    published = true
                )
                val errors = mapOf("title" to "入力してください")
                
                given(updateBookUsecase.execute(any(), any()))
                    .willThrow(DomainValidationException(errors))

                // when & then
                mockMvc.perform(
                    put("/api/v1/books/$bookId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.details.title").value("入力してください"))
            }

            @Test
            @DisplayName("ビジネスルール違反の場合、400 Bad Requestが返されること")
            fun updateBook_businessRuleViolation() {
                // given
                val bookId = 1L
                val request = UpdateBookRequest(
                    title = "プログラミング入門",
                    price = 1980,
                    authorIds = listOf(1L),
                    published = false
                )
                
                given(updateBookUsecase.execute(any(), any()))
                    .willThrow(BusinessRuleViolationException("出版済みの書籍を未出版に変更することはできません"))

                // when & then
                mockMvc.perform(
                    put("/api/v1/books/$bookId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("BUSINESS_RULE_VIOLATION"))
                    .andExpect(jsonPath("$.message").value("出版済みの書籍を未出版に変更することはできません"))
            }
        }
    }
}

