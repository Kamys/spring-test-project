package ru.my.test.service

import ru.my.test.model.NotFoundException
import org.springframework.data.jpa.repository.JpaRepository
import ru.my.test.entity.Book
import ru.my.test.entity.Review

interface ReviewRepository : JpaRepository<Review, Int> {
    fun findByBookId(bookId: Int): List<Review>
}

fun ReviewRepository.findOrException(id: Int): Review {
    return this.findById(id).orElseThrow {
        NotFoundException("Не удалось найти отзыв с id: $id")
    }
}