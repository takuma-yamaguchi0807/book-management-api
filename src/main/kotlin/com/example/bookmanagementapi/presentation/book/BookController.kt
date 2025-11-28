package com.example.bookmanagementapi.presentation.book

import com.example.bookmanagementapi.presentation.shared.CommonFields
import com.example.bookmanagementapi.usecase.book.CreateBookRequestDto
import com.example.bookmanagementapi.usecase.book.CreateBookResponseDto
import com.example.bookmanagementapi.usecase.book.CreateBookUsecase
import com.example.bookmanagementapi.usecase.book.GetBooksByAuthorRequestDto
import com.example.bookmanagementapi.usecase.book.GetBooksByAuthorUsecase
import com.example.bookmanagementapi.usecase.book.UpdateBookRequestDto
import com.example.bookmanagementapi.usecase.book.UpdateBookUsecase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * 書籍コントローラー
 */
@RestController
@RequestMapping(BookFields.PATH_BASE)
class BookController(
    private val createBookUsecase: CreateBookUsecase,
    private val updateBookUsecase: UpdateBookUsecase,
    private val getBooksByAuthorUsecase: GetBooksByAuthorUsecase
) {
    @GetMapping
    fun getBooks(
        @RequestParam authorId: Long,
        @RequestParam(name = CommonFields.PAGE_NUMBER, defaultValue = "1") pageNumber: Int,
        @RequestParam(name = CommonFields.PAGE_SIZE, defaultValue = "20") pageSize: Int
    ): ResponseEntity<BookListResponse> {
        val requestDto = GetBooksByAuthorRequestDto(
            authorId = authorId,
            pageNumber = pageNumber,
            pageSize = pageSize
        )
        val responseDto = getBooksByAuthorUsecase.execute(requestDto)
        
        val items = responseDto.items.map { item ->
            BookItemResponse(
                id = item.id,
                title = item.title,
                price = item.price,
                published = item.published,
                authors = item.authors.map { author ->
                    AuthorSummaryResponse(
                        id = author.id,
                        name = author.name
                    )
                },
                createdAt = item.createdAt,
                updatedAt = item.updatedAt
            )
        }
        
        val response = BookListResponse(
            items = items,
            pagination = PaginationResponse(
                pageNumber = responseDto.pagination.pageNumber,
                pageSize = responseDto.pagination.pageSize,
                total = responseDto.pagination.total,
                totalPages = responseDto.pagination.totalPages
            )
        )
        
        return ResponseEntity.ok(response)
    }
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

    @PutMapping(BookFields.PATH_ID_SEGMENT)
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
