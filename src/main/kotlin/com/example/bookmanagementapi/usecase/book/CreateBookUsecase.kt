package com.example.bookmanagementapi.usecase.book

import com.example.bookmanagementapi.domain.ValidationResult
import com.example.bookmanagementapi.domain.author.AuthorId
import com.example.bookmanagementapi.domain.author.AuthorRepository
import com.example.bookmanagementapi.domain.book.Book
import com.example.bookmanagementapi.domain.book.BookAuthorRepository
import com.example.bookmanagementapi.domain.book.BookRepository
import com.example.bookmanagementapi.domain.book.Price
import com.example.bookmanagementapi.domain.book.PublishedStatus
import com.example.bookmanagementapi.domain.book.Title
import com.example.bookmanagementapi.domain.exception.BusinessRuleViolationException
import com.example.bookmanagementapi.domain.exception.DomainValidationException
import com.example.bookmanagementapi.domain.exception.ResourceNotFoundException
import com.example.bookmanagementapi.domain.exception.ValidationError
import com.example.bookmanagementapi.domain.message.ErrorMessages
import com.example.bookmanagementapi.domain.message.ValidationMessages
import com.example.bookmanagementapi.domain.service.DomainValidationService
import com.example.bookmanagementapi.presentation.book.BookFields
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 書籍登録ユースケース
 */
@Service
class CreateBookUsecase(
    private val bookRepository: BookRepository,
    private val bookAuthorRepository: BookAuthorRepository,
    private val authorRepository: AuthorRepository
) {
    @Transactional
    fun execute(request: CreateBookRequestDto): CreateBookResponseDto {
        // 値オブジェクトのバリデーション
        val titleResult = Title.create(request.title)
        val priceResult = Price.create(request.price)
        val publishedStatusResult = PublishedStatus.create(request.published)
        
        // 著者IDリストのバリデーション
        val authorIdsValidationResult = validateAuthorIds(request.authorIds)
        
        DomainValidationService.validateAll(titleResult, priceResult, publishedStatusResult, authorIdsValidationResult)
        
        val title = (titleResult as ValidationResult.Success).value
        val price = (priceResult as ValidationResult.Success).value
        val publishedStatus = (publishedStatusResult as ValidationResult.Success).value
        val authorIds = (authorIdsValidationResult as ValidationResult.Success).value
        
        // 著者IDの存在チェック
        validateAuthorIdsExist(authorIds)
        
        // 書籍を作成して保存
        val book = Book.create(
            title = title,
            price = price,
            publishedStatus = publishedStatus,
            authorIds = authorIds
        )
        
        val bookId = bookRepository.save(book)
        
        // books_authorsテーブルに保存
        bookAuthorRepository.save(bookId, authorIds)
        
        return bookId
    }
    
    /**
     * 著者IDリストのバリデーション
     * - null/空チェック
     * - 各要素をAuthorIdに変換
     * - 重複チェック
     */
    private fun validateAuthorIds(authorIds: List<Long>?): ValidationResult<List<AuthorId>> {
        if (authorIds == null || authorIds.isEmpty()) {
            return ValidationResult.Failure(ValidationError(BookFields.AUTHOR_IDS, ValidationMessages.MUST_HAVE_AT_LEAST_ONE))
        }
        
        // 重複チェック
        if (authorIds.size != authorIds.toSet().size) {
            return ValidationResult.Failure(ValidationError(BookFields.AUTHOR_IDS, ValidationMessages.DUPLICATE))
        }
        
        // 各要素をAuthorIdに変換
        val validatedAuthorIds = mutableListOf<AuthorId>()
        val errors = mutableListOf<ValidationError>()
        
        authorIds.forEachIndexed { index, authorIdValue ->
            val authorIdResult = AuthorId.createWithFieldName(authorIdValue, BookFields.AUTHOR_IDS)
            when (authorIdResult) {
                is ValidationResult.Success -> {
                    validatedAuthorIds.add(authorIdResult.value)
                }
                is ValidationResult.Failure -> {
                    authorIdResult.errors.forEach { error ->
                        errors.add(ValidationError("${BookFields.AUTHOR_IDS}[$index]", error.message))
                    }
                }
            }
        }
        
        if (errors.isNotEmpty()) {
            return ValidationResult.Failure(errors)
        }
        
        return ValidationResult.Success(validatedAuthorIds)
    }
    
    /**
     * 著者IDの存在チェック
     * IN句で一括取得し、存在しないIDだけをエラーとして返す
     */
    private fun validateAuthorIdsExist(authorIds: List<AuthorId>) {
        val foundAuthors = authorRepository.findByIds(authorIds)
        val foundAuthorIds = foundAuthors.map { it.id!!.value }.toSet()
        
        val authorIdsErrors = mutableMapOf<String, String>()
        authorIds.forEachIndexed { index, authorId ->
            if (!foundAuthorIds.contains(authorId.value)) {
                authorIdsErrors[index.toString()] = ErrorMessages.RESOURCE_NOT_FOUND
            }
        }
        
        if (authorIdsErrors.isNotEmpty()) {
            val errors = mapOf<String, Any>(BookFields.AUTHOR_IDS to authorIdsErrors.toMap())
            throw BusinessRuleViolationException(errors)
        }
    }
}

