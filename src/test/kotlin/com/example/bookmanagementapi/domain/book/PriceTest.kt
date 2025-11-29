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

@DisplayName("Price テスト")
class PriceTest {

    @Nested
    @DisplayName("create")
    inner class Create {
        @Nested
        @DisplayName("正常系")
        inner class Normal {
            @ParameterizedTest(name = "値が {0} の場合、Successを返すこと")
            @ValueSource(ints = [0, 1000])
            fun validValue(value: Int) {
                val result = Price.create(value)
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
                val result = Price.create(null)
                assertTrue(result is ValidationResult.Failure)
                assertEquals(ValidationMessages.REQUIRED, (result as ValidationResult.Failure).errors[0].message)
            }

            @Test
            @DisplayName("値が負数(-1)の場合、Failure(PRICE_MUST_BE_NON_NEGATIVE)を返すこと")
            fun negativeValue() {
                val result = Price.create(-1)
                assertTrue(result is ValidationResult.Failure)
                assertEquals(ValidationMessages.MUST_BE_NON_NEGATIVE, (result as ValidationResult.Failure).errors[0].message)
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
                val price = Price.reconstruct(-100) // Can reconstruct invalid value
                assertEquals(-100, price.value)
            }
        }

        @Nested
        @DisplayName("異常系")
        inner class Abnormal {
            @Test
            @DisplayName("nullの場合、IllegalArgumentExceptionが発生すること")
            fun nullValue() {
                val exception = assertThrows<IllegalArgumentException> {
                    Price.reconstruct(null)
                }
                assertEquals("Price must not be null", exception.message)
            }
        }
    }
}
