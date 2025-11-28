package com.example.bookmanagementapi.presentation.author

import com.example.bookmanagementapi.usecase.author.CreateAuthorRequestDto
import com.example.bookmanagementapi.usecase.author.CreateAuthorResponseDto
import com.example.bookmanagementapi.usecase.author.CreateAuthorUsecase
import com.example.bookmanagementapi.usecase.author.UpdateAuthorRequestDto
import com.example.bookmanagementapi.usecase.author.UpdateAuthorUsecase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 著者コントローラー
 */
@RestController
@RequestMapping(AuthorFields.PATH_BASE)
class AuthorController(
    private val createAuthorUsecase: CreateAuthorUsecase,
    private val updateAuthorUsecase: UpdateAuthorUsecase
) {
    @PostMapping
    fun createAuthor(@RequestBody request: CreateAuthorRequest): ResponseEntity<CreateAuthorResponse> {
        val requestDto = CreateAuthorRequestDto(
            name = request.name,
            birthDate = request.birthDate
        )
        val responseDto: CreateAuthorResponseDto = createAuthorUsecase.execute(requestDto)
        val response = CreateAuthorResponse(id = responseDto.value)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping(AuthorFields.PATH_ID_SEGMENT)
    fun updateAuthor(
        @PathVariable id: Long,
        @RequestBody request: UpdateAuthorRequest
    ): ResponseEntity<Unit> {
        val requestDto = UpdateAuthorRequestDto(
            name = request.name,
            birthDate = request.birthDate
        )
        updateAuthorUsecase.execute(id, requestDto)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
