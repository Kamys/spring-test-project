package ru.my.test.service

import org.springframework.data.jpa.repository.JpaRepository
import ru.my.test.entity.Book
import ru.my.test.model.NotFoundException

interface BookRepository : JpaRepository<Book, Long> {
    fun existsByName(name: String): Boolean

    @JvmDefault
    fun findOrException(id: Long): Book {
        return this.findById(id).orElseThrow {
            NotFoundException("Не удалось найти книгу с id: $id")
        }
    }

    @JvmDefault
    fun findAllByIdOrException(booksIds: List<Long>): List<Book> {
        val books = this.findAllById(booksIds)

        booksIds.forEach { bookId ->
            if (!books.any { it.id == bookId }) {
                throw NotFoundException("Не удалось найти книгу с id: $bookId")
            }
        }

        return books
    }
}