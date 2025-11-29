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
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("Title テスト")
class TitleTest {

    @Nested
    @DisplayName("create")
    inner class Create {
        @Nested
        @DisplayName("正常系")
        inner class Normal {
            @Test
            @DisplayName("有効なタイトルの場合、Successを返すこと")
            fun validValue() {
                val result = Title.create("Domain Driven Design")
                assertTrue(result is ValidationResult.Success)
                assertEquals("Domain Driven Design", (result as ValidationResult.Success).value.value)
            }
        }

        @Nested
        @DisplayName("異常系")
        inner class Abnormal {
            @ParameterizedTest(name = "値が \"{0}\" の場合、Failure(REQUIRED)を返すこと")
            @NullSource
            @ValueSource(strings = ["", "  "])
            fun invalidValue(value: String?) {
                val result = Title.create(value)
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
                val title = Title.reconstruct("Domain Driven Design")
                assertEquals("Domain Driven Design", title.value)
            }
        }

        @Nested
        @DisplayName("異常系")
        inner class Abnormal {
            @Test
            @DisplayName("nullの場合、IllegalArgumentExceptionが発生すること")
            fun nullValue() {
                val exception = assertThrows<IllegalArgumentException> {
                    Title.reconstruct(null)
                }
                assertEquals("Title must not be null", exception.message)
            }
        }
    }
}
