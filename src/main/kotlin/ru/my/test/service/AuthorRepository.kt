package ru.my.test.service

import org.springframework.data.jpa.repository.JpaRepository
import ru.my.test.entity.Author
import ru.my.test.entity.Contact
import ru.my.test.model.NotFoundException
import java.util.*

interface AuthorRepository : JpaRepository<Author, Long> {
    @JvmDefault
    fun findOrException(id: Long): Author {
        return this.findById(id).orElseThrow {
            NotFoundException("Не удалось найти автора с id: $id")
        }
    }

    @JvmDefault
    fun findAllByIdOrException(authorIds: List<Long>): List<Author> {
        val authors = this.findAllById(authorIds)

        authorIds.forEach { authorId ->
            if (!authors.any { it.id == authorId }) {
                throw NotFoundException("Не удалось найти автора с id: $authorId")
            }
        }

        return authors
    }
}



