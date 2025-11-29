package com.example.bookmanagementapi.usecase.author

import com.example.bookmanagementapi.domain.ValidationResult
import com.example.bookmanagementapi.domain.author.Author
import com.example.bookmanagementapi.domain.author.AuthorId
import com.example.bookmanagementapi.domain.author.AuthorName
import com.example.bookmanagementapi.domain.author.AuthorRepository
import com.example.bookmanagementapi.domain.author.BirthDate
import com.example.bookmanagementapi.domain.exception.ResourceNotFoundException
import com.example.bookmanagementapi.domain.service.DomainValidationService
import com.example.bookmanagementapi.presentation.author.AuthorFields
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 著者更新ユースケース
 */
@Service
class UpdateAuthorUsecase(
    private val authorRepository: AuthorRepository
) {
    @Transactional
    fun execute(id: Long, request: UpdateAuthorRequestDto) {
        // IDのバリデーション
        val authorIdResult = AuthorId.createWithFieldName(id, AuthorFields.ID)
        val nameResult = AuthorName.create(request.name)
        val birthDateResult = BirthDate.create(request.birthDate)

        DomainValidationService.validateAll(authorIdResult, nameResult, birthDateResult)

        val authorId = (authorIdResult as ValidationResult.Success).value
        val authorName = (nameResult as ValidationResult.Success).value
        val authorBirthDate = (birthDateResult as ValidationResult.Success).value

        // 著者の存在チェック
        authorRepository.findById(authorId)
            ?: throw ResourceNotFoundException()

        // 著者を更新
        val updatedAuthor = Author.update(
            id = authorId,
            name = authorName,
            birthDate = authorBirthDate
        )
        authorRepository.update(updatedAuthor)
    }
}

