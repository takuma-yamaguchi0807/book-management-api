package com.example.bookmanagementapi.usecase.author

import com.example.bookmanagementapi.domain.author.AuthorId
import com.example.bookmanagementapi.domain.author.AuthorName
import com.example.bookmanagementapi.domain.author.AuthorRepository
import com.example.bookmanagementapi.domain.author.BirthDate
import com.example.bookmanagementapi.domain.exception.DomainValidationException
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
import com.example.bookmanagementapi.domain.author.Author
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
@DisplayName("CreateAuthorUsecase テスト")
class CreateAuthorUsecaseTest {

    @Mock
    private lateinit var authorRepository: AuthorRepository

    @InjectMocks
    private lateinit var usecase: CreateAuthorUsecase

    @Nested
    @DisplayName("正常系")
    inner class Normal {
        @Test
        @DisplayName("有効なリクエストで著者が作成されること")
        fun createAuthor_success() {
            // given
            val request = CreateAuthorRequestDto(
                name = "山田太郎",
                birthDate = "1980-05-15"
            )
            val expectedAuthorId = AuthorId.reconstruct(1L)
            
            given(authorRepository.save(any())).willReturn(expectedAuthorId)

            // when
            val result = usecase.execute(request)

            // then
            assertEquals(expectedAuthorId, result)
            verify(authorRepository).save(any())
        }
    }

    @Nested
    @DisplayName("異常系")
    inner class Abnormal {
        @ParameterizedTest
        @ValueSource(strings = ["", "   "])
        @DisplayName("名前が空文字または空白文字のみの場合、DomainValidationExceptionがスローされること")
        fun createAuthor_nameIsInvalid(name: String) {
            // given
            val request = CreateAuthorRequestDto(
                name = name,
                birthDate = "1980-05-15"
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("入力してください", exception.errors["name"] as String)
        }

        @Test
        @DisplayName("名前がnullの場合、DomainValidationExceptionがスローされること")
        fun createAuthor_nameIsNull() {
            // given
            val request = CreateAuthorRequestDto(
                name = null,
                birthDate = "1980-05-15"
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("入力してください", exception.errors["name"] as String)
        }

        @Test
        @DisplayName("生年月日がnullの場合、DomainValidationExceptionがスローされること")
        fun createAuthor_birthDateIsNull() {
            // given
            val request = CreateAuthorRequestDto(
                name = "山田太郎",
                birthDate = null
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("入力してください", exception.errors["birth_date"])
        }

        @Test
        @DisplayName("生年月日が未来の場合、DomainValidationExceptionがスローされること")
        fun createAuthor_birthDateIsFuture() {
            // given
            val request = CreateAuthorRequestDto(
                name = "山田太郎",
                birthDate = LocalDate.now().plusDays(1).toString()
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("現在より過去である必要があります", exception.errors["birth_date"] as String)
        }

        @Test
        @DisplayName("名前と生年月日が両方nullの場合、両方のエラーが返されること")
        fun createAuthor_bothFieldsAreNull() {
            // given
            val request = CreateAuthorRequestDto(
                name = null,
                birthDate = null
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(request)
            }
            assertEquals(2, exception.errors.size)
            assertEquals("入力してください", exception.errors["name"] as String)
            assertEquals("入力してください", exception.errors["birth_date"])
        }
    }
}