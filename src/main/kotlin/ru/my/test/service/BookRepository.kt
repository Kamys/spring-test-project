package ru.my.test.service

import org.springframework.data.jpa.repository.JpaRepository
import ru.my.test.entity.Book
import ru.my.test.model.NotFoundException

interface BookRepository : JpaRepository<Book, Int> {
    fun existsByName(name: String): Boolean
}

fun BookRepository.findOrException(id: Int): Book {
    return this.findById(id).orElseThrow {
        NotFoundException("Не удалось найти книгу с id: $id")
    }
}

fun BookRepository.findAllByIdOrException(booksIds: List<Int>): List<Book> {
    val books = this.findAllById(booksIds)

    booksIds.forEach { bookId ->
        if (!books.any { it.id == bookId }) {
            throw NotFoundException("Не удалось найти книгу с id: $bookId")
        }
    }

    return books
}