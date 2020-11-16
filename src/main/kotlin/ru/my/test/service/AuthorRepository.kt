package ru.my.test.service

import javassist.NotFoundException
import org.springframework.data.jpa.repository.JpaRepository
import ru.my.test.entity.Author

interface AuthorRepository : JpaRepository<Author, Int>

fun AuthorRepository.findOrException(id: Int): Author {
    return this.findById(id).orElseThrow {
        NotFoundException("Не удалось найти автора с id: $id")
    }
}

fun AuthorRepository.findAllByIdOrException(authorIds: List<Int>): List<Author> {
    val authors = this.findAllById(authorIds)

    authorIds.forEach { authorId ->
        if (!authors.any { it.id == authorId }) {
            throw NotFoundException("Не удалось найти автора с id: $authorId")
        }
    }

    return authors
}