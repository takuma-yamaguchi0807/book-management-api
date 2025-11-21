package com.example.bookmanagementapi.domain.book

/**
 * 書籍リポジトリインターフェース
 */
interface BookRepository {
    /**
     * 書籍を保存する
     * @return 保存された書籍のID
     */
    fun save(book: Book): BookId

    /**
     * IDで書籍を取得する
     * @return 見つかった書籍、存在しない場合はnull
     */
    fun findById(id: BookId): Book?

    /**
     * 書籍を更新する
     */
    fun update(book: Book)
}

