package com.example.bookmanagementapi.domain.service

import com.example.bookmanagementapi.domain.ValidationResult
import com.example.bookmanagementapi.domain.exception.DomainValidationException

/**
 * ドメイン層のバリデーションサービス
 * 複数の値オブジェクトのバリデーションを一括で実行し、全エラーを集約して返す
 * インタンス化不要なので、object で定義する
 */
object DomainValidationService {
    /**
     * 複数のバリデーション結果を一括で検証し、エラーがあれば例外をスローする
     *
     * @param results バリデーション結果の可変長引数
     * @throws DomainValidationException バリデーションエラーがある場合（全フィールドのエラーを一括で返す）
     */
    fun validateAll(vararg results: ValidationResult<*>) {
        val errors = mutableMapOf<String, String>()
        
        results.forEach { result ->
            when (result) {
                is ValidationResult.Success -> {
                    // 成功時は何もしない
                }
                is ValidationResult.Failure -> {
                    errors[result.error.field] = result.error.message
                }
            }
        }
        
        if (errors.isNotEmpty()) {
            throw DomainValidationException(errors)
        }
    }
}

