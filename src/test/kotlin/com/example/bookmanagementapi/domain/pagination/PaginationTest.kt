package com.example.bookmanagementapi.domain.pagination

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@DisplayName("Pagination テスト")
class PaginationTest {

    @Nested
    @DisplayName("create")
    inner class Create {
        @Nested
        @DisplayName("正常系")
        inner class Normal {
            @Test
            @DisplayName("PageNumberとPageSizeからインスタンスが生成されること")
            fun success() {
                val pageNumber = PageNumber.reconstruct(1)
                val pageSize = PageSize.reconstruct(20)
                val pagination = Pagination.create(pageNumber, pageSize)
                assertEquals(pageNumber, pagination.pageNumber)
                assertEquals(pageSize, pagination.pageSize)
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
            @DisplayName("PageNumberとPageSizeからインスタンスが生成されること")
            fun success() {
                val pageNumber = PageNumber.reconstruct(1)
                val pageSize = PageSize.reconstruct(20)
                val pagination = Pagination.reconstruct(pageNumber, pageSize)
                assertEquals(pageNumber, pagination.pageNumber)
                assertEquals(pageSize, pagination.pageSize)
            }
        }
    }

    @Nested
    @DisplayName("calculateOffset")
    inner class CalculateOffset {
        @Nested
        @DisplayName("正常系")
        inner class Normal {
            @ParameterizedTest
            @CsvSource(
                "1, 20, 0",
                "2, 20, 20",
                "3, 20, 40",
                "1, 10, 0",
                "5, 10, 40"
            )
            @DisplayName("ページ番号とページサイズから正しいオフセットが計算されること")
            fun success(pageNumber: Int, pageSize: Int, expectedOffset: Int) {
                val pagination = Pagination.create(
                    PageNumber.reconstruct(pageNumber),
                    PageSize.reconstruct(pageSize)
                )
                assertEquals(expectedOffset, pagination.calculateOffset())
            }
        }
    }

    @Nested
    @DisplayName("calculateTotalPages")
    inner class CalculateTotalPages {
        @Nested
        @DisplayName("正常系")
        inner class Normal {
            @Test
            @DisplayName("総件数が0の場合、0を返すこと")
            fun totalIsZero() {
                val pagination = Pagination.create(
                    PageNumber.reconstruct(1),
                    PageSize.reconstruct(20)
                )
                assertEquals(0, pagination.calculateTotalPages(0L))
            }

            @ParameterizedTest
            @CsvSource(
                "1, 20, 1",
                "20, 20, 1",
                "21, 20, 2",
                "40, 20, 2",
                "41, 20, 3",
                "100, 20, 5",
                "1, 10, 1",
                "10, 10, 1",
                "11, 10, 2"
            )
            @DisplayName("総件数とページサイズから正しい総ページ数が計算されること")
            fun success(total: Long, pageSize: Int, expectedTotalPages: Int) {
                val pagination = Pagination.create(
                    PageNumber.reconstruct(1),
                    PageSize.reconstruct(pageSize)
                )
                assertEquals(expectedTotalPages, pagination.calculateTotalPages(total))
            }
        }
    }
}

