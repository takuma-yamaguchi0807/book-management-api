package com.example.bookmanagementapi.domain.author

import com.example.bookmanagementapi.domain.ValidationResult
import com.example.bookmanagementapi.domain.message.ValidationMessages
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("AuthorName テスト")
class AuthorNameTest {

    @Nested
    @DisplayName("create")
    inner class Create {
        @Nested
        @DisplayName("正常系")
        inner class Normal {
            @Test
            @DisplayName("有効な名前の場合、Successを返すこと")
            fun validValue() {
                val result = AuthorName.create("John Doe")
                assertTrue(result is ValidationResult.Success)
                assertEquals("John Doe", (result as ValidationResult.Success).value.value)
            }
        }

        @Nested
        @DisplayName("異常系")
        inner class Abnormal {
            @ParameterizedTest(name = "値が \"{0}\" の場合、Failure(REQUIRED)を返すこと")
            @NullSource
            @ValueSource(strings = ["", "  "])
            fun invalidValue(value: String?) {
                val result = AuthorName.create(value)
                assertTrue(result is ValidationResult.Failure)
                assertEquals(ValidationMessages.REQUIRED, (result as ValidationResult.Failure).errors[0].message)
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
                val authorName = AuthorName.reconstruct("John Doe")
                assertEquals("John Doe", authorName.value)
            }
        }

        @Nested
        @DisplayName("異常系")
        inner class Abnormal {
            @Test
            @DisplayName("nullの場合、IllegalArgumentExceptionが発生すること")
            fun nullValue() {
                val exception = assertThrows<IllegalArgumentException> {
                    AuthorName.reconstruct(null)
                }
                assertEquals("AuthorName must not be null", exception.message)
            }
        }
    }
}
