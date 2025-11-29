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
                        .param("author_id", authorId.toString())
                        .param("page_number", "1")
                        .param("page_size", "20")
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
                    .andExpect(jsonPath("$.pagination.page_number").value(1))
                    .andExpect(jsonPath("$.pagination.page_size").value(20))
                    .andExpect(jsonPath("$.pagination.total").value(1L))
                    .andExpect(jsonPath("$.pagination.total_pages").value(1))
                
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
                        .param("author_id", authorId.toString())
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.items").isEmpty)
                    .andExpect(jsonPath("$.pagination.page_number").value(1))
                    .andExpect(jsonPath("$.pagination.page_size").value(20))
            }
        }

        @Nested
        @DisplayName("異常系")
        inner class Abnormal {
            @Test
            @DisplayName("authorIdパラメータが欠如している場合、400 Bad RequestとValidationErrorResponseが返されること")
            fun getBooks_missingAuthorId() {
                // when & then
                mockMvc.perform(
                    get("/api/v1/books")
                )
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.details.author_id").value("入力してください"))
            }

            @Test
            @DisplayName("ページ番号が0以下の場合、400 Bad Requestが返されること")
            fun getBooks_invalidPageNumber() {
                // given
                val authorId = 1L
                val errors = mapOf("page_number" to "1以上である必要があります")
                
                given(getBooksByAuthorUsecase.execute(any()))
                    .willThrow(DomainValidationException(errors))

                // when & then
                mockMvc.perform(
                    get("/api/v1/books")
                        .param("author_id", authorId.toString())
                        .param("page_number", "0")
                )
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.details.page_number").value("1以上である必要があります"))
            }

            @Test
            @DisplayName("ページサイズが範囲外の場合、400 Bad Requestが返されること")
            fun getBooks_invalidPageSize() {
                // given
                val authorId = 1L
                val errors = mapOf("page_size" to "1以上100未満である必要があります")
                
                given(getBooksByAuthorUsecase.execute(any()))
                    .willThrow(DomainValidationException(errors))

                // when & then
                mockMvc.perform(
                    get("/api/v1/books")
                        .param("author_id", authorId.toString())
                        .param("page_size", "100")
                )
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.details.page_size").value("1以上100未満である必要があります"))
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
            @DisplayName("必須項目が欠如している場合、400 Bad Requestが返されること")
            fun createBook_missingRequiredFields() {
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

            @Test
            @DisplayName("価格が0未満の場合、400 Bad Requestが返されること")
            fun createBook_negativePrice() {
                // given
                val request = CreateBookRequest(
                    title = "プログラミング入門",
                    price = -1,
                    authorIds = listOf(1L),
                    published = true
                )
                val errors = mapOf("price" to "0以上である必要があります")
                
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
                    .andExpect(jsonPath("$.details.price").value("0以上である必要があります"))
            }

            @Test
            @DisplayName("著者IDが空配列の場合、400 Bad Requestが返されること")
            fun createBook_emptyAuthorIds() {
                // given
                val request = CreateBookRequest(
                    title = "プログラミング入門",
                    price = 1980,
                    authorIds = emptyList(),
                    published = true
                )
                val errors = mapOf("author_ids" to "1件以上必要です")
                
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
                    .andExpect(jsonPath("$.details.author_ids").value("1件以上必要です"))
            }

            @Test
            @DisplayName("リクエストボディが不正な場合、400 Bad Requestが返されること")
            fun createBook_invalidRequestBody() {
                // when & then
                mockMvc.perform(
                    post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }")
                )
                    .andExpect(status().isBadRequest)
            }

            @Test
            @DisplayName("著者IDが存在しない場合、422 Unprocessable Entityが返されること")
            fun createBook_authorNotFound() {
                // given
                val request = CreateBookRequest(
                    title = "プログラミング入門",
                    price = 1980,
                    authorIds = listOf(999L),
                    published = true
                )
                val errors = mapOf<String, Any>("author_ids" to mapOf("0" to "存在しません"))
                
                given(createBookUsecase.execute(any()))
                    .willThrow(BusinessRuleViolationException(errors))

                // when & then
                mockMvc.perform(
                    post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isUnprocessableEntity)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("BUSINESS_RULE_VIOLATION"))
                    .andExpect(jsonPath("$.details.author_ids['0']").value("存在しません"))
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
            @DisplayName("必須項目が欠如している場合、400 Bad Requestが返されること")
            fun updateBook_missingRequiredFields() {
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
            @DisplayName("価格が0未満の場合、400 Bad Requestが返されること")
            fun updateBook_negativePrice() {
                // given
                val bookId = 1L
                val request = UpdateBookRequest(
                    title = "プログラミング入門",
                    price = -1,
                    authorIds = listOf(1L),
                    published = true
                )
                val errors = mapOf("price" to "0以上である必要があります")
                
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
                    .andExpect(jsonPath("$.details.price").value("0以上である必要があります"))
            }

            @Test
            @DisplayName("著者IDが空配列の場合、400 Bad Requestが返されること")
            fun updateBook_emptyAuthorIds() {
                // given
                val bookId = 1L
                val request = UpdateBookRequest(
                    title = "プログラミング入門",
                    price = 1980,
                    authorIds = emptyList(),
                    published = true
                )
                val errors = mapOf("author_ids" to "1件以上必要です")
                
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
                    .andExpect(jsonPath("$.details.author_ids").value("1件以上必要です"))
            }

            @Test
            @DisplayName("リクエストボディが不正な場合、400 Bad Requestが返されること")
            fun updateBook_invalidRequestBody() {
                // given
                val bookId = 1L

                // when & then
                mockMvc.perform(
                    put("/api/v1/books/$bookId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }")
                )
                    .andExpect(status().isBadRequest)
            }

            @Test
            @DisplayName("ビジネスルール違反（出版済み→未出版）の場合、422 Unprocessable Entityが返されること")
            fun updateBook_businessRuleViolationPublishedToUnpublished() {
                // given
                val bookId = 1L
                val request = UpdateBookRequest(
                    title = "プログラミング入門",
                    price = 1980,
                    authorIds = listOf(1L),
                    published = false
                )
                
                given(updateBookUsecase.execute(any(), any()))
                    .willThrow(BusinessRuleViolationException(
                        mapOf<String, Any>("published" to "出版済みの書籍を未出版に変更することはできません")
                    ))

                // when & then
                mockMvc.perform(
                    put("/api/v1/books/$bookId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isUnprocessableEntity)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("BUSINESS_RULE_VIOLATION"))
                    .andExpect(jsonPath("$.details.published").value("出版済みの書籍を未出版に変更することはできません"))
            }

            @Test
            @DisplayName("著者IDが存在しない場合、422 Unprocessable Entityが返されること")
            fun updateBook_authorNotFound() {
                // given
                val bookId = 1L
                val request = UpdateBookRequest(
                    title = "プログラミング入門",
                    price = 1980,
                    authorIds = listOf(999L),
                    published = true
                )
                val errors = mapOf<String, Any>("author_ids" to mapOf("0" to "存在しません"))
                
                given(updateBookUsecase.execute(any(), any()))
                    .willThrow(BusinessRuleViolationException(errors))

                // when & then
                mockMvc.perform(
                    put("/api/v1/books/$bookId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isUnprocessableEntity)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("BUSINESS_RULE_VIOLATION"))
                    .andExpect(jsonPath("$.details.author_ids['0']").value("存在しません"))
            }

            @Test
            @DisplayName("パスパラメータの型が不正な場合、400 Bad Requestが返されること")
            fun updateBook_invalidPathParameter() {
                // when & then
                mockMvc.perform(
                    put("/api/v1/books/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                )
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
            }
        }
    }
}

