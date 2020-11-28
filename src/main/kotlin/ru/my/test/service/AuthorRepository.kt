package ru.my.test.service

import ru.my.test.model.NotFoundException
import org.springframework.data.jpa.repository.JpaRepository
import ru.my.test.entity.Author
import ru.my.test.entity.Contact
import java.util.*

interface AuthorRepository : JpaRepository<Author, Int> {
    fun findByContact(contact: Contact): Optional<Author>
}

fun AuthorRepository.findByContactOrException(contact: Contact): Author {
    return this.findByContact(contact).orElseThrow {
        NotFoundException("Не удалось найти автора с Contact: $contact")
    }
}

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