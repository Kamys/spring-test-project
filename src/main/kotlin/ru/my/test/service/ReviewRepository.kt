package ru.my.test.service

import org.springframework.data.jpa.repository.JpaRepository
import ru.my.test.entity.Review
import ru.my.test.model.NotFoundException

interface ReviewRepository : JpaRepository<Review, Long> {
    fun findByBookId(bookId: Long): List<Review>
}

fun ReviewRepository.findOrException(id: Long): Review {
    return this.findById(id).orElseThrow {
        NotFoundException("Не удалось найти отзыв с id: $id")
    }
}