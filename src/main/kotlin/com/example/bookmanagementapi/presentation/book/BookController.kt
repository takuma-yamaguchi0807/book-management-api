package com.example.bookmanagementapi.presentation.book

import com.example.bookmanagementapi.usecase.book.CreateBookRequestDto
import com.example.bookmanagementapi.usecase.book.CreateBookResponseDto
import com.example.bookmanagementapi.usecase.book.CreateBookUsecase
import com.example.bookmanagementapi.usecase.book.UpdateBookRequestDto
import com.example.bookmanagementapi.usecase.book.UpdateBookUsecase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 書籍コントローラー
 */
@RestController
@RequestMapping("/books")
class BookController(
    private val createBookUsecase: CreateBookUsecase,
    private val updateBookUsecase: UpdateBookUsecase
) {
    @PostMapping
    fun createBook(@RequestBody request: CreateBookRequest): ResponseEntity<CreateBookResponse> {
        val requestDto = CreateBookRequestDto(
            title = request.title,
            price = request.price,
            authorIds = request.authorIds,
            published = request.published
        )
        val responseDto: CreateBookResponseDto = createBookUsecase.execute(requestDto)
        val response = CreateBookResponse(id = responseDto.value)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/{id}")
    fun updateBook(
        @PathVariable id: Long,
        @RequestBody request: UpdateBookRequest
    ): ResponseEntity<Unit> {
        val requestDto = UpdateBookRequestDto(
            title = request.title,
            price = request.price,
            authorIds = request.authorIds,
            published = request.published
        )
        updateBookUsecase.execute(id, requestDto)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}

