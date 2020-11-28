package ru.my.test.service

import ru.my.test.model.NotFoundException
import org.springframework.data.jpa.repository.JpaRepository
import ru.my.test.entity.Contact
import java.util.*

interface ContactRepository : JpaRepository<Contact, Int>

fun ContactRepository.findOrException(id: Int): Contact {
    return this.findById(id).orElseThrow {
        NotFoundException("Не удалось найти контактные данные с id: $id")
    }
}