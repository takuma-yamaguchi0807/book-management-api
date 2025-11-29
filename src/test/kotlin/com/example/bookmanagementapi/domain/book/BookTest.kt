package com.example.bookmanagementapi.domain.book

import com.example.bookmanagementapi.domain.author.AuthorId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Book テスト")
class BookTest {

    private val title = Title.reconstruct("Domain Driven Design")
    private val price = Price.reconstruct(3000)
    private val publishedStatus = PublishedStatus.reconstruct(true)
    private val authorIds = listOf(AuthorId.reconstruct(1L))

    @Nested
    @DisplayName("create")
    inner class Create {
        @Nested
        @DisplayName("正常系")
        inner class Normal {
            @Test
            @DisplayName("新規作成の場合、IDがnullの状態で生成されること")
            fun create() {
                val book = Book.create(title, price, publishedStatus, authorIds)
                assertNull(book.id)
                assertEquals(title, book.title)
                assertEquals(price, book.price)
                assertEquals(publishedStatus, book.publishedStatus)
                assertEquals(authorIds, book.authorIds)
            }
        }
    }

    @Nested
    @DisplayName("update")
    inner class Update {
        @Nested
        @DisplayName("正常系")
        inner class Normal {
            @Test
            @DisplayName("更新の場合、指定したIDを持つインスタンスが生成されること")
            fun update() {
                val id = BookId.reconstruct(1L)
                val book = Book.update(id, title, price, publishedStatus, authorIds)
                assertEquals(id, book.id)
                assertEquals(title, book.title)
                assertEquals(price, book.price)
                assertEquals(publishedStatus, book.publishedStatus)
                assertEquals(authorIds, book.authorIds)
            }
        }
    }

    @Nested
    @DisplayName("reconstruct")
    inner class Reconstruct {
        @Nested
        @DisplayName("正常系")
        inner class Normal {
            @Test
            @DisplayName("再構築の場合、全てのフィールドがセットされたインスタンスが生成されること")
            fun reconstruct() {
                val id = BookId.reconstruct(1L)
                val book = Book.reconstruct(id, title, price, publishedStatus, authorIds)
                assertEquals(id, book.id)
                assertEquals(title, book.title)
                assertEquals(price, book.price)
                assertEquals(publishedStatus, book.publishedStatus)
                assertEquals(authorIds, book.authorIds)
            }
        }
    }
}
