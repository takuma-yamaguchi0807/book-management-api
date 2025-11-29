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
    
    // 共通メッセージ
    val MUST_BE_POSITIVE: String
        get() = bundle.getString("validation.must.be.positive")
    
    val MUST_BE_NON_NEGATIVE: String
        get() = bundle.getString("validation.must.be.non.negative")
    
    val MUST_BE_PAST: String
        get() = bundle.getString("validation.must.be.past")
    
    val MUST_HAVE_AT_LEAST_ONE: String
        get() = bundle.getString("validation.must.have.at.least.one")
    
    val DUPLICATE: String
        get() = bundle.getString("validation.duplicate")
    
    val MUST_BE_BETWEEN_ONE_AND_NINETY_NINE: String
        get() = bundle.getString("validation.must.be.between.one.and.ninety.nine")
}

