package com.example.bookmanagementapi.domain.author

/**
 * 著者リポジトリインターフェース
 */
interface AuthorRepository {
    /**
     * 著者を保存する
     */
    fun save(author: Author): Author
}

