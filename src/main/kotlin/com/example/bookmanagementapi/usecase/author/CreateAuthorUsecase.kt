package com.example.bookmanagementapi.usecase.author

import com.example.bookmanagementapi.domain.ValidationResult
import com.example.bookmanagementapi.domain.author.Author
import com.example.bookmanagementapi.domain.author.AuthorName
import com.example.bookmanagementapi.domain.author.AuthorRepository
import com.example.bookmanagementapi.domain.author.BirthDate
import com.example.bookmanagementapi.domain.service.DomainValidationService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 著者登録ユースケース
 */
@Service
class CreateAuthorUsecase(
    private val authorRepository: AuthorRepository
) {
    @Transactional
    fun execute(request: CreateAuthorRequestDto): CreateAuthorResponseDto {
        val nameResult = AuthorName.create(request.name)
        val birthDateResult = BirthDate.create(request.birthDate)
        
        DomainValidationService.validateAll(nameResult, birthDateResult)
        
        val authorName = (nameResult as ValidationResult.Success).value
        val authorBirthDate = (birthDateResult as ValidationResult.Success).value
        
        val author = Author.create(authorName, authorBirthDate)
        return authorRepository.save(author)
    }
}

