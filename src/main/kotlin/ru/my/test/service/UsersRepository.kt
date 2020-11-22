package ru.my.test.service

import javassist.NotFoundException
import org.springframework.data.jpa.repository.JpaRepository
import ru.my.test.entity.Book
import ru.my.test.entity.Review
import ru.my.test.entity.User

interface UserRepository : JpaRepository<User, Int>

fun UserRepository.findOrException(id: Int): User {
    return this.findById(id).orElseThrow {
        NotFoundException("Не удалось найти пользователя с id: $id")
    }
}