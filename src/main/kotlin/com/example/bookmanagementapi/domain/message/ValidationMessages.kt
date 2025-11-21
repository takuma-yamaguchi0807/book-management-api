package com.example.bookmanagementapi.domain.message

import java.util.ResourceBundle

/**
 * バリデーションメッセージ
 * ResourceBundleを使用してプロパティファイルからメッセージを読み込む
 */
object ValidationMessages {
    private val bundle = ResourceBundle.getBundle("messages")
    
    val REQUIRED: String
        get() = bundle.getString("validation.required")
    
    val INVALID_DATE_FORMAT: String
        get() = bundle.getString("validation.invalid.date.format")
    
    val BIRTH_DATE_MUST_BE_PAST: String
        get() = bundle.getString("validation.birth.date.must.be.past")
    
    val AUTHOR_ID_MUST_BE_POSITIVE: String
        get() = bundle.getString("validation.author.id.must.be.positive")
    
    val PRICE_MUST_BE_NON_NEGATIVE: String
        get() = bundle.getString("validation.price.must.be.non.negative")
    
    val AUTHOR_IDS_MUST_HAVE_AT_LEAST_ONE: String
        get() = bundle.getString("validation.author.ids.must.have.at.least.one")
    
    val AUTHOR_IDS_DUPLICATE: String
        get() = bundle.getString("validation.author.ids.duplicate")
}

