package com.example.bookmanagementapi.domain.book

import com.example.bookmanagementapi.domain.ValidationResult
import com.example.bookmanagementapi.domain.message.ValidationMessages
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("BookId テスト")
class BookIdTest {

    @Nested
    @DisplayName("create")
    inner class Create {
        @Nested
        @DisplayName("正常系")
        inner class Normal {
            @ParameterizedTest
            @ValueSource(longs = [1, 100])
            @DisplayName("正の数の場合、Successを返すこと")
            fun success(value: Long) {
                val result = BookId.create(value)
                assertTrue(result is ValidationResult.Success)
                assertEquals(value, (result as ValidationResult.Success).value.value)
            }
        }

        @Nested
        @DisplayName("異常系")
        inner class Abnormal {
            @Test
            @DisplayName("nullの場合、Failure(REQUIRED)を返すこと")
            fun nullValue() {
                val result = BookId.create(null)
                assertTrue(result is ValidationResult.Failure)
                assertEquals(ValidationMessages.REQUIRED, (result as ValidationResult.Failure).error.message)
            }

            @ParameterizedTest
            @ValueSource(longs = [0, -1])
            @DisplayName("0以下の場合、Failure(AUTHOR_ID_MUST_BE_POSITIVE)を返すこと")
            fun notPositive(value: Long) {
                val result = BookId.create(value)
                assertTrue(result is ValidationResult.Failure)
                assertEquals(ValidationMessages.MUST_BE_POSITIVE, (result as ValidationResult.Failure).error.message)
            }
        }
    }

    @Nested
    @DisplayName("reconstruct")
    inner class Reconstruct {
        @Nested
        @DisplayName("正常系")
        inner class Normal {
            @Test
            @DisplayName("値が存在する場合、インスタンスが生成されること")
            fun success() {
                val value = 1L
                val id = BookId.reconstruct(value)
                assertEquals(value, id.value)
            }
        }

        @Nested
        @DisplayName("異常系")
        inner class Abnormal {
            @Test
            @DisplayName("nullの場合、IllegalArgumentExceptionが発生すること")
            fun nullValue() {
                val exception = assertThrows<IllegalArgumentException> {
                    BookId.reconstruct(null)
                }
                assertEquals("BookId must not be null", exception.message)
            }
        }
    }
}
