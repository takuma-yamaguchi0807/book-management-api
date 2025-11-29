package com.example.bookmanagementapi.presentation.author

import com.example.bookmanagementapi.domain.author.AuthorId
import com.example.bookmanagementapi.domain.exception.DomainValidationException
import com.example.bookmanagementapi.domain.exception.ResourceNotFoundException
import com.example.bookmanagementapi.usecase.author.CreateAuthorUsecase
import com.example.bookmanagementapi.usecase.author.UpdateAuthorUsecase
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.mockito.BDDMockito.given
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.Mockito.verify

@WebMvcTest(AuthorController::class)
@DisplayName("AuthorController テスト")
class AuthorControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var createAuthorUsecase: CreateAuthorUsecase

    @MockitoBean
    private lateinit var updateAuthorUsecase: UpdateAuthorUsecase

    @Nested
    @DisplayName("POST /api/v1/authors")
    inner class CreateAuthor {
        @Nested
        @DisplayName("正常系")
        inner class Normal {
            @Test
            @DisplayName("有効なリクエストで201 CreatedとIDが返されること")
            fun createAuthor_success() {
                // given
                val request = CreateAuthorRequest(
                    name = "山田太郎",
                    birthDate = "1980-05-15"
                )
                val expectedAuthorId = AuthorId.reconstruct(1L)
                
                given(createAuthorUsecase.execute(any())).willReturn(expectedAuthorId)

                // when & then
                mockMvc.perform(
                    post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isCreated)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1L))
                
                verify(createAuthorUsecase).execute(any())
            }
        }

        @Nested
        @DisplayName("異常系")
        inner class Abnormal {
            @Test
            @DisplayName("必須項目が欠如している場合、400 Bad Requestが返されること")
            fun createAuthor_missingRequiredFields() {
                // given
                val request = CreateAuthorRequest(
                    name = null,
                    birthDate = "1980-05-15"
                )
                val errors = mapOf("name" to "入力してください")
                
                given(createAuthorUsecase.execute(any()))
                    .willThrow(DomainValidationException(errors))

                // when & then
                mockMvc.perform(
                    post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.details.name").value("入力してください"))
            }

            @Test
            @DisplayName("生年月日が現在より未来の場合、400 Bad Requestが返されること")
            fun createAuthor_futureBirthDate() {
                // given
                val request = CreateAuthorRequest(
                    name = "山田太郎",
                    birthDate = "2100-01-01"
                )
                val errors = mapOf("birth_date" to "現在より過去である必要があります")
                
                given(createAuthorUsecase.execute(any()))
                    .willThrow(DomainValidationException(errors))

                // when & then
                mockMvc.perform(
                    post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.details.birth_date").value("現在より過去である必要があります"))
            }

            @Test
            @DisplayName("リクエストボディが不正な場合、400 Bad Requestが返されること")
            fun createAuthor_invalidRequestBody() {
                // when & then
                mockMvc.perform(
                    post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }")
                )
                    .andExpect(status().isBadRequest)
            }
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/authors/{id}")
    inner class UpdateAuthor {
        @Nested
        @DisplayName("正常系")
        inner class Normal {
            @Test
            @DisplayName("有効なリクエストで204 No Contentが返されること")
            fun updateAuthor_success() {
                // given
                val authorId = 1L
                val request = UpdateAuthorRequest(
                    name = "山田太郎",
                    birthDate = "1980-05-15"
                )

                // when & then
                mockMvc.perform(
                    put("/api/v1/authors/$authorId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isNoContent)
                
                verify(updateAuthorUsecase).execute(eq(authorId), any())
            }
        }

        @Nested
        @DisplayName("異常系")
        inner class Abnormal {
            @Test
            @DisplayName("著者が存在しない場合、404 Not Foundが返されること")
            fun updateAuthor_notFound() {
                // given
                val authorId = 999L
                val request = UpdateAuthorRequest(
                    name = "山田太郎",
                    birthDate = "1980-05-15"
                )
                
                given(updateAuthorUsecase.execute(any(), any()))
                    .willThrow(ResourceNotFoundException())

                // when & then
                mockMvc.perform(
                    put("/api/v1/authors/$authorId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isNotFound)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
            }

            @Test
            @DisplayName("必須項目が欠如している場合、400 Bad Requestが返されること")
            fun updateAuthor_missingRequiredFields() {
                // given
                val authorId = 1L
                val request = UpdateAuthorRequest(
                    name = null,
                    birthDate = "1980-05-15"
                )
                val errors = mapOf("name" to "入力してください")
                
                given(updateAuthorUsecase.execute(any(), any()))
                    .willThrow(DomainValidationException(errors))

                // when & then
                mockMvc.perform(
                    put("/api/v1/authors/$authorId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.details.name").value("入力してください"))
            }

            @Test
            @DisplayName("生年月日が現在より未来の場合、400 Bad Requestが返されること")
            fun updateAuthor_futureBirthDate() {
                // given
                val authorId = 1L
                val request = UpdateAuthorRequest(
                    name = "山田太郎",
                    birthDate = "2100-01-01"
                )
                val errors = mapOf("birth_date" to "現在より過去である必要があります")
                
                given(updateAuthorUsecase.execute(any(), any()))
                    .willThrow(DomainValidationException(errors))

                // when & then
                mockMvc.perform(
                    put("/api/v1/authors/$authorId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.details.birth_date").value("現在より過去である必要があります"))
            }

            @Test
            @DisplayName("リクエストボディが不正な場合、400 Bad Requestが返されること")
            fun updateAuthor_invalidRequestBody() {
                // given
                val authorId = 1L

                // when & then
                mockMvc.perform(
                    put("/api/v1/authors/$authorId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }")
                )
                    .andExpect(status().isBadRequest)
            }

            @Test
            @DisplayName("パスパラメータの型が不正な場合、400 Bad Requestが返されること")
            fun updateAuthor_invalidPathParameter() {
                // when & then
                mockMvc.perform(
                    put("/api/v1/authors/abc")
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

