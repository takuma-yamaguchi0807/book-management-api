package com.example.bookmanagementapi.presentation.book

/**
 * 書籍リソースのフィールド名定数とエンドポイント定数
 */
object BookFields {
    // エンドポイントパス
    const val PATH_BASE = "/api/v1/books"
    const val PATH_BY_ID = "/api/v1/books/{id}"
    const val PATH_ID_SEGMENT = "/{id}"
    
    // JSONフィールド名
    const val ID = "id"
    const val TITLE = "title"
    const val PRICE = "price"
    const val AUTHOR_ID = "author_id"
    const val AUTHOR_IDS = "author_ids"
    const val PUBLISHED = "published"
}

