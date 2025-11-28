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

@DisplayName("PublishedStatus テスト")
class PublishedStatusTest {

    @Nested
    @DisplayName("create")
    inner class Create {
        @Nested
        @DisplayName("正常系")
        inner class Normal {
            @ParameterizedTest(name = "値が {0} の場合、Successを返すこと")
            @ValueSource(booleans = [true, false])
            fun validValue(value: Boolean) {
                val result = PublishedStatus.create(value)
                assertTrue(result is ValidationResult.Success)
                assertEquals(value, (result as ValidationResult.Success).value.value)
            }
        }

        @Nested
        @DisplayName("異常系")
        inner class Abnormal {
            @Test
            @DisplayName("値がnullの場合、Failure(REQUIRED)を返すこと")
            fun nullValue() {
                val result = PublishedStatus.create(null)
                assertTrue(result is ValidationResult.Failure)
                assertEquals(ValidationMessages.REQUIRED, (result as ValidationResult.Failure).error.message)
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
                val status = PublishedStatus.reconstruct(false)
                assertEquals(false, status.value)
            }
        }

        @Nested
        @DisplayName("異常系")
        inner class Abnormal {
            @Test
            @DisplayName("nullの場合、IllegalArgumentExceptionが発生すること")
            fun nullValue() {
                val exception = assertThrows<IllegalArgumentException> {
                    PublishedStatus.reconstruct(null)
                }
                assertEquals("PublishedStatus must not be null", exception.message)
            }
        }
    }
}
