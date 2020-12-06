package ru.my.test.service

import org.springframework.data.jpa.repository.JpaRepository
import ru.my.test.entity.Contact
import ru.my.test.model.NotFoundException
import java.util.*

interface ContactRepository : JpaRepository<Contact, Long> {
    fun findByAuthorId(authorId: Long): Optional<Contact>

    @JvmDefault
    fun findOrException(id: Long): Contact {
        return this.findById(id).orElseThrow {
            NotFoundException("Не удалось найти контактные данные с id: $id")
        }
    }

    @JvmDefault
    fun findByAuthorIdOrException(authorId: Long): Contact {
        return this.findByAuthorId(authorId).orElseThrow {
            NotFoundException("Не удалось найти контактные данные у автора с id: $authorId")
        }
    }
}

