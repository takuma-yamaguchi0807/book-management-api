package com.example.bookmanagementapi.domain.pagination

import com.example.bookmanagementapi.domain.ValidationResult
import com.example.bookmanagementapi.domain.message.ValidationMessages
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("PageSize テスト")
class PageSizeTest {

    @Nested
    @DisplayName("create")
    inner class Create {
        @Nested
        @DisplayName("正常系")
        inner class Normal {
            @ParameterizedTest
            @ValueSource(ints = [1, 20, 99])
            @DisplayName("1以上99未満の値の場合、Successを返すこと")
            fun success(value: Int) {
                val result = PageSize.create(value)
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
                val result = PageSize.create(null)
                assertTrue(result is ValidationResult.Failure)
                assertEquals(ValidationMessages.REQUIRED, (result as ValidationResult.Failure).error.message)
            }

            @ParameterizedTest
            @ValueSource(ints = [0, -1])
            @DisplayName("1未満の場合、Failure(PAGE_SIZE_MUST_BE_BETWEEN_ONE_AND_NINETY_NINE)を返すこと")
            fun lessThanOne(value: Int) {
                val result = PageSize.create(value)
                assertTrue(result is ValidationResult.Failure)
                assertEquals(ValidationMessages.PAGE_SIZE_MUST_BE_BETWEEN_ONE_AND_NINETY_NINE, (result as ValidationResult.Failure).error.message)
            }

            @ParameterizedTest
            @ValueSource(ints = [100, 101])
            @DisplayName("100以上の場合、Failure(PAGE_SIZE_MUST_BE_BETWEEN_ONE_AND_NINETY_NINE)を返すこと")
            fun greaterThanOrEqualTo100(value: Int) {
                val result = PageSize.create(value)
                assertTrue(result is ValidationResult.Failure)
                assertEquals(ValidationMessages.PAGE_SIZE_MUST_BE_BETWEEN_ONE_AND_NINETY_NINE, (result as ValidationResult.Failure).error.message)
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
                val value = 20
                val pageSize = PageSize.reconstruct(value)
                assertEquals(value, pageSize.value)
            }
        }
    }
}

