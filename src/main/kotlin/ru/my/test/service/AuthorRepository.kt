package ru.my.test.service

import javassist.NotFoundException
import org.springframework.data.jpa.repository.JpaRepository
import ru.my.test.entity.Author
import ru.my.test.entity.Book
import ru.my.test.model.BookEditRequest

interface AuthorRepository : JpaRepository<Author, Int>

fun AuthorRepository.findOrException(id: Int): Author {
    return this.findById(id).orElseThrow {
        NotFoundException("Не удалось найти автора с id: $id")
    }
}