package com.example.bookmanagementapi.usecase.author

import com.example.bookmanagementapi.domain.author.Author
import com.example.bookmanagementapi.domain.author.AuthorId
import com.example.bookmanagementapi.domain.author.AuthorName
import com.example.bookmanagementapi.domain.author.AuthorRepository
import com.example.bookmanagementapi.domain.author.BirthDate
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
@DisplayName("UpdateAuthorUsecase テスト")
class UpdateAuthorUsecaseTest {

    @Mock
    private lateinit var authorRepository: AuthorRepository

    @InjectMocks
    private lateinit var usecase: UpdateAuthorUsecase

    @Nested
    @DisplayName("正常系")
    inner class Normal {
        @Test
        @DisplayName("有効なリクエストで著者が更新されること")
        fun updateAuthor_success() {
            // given
            val authorId = 1L
            val request = UpdateAuthorRequestDto(
                name = "山田太郎",
                birthDate = "1980-05-15"
            )
            val existingAuthor = Author.reconstruct(
                id = AuthorId.reconstruct(authorId),
                name = AuthorName.reconstruct("旧名前"),
                birthDate = BirthDate.reconstruct(LocalDate.of(1970, 1, 1))
            )
            
            given(authorRepository.findById(any())).willReturn(existingAuthor)

            // when & then
            assertDoesNotThrow {
                usecase.execute(authorId, request)
            }
            verify(authorRepository).findById(any())
            verify(authorRepository).update(any())
        }
    }

    @Nested
    @DisplayName("異常系")
    inner class Abnormal {
        @ParameterizedTest
        @ValueSource(longs = [0, -1])
        @DisplayName("IDが0以下の場合、DomainValidationExceptionがスローされること")
        fun updateAuthor_idIsInvalid(authorId: Long) {
            // given
            val request = UpdateAuthorRequestDto(
                name = "山田太郎",
                birthDate = "1980-05-15"
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(authorId, request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("1以上である必要があります", exception.errors["id"] as String)
        }

        @Test
        @DisplayName("名前がnullの場合、DomainValidationExceptionがスローされること")
        fun updateAuthor_nameIsNull() {
            // given
            val authorId = 1L
            val request = UpdateAuthorRequestDto(
                name = null,
                birthDate = "1980-05-15"
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(authorId, request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("入力してください", exception.errors["name"] as String)
        }

        @Test
        @DisplayName("生年月日が未来の場合、DomainValidationExceptionがスローされること")
        fun updateAuthor_birthDateIsFuture() {
            // given
            val authorId = 1L
            val request = UpdateAuthorRequestDto(
                name = "山田太郎",
                birthDate = LocalDate.now().plusDays(1).toString()
            )

            // when & then
            val exception = assertThrows(DomainValidationException::class.java) {
                usecase.execute(authorId, request)
            }
            assertEquals(1, exception.errors.size)
            assertEquals("現在日より過去日である必要があります", exception.errors["birth_date"] as String)
        }

        @Test
        @DisplayName("著者が存在しない場合、ResourceNotFoundExceptionがスローされること")
        fun updateAuthor_authorNotFound() {
            // given
            val authorId = 1L
            val request = UpdateAuthorRequestDto(
                name = "山田太郎",
                birthDate = "1980-05-15"
            )
            
            given(authorRepository.findById(any())).willReturn(null)

            // when & then
            assertThrows(ResourceNotFoundException::class.java) {
                usecase.execute(authorId, request)
            }
            verify(authorRepository).findById(any())
        }
    }
}

