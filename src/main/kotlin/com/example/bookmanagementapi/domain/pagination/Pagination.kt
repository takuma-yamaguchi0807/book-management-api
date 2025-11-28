package com.example.bookmanagementapi.domain.pagination

/**
 * ページネーションの集約ルート
 */
data class Pagination(
    val pageNumber: PageNumber,
    val pageSize: PageSize
) {
    companion object {
        /**
         * 値オブジェクトから作成
         */
        fun create(pageNumber: PageNumber, pageSize: PageSize): Pagination {
            return Pagination(pageNumber, pageSize)
        }

        /**
         * 再構築用
         */
        fun reconstruct(pageNumber: PageNumber, pageSize: PageSize): Pagination {
            return Pagination(pageNumber, pageSize)
        }
    }

    /**
     * オフセットを計算
     */
    fun calculateOffset(): Int {
        return (pageNumber.value - 1) * pageSize.value
    }

    /**
     * 総ページ数を計算
     */
    fun calculateTotalPages(total: Long): Int {
        if (total == 0L) {
            return 0
        }
        return ((total - 1) / pageSize.value).toInt() + 1
    }
}

