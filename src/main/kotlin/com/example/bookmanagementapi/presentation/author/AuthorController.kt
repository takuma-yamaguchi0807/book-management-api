package com.example.bookmanagementapi.presentation.author

import com.example.bookmanagementapi.usecase.author.CreateAuthorRequestDto
import com.example.bookmanagementapi.usecase.author.CreateAuthorResponseDto
import com.example.bookmanagementapi.usecase.author.CreateAuthorUsecase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 著者コントローラー
 */
@RestController
@RequestMapping("/authors")
class AuthorController(
    private val createAuthorUsecase: CreateAuthorUsecase
) {
    @PostMapping
    fun createAuthor(@RequestBody request: CreateAuthorRequest): ResponseEntity<CreateAuthorResponse> {
        val requestDto = CreateAuthorRequestDto(
            name = request.name,
            birthDate = request.birthDate
        )
        val responseDto: CreateAuthorResponseDto = createAuthorUsecase.execute(requestDto)
        val response = CreateAuthorResponse(id = responseDto.id)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}

