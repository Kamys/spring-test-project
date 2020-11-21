package ru.my.test.service

import javassist.NotFoundException
import org.springframework.data.jpa.repository.JpaRepository
import ru.my.test.entity.Book
import ru.my.test.entity.Review

interface ReviewRepository : JpaRepository<Review, Int>

fun ReviewRepository.findOrException(id: Int): Review {
    return this.findById(id).orElseThrow {
        NotFoundException("Не удалось найти отзыв с id: $id")
    }
}