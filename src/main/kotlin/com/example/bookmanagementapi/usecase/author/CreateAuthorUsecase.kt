package com.example.bookmanagementapi.usecase.author

import com.example.bookmanagementapi.domain.author.Author
import com.example.bookmanagementapi.domain.author.AuthorRepository
import org.springframework.stereotype.Service

/**
 * 著者登録ユースケース
 */
@Service
class CreateAuthorUsecase(
    private val authorRepository: AuthorRepository
) {
    fun execute(request: CreateAuthorRequestDto): CreateAuthorResponseDto {
        val author = Author.createFromRequest(request.name, request.birthDate)
        val savedAuthor = authorRepository.save(author)
        return CreateAuthorResponseDto(id = savedAuthor.id!!)
    }
}

