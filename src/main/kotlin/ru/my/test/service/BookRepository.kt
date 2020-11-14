package ru.my.test.service

import javassist.NotFoundException
import org.springframework.data.jpa.repository.JpaRepository
import ru.my.test.entity.Book

interface BookRepository : JpaRepository<Book, Int>

fun BookRepository.findOrException(id: Int): Book {
    return this.findById(id).orElseThrow {
        NotFoundException("Не удалось найти книгу с id: $id")
    }
}