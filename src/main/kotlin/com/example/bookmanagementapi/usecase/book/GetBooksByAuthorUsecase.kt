package com.example.bookmanagementapi.usecase.book

import com.example.bookmanagementapi.domain.ValidationResult
import com.example.bookmanagementapi.domain.author.AuthorId
import com.example.bookmanagementapi.domain.pagination.PageNumber
import com.example.bookmanagementapi.domain.pagination.PageSize
import com.example.bookmanagementapi.domain.pagination.Pagination
import com.example.bookmanagementapi.domain.service.DomainValidationService
import com.example.bookmanagementapi.domain.queryservice.BookQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 著者に紐づく書籍一覧取得ユースケース
 */
@Service
class GetBooksByAuthorUsecase(
    private val bookQueryRepository: BookQueryRepository
) {
    @Transactional(readOnly = true)
    fun execute(request: GetBooksByAuthorRequestDto): GetBooksByAuthorResponseDto {
        // 著者IDのバリデーション
        val authorIdResult = AuthorId.create(request.authorId)
        // ページ番号のバリデーション
        val pageNumberResult = PageNumber.create(request.pageNumber)
        // ページサイズのバリデーション
        val pageSizeResult = PageSize.create(request.pageSize)
        
        DomainValidationService.validateAll(authorIdResult, pageNumberResult, pageSizeResult)
        
        val authorId = (authorIdResult as ValidationResult.Success).value
        val pageNumber = (pageNumberResult as ValidationResult.Success).value
        val pageSize = (pageSizeResult as ValidationResult.Success).value
        
        // ページネーション集約ルートを作成
        val pagination = Pagination.create(pageNumber, pageSize)
        
        // 総件数を取得
        val total = bookQueryRepository.countByAuthorId(authorId)
        
        // 書籍一覧を取得（著者が存在しない場合も空の配列を返す）
        // JOINクエリにより書籍と著者情報を一度に取得しているため、追加のクエリは不要
        val queryResult = bookQueryRepository.findByAuthorId(
            authorId = authorId,
            pagination = pagination
        )
        
        // QueryServiceのDTOをそのままUsecaseのDTOに変換
        val items = queryResult.books.map { bookWithAuthors ->
            BookItemDto(
                id = bookWithAuthors.id,
                title = bookWithAuthors.title,
                price = bookWithAuthors.price,
                published = bookWithAuthors.published,
                authors = bookWithAuthors.authors.map { author ->
                    AuthorSummaryDto(
                        id = author.id,
                        name = author.name
                    )
                },
                createdAt = bookWithAuthors.createdAt,
                updatedAt = bookWithAuthors.updatedAt
            )
        }
        
        val totalPages = pagination.calculateTotalPages(total)
        
        return GetBooksByAuthorResponseDto(
            items = items,
            pagination = PaginationDto(
                pageNumber = pageNumber.value,
                pageSize = pageSize.value,
                total = total,
                totalPages = totalPages
            )
        )
    }
}

