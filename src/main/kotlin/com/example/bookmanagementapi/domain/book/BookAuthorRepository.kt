package com.example.bookmanagementapi.domain.book

import com.example.bookmanagementapi.domain.author.AuthorId

/**
 * 書籍-著者関連リポジトリインターフェース
 * books_authorsテーブルの操作を担当
 */
interface BookAuthorRepository {
    /**
     * 書籍IDに紐づく著者IDリストを保存する
     * @param bookId 書籍ID
     * @param authorIds 著者IDリスト
     */
    fun save(bookId: BookId, authorIds: List<AuthorId>)

    /**
     * 書籍IDに紐づく著者IDリストを取得する
     * @param bookId 書籍ID
     * @return 著者IDリスト
     */
    fun findByBookId(bookId: BookId): List<AuthorId>

    /**
     * 書籍IDに紐づく著者IDリストを削除する
     * @param bookId 書籍ID
     */
    fun deleteByBookId(bookId: BookId)
}

