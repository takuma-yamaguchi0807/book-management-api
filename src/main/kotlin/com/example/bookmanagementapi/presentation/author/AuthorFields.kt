package com.example.bookmanagementapi.presentation.author

/**
 * 著者リソースのフィールド名定数とエンドポイント定数
 */
object AuthorFields {
    // エンドポイントパス
    const val PATH_BASE = "/api/v1/authors"
    const val PATH_BY_ID = "/api/v1/authors/{id}"
    const val PATH_ID_SEGMENT = "/{id}"
    
    // JSONフィールド名
    const val ID = "id"
    const val NAME = "name"
    const val BIRTH_DATE = "birth_date"
}

