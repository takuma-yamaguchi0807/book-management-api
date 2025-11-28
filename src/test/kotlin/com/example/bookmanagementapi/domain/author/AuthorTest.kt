package com.example.bookmanagementapi.domain.author

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

@DisplayName("Author テスト")
class AuthorTest {

    private val name = AuthorName.reconstruct("John Doe")
    private val birthDate = BirthDate.reconstruct(LocalDate.of(1990, 1, 1))

    @Nested
    @DisplayName("create")
    inner class Create {
        @Nested
        @DisplayName("正常系")
        inner class Normal {
            @Test
            @DisplayName("新規作成の場合、IDがnullの状態で生成されること")
            fun create() {
                val author = Author.create(name, birthDate)
                assertNull(author.id)
                assertEquals(name, author.name)
                assertEquals(birthDate, author.birthDate)
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
                val id = AuthorId.reconstruct(1L)
                val author = Author.update(id, name, birthDate)
                assertEquals(id, author.id)
                assertEquals(name, author.name)
                assertEquals(birthDate, author.birthDate)
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
                val id = AuthorId.reconstruct(1L)
                val author = Author.reconstruct(id, name, birthDate)
                assertEquals(id, author.id)
                assertEquals(name, author.name)
                assertEquals(birthDate, author.birthDate)
            }
        }
    }
}
