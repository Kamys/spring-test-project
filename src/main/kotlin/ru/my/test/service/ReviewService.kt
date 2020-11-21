package ru.my.test.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.my.test.entity.Review
import ru.my.test.model.ReviewAddRequest
import ru.my.test.model.ReviewEditRequest
import ru.my.test.model.ReviewView
import ru.my.test.service.mapper.toView

@Service
@Transactional
class ReviewService(
    private val reviewRepository: ReviewRepository,
) {

    @Autowired
    private lateinit var bookService: BookService

    fun getAll(): List<ReviewView> {
        return reviewRepository.findAll().map { it.toView() }
    }

    fun add(request: ReviewAddRequest): ReviewView {
        val attachBook = bookService.getModelById(request.bookId)
        val review = Review(text = request.text ?: "", rating = request.rating, book = attachBook)

        return reviewRepository.save(review).toView()
    }

    fun edit(reviewId: Int, request: ReviewEditRequest): ReviewView {
        val review = reviewRepository.findOrException(reviewId)
        if (!request.text.isNullOrEmpty()) {
            review.text = request.text
        }
        if (request.rating !== null) {
            review.rating = request.rating
        }
        return reviewRepository.save(review).toView()
    }

    fun delete(reviewId: Int) {
        val review = reviewRepository.findOrException(reviewId)
        return reviewRepository.delete(review)
    }

    fun getById(reviewId: Int): ReviewView {
        return reviewRepository.findOrException(reviewId).toView()
    }
}