package ru.my.test.service

import org.springframework.data.jpa.repository.JpaRepository
import ru.my.test.entity.Contact
import ru.my.test.model.NotFoundException

interface ContactRepository : JpaRepository<Contact, Long>

fun ContactRepository.findOrException(id: Long): Contact {
    return this.findById(id).orElseThrow {
        NotFoundException("Не удалось найти контактные данные с id: $id")
    }
}