package com.example.bookmanagementapi.domain.pagination

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

@DisplayName("PageNumber テスト")
class PageNumberTest {

    @Nested
    @DisplayName("create")
    inner class Create {
        @Nested
        @DisplayName("正常系")
        inner class Normal {
            @ParameterizedTest
            @ValueSource(ints = [1, 10, 100])
            @DisplayName("1以上の値の場合、Successを返すこと")
            fun success(value: Int) {
                val result = PageNumber.create(value)
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
                val result = PageNumber.create(null)
                assertTrue(result is ValidationResult.Failure)
                assertEquals(ValidationMessages.REQUIRED, (result as ValidationResult.Failure).errors[0].message)
            }

            @ParameterizedTest
            @ValueSource(ints = [0, -1])
            @DisplayName("1未満の場合、Failure(PAGE_NUMBER_MUST_BE_AT_LEAST_ONE)を返すこと")
            fun lessThanOne(value: Int) {
                val result = PageNumber.create(value)
                assertTrue(result is ValidationResult.Failure)
                assertEquals(ValidationMessages.MUST_BE_POSITIVE, (result as ValidationResult.Failure).errors[0].message)
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
                val value = 1
                val pageNumber = PageNumber.reconstruct(value)
                assertEquals(value, pageNumber.value)
            }
        }
    }
}

