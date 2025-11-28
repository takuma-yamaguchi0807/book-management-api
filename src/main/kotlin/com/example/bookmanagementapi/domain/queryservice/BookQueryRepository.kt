package com.example.bookmanagementapi.domain.queryservice

import com.example.bookmanagementapi.domain.author.AuthorId
import com.example.bookmanagementapi.domain.pagination.Pagination

/**
 * 書籍クエリリポジトリインターフェース
 * QueryService用の読み取り専用リポジトリ
 */
interface BookQueryRepository {
    /**
     * 指定された著者IDに紐づく書籍の総件数を取得
     * 
     * @param authorId 著者ID
     * @return 総件数
     */
    fun countByAuthorId(authorId: AuthorId): Long

    /**
     * 指定された著者IDに紐づく書籍一覧を取得（ページネーション適用）
     * 
     * @param authorId 著者ID
     * @param pagination ページネーション情報
     * @return 書籍一覧
     */
    fun findByAuthorId(
        authorId: AuthorId,
        pagination: Pagination
    ): BookQueryResultDto
}

