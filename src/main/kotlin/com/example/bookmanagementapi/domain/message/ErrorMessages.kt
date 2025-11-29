package com.example.bookmanagementapi.domain.message

import java.util.ResourceBundle

/**
 * エラーメッセージ
 * ResourceBundleを使用してプロパティファイルからメッセージを読み込む
 */
object ErrorMessages {
    private val bundle = ResourceBundle.getBundle("messages")
    
    val RESOURCE_NOT_FOUND: String
        get() = bundle.getString("error.resource.not.found")
    
    fun invalidPathParameter(parameterName: String): String {
        return bundle.getString("error.invalid.path.parameter").replace("{0}", parameterName)
    }
    
    val BUSINESS_RULE_VIOLATION_PUBLISHED_TO_UNPUBLISHED: String
        get() = bundle.getString("error.business.rule.violation.published.to.unpublished")
}

