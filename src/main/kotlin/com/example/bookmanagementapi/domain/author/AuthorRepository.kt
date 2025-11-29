package com.example.bookmanagementapi.domain.author

/**
 * 著者リポジトリインターフェース
 */
interface AuthorRepository {
    /**
     * 著者を保存する
     * @return 保存された著者のID
     */
    fun save(author: Author): AuthorId

    /**
     * IDで著者を取得する
     * @return 見つかった著者、存在しない場合はnull
     */
    fun findById(id: AuthorId): Author?

    /**
     * 複数のIDで著者を一括取得する
     * @param ids 取得する著者IDのリスト
     * @return 見つかった著者のリスト（IDの順序は保証されない）
     */
    fun findByIds(ids: List<AuthorId>): List<Author>

    /**
     * 著者を更新する
     */
    fun update(author: Author)
}

