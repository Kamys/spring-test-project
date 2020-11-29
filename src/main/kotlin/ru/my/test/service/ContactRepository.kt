package ru.my.test.service

import ru.my.test.model.NotFoundException
import org.springframework.data.jpa.repository.JpaRepository
import ru.my.test.entity.Contact
import java.util.*

interface ContactRepository : JpaRepository<Contact, Long>

fun ContactRepository.findOrException(id: Long): Contact {
    return this.findById(id).orElseThrow {
        NotFoundException("Не удалось найти контактные данные с id: $id")
    }
}