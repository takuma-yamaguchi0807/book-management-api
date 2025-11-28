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
import java.time.LocalDate

@DisplayName("BirthDate テスト")
class BirthDateTest {

    @Nested
    @DisplayName("create")
    inner class Create {
        @Nested
        @DisplayName("正常系")
        inner class Normal {
            @ParameterizedTest(name = "値が {0} の場合、Successを返すこと")
            @ValueSource(strings = ["2000-01-01", "2020-12-31"]) // 過去の日付
            fun validValue(value: String) {
                val result = BirthDate.create(value)
                assertTrue(result is ValidationResult.Success)
                assertEquals(LocalDate.parse(value), (result as ValidationResult.Success).value.value)
            }
        }

        @Nested
        @DisplayName("異常系")
        inner class Abnormal {
            @ParameterizedTest(name = "値が \"{0}\" の場合、Failure(REQUIRED)を返すこと")
            @NullSource
            @ValueSource(strings = ["", "  "])
            fun nullOrEmpty(value: String?) {
                val result = BirthDate.create(value)
                assertTrue(result is ValidationResult.Failure)
                assertEquals(ValidationMessages.REQUIRED, (result as ValidationResult.Failure).error.message)
            }

            @Test
            @DisplayName("不正なフォーマットの場合、Failure(INVALID_DATE_FORMAT)を返すこと")
            fun invalidFormat() {
                val result = BirthDate.create("invalid-date")
                assertTrue(result is ValidationResult.Failure)
                assertEquals(ValidationMessages.INVALID_DATE_FORMAT, (result as ValidationResult.Failure).error.message)
            }

            @Test
            @DisplayName("未来の日付の場合、Failure(BIRTH_DATE_MUST_BE_PAST)を返すこと")
            fun futureDate() {
                val futureDate = LocalDate.now().plusDays(1).toString()
                val result = BirthDate.create(futureDate)
                assertTrue(result is ValidationResult.Failure)
                assertEquals(ValidationMessages.BIRTH_DATE_MUST_BE_PAST, (result as ValidationResult.Failure).error.message)
            }

            @Test
            @DisplayName("現在の日付の場合、Failure(BIRTH_DATE_MUST_BE_PAST)を返すこと")
            fun currentDate() {
                val currentDate = LocalDate.now().toString()
                val result = BirthDate.create(currentDate)
                assertTrue(result is ValidationResult.Failure)
                assertEquals(ValidationMessages.BIRTH_DATE_MUST_BE_PAST, (result as ValidationResult.Failure).error.message)
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
                val date = LocalDate.of(2000, 1, 1)
                val birthDate = BirthDate.reconstruct(date)
                assertEquals(date, birthDate.value)
            }
        }

        @Nested
        @DisplayName("異常系")
        inner class Abnormal {
            @Test
            @DisplayName("nullの場合、IllegalArgumentExceptionが発生すること")
            fun nullValue() {
                val exception = assertThrows<IllegalArgumentException> {
                    BirthDate.reconstruct(null)
                }
                assertEquals("BirthDate must not be null", exception.message)
            }
        }
    }
}
