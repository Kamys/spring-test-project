package ru.my.test.controller

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ru.my.test.AbstractIntegrationTest
import ru.my.test.entity.BookRating
import ru.my.test.entity.Review
import ru.my.test.model.ReviewAddRequest
import ru.my.test.model.ReviewEditRequest
import ru.my.test.model.ReviewView
import ru.my.test.service.AuthorRepository
import ru.my.test.service.BookRepository
import ru.my.test.service.ReviewRepository
import ru.my.test.service.findOrException


class ReviewControllerTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @Autowired
    private lateinit var reviewRepository: ReviewRepository

    @BeforeEach
    fun beforeEach() {
        bookRepository.deleteAll()
        authorRepository.deleteAll()
        reviewRepository.deleteAll()
    }

    @Test
    fun `GET all reviews by book`() {
        val book = transactional {
            modelHelper.createBook().apply {
                addReview(Review(text = "Review 1", rating = BookRating.GOOD))
                addReview(Review(text = "Review 2", rating = BookRating.NORMAL))
                bookRepository.save(this)
            }
        }


        val response = mvc.get("/books/${book.id}/reviews")
            .andExpect(status().isOk)
            .andReturn()
            .asObject<List<ReviewView>>()

        response.size.shouldBe(2)
        response.map { it.id }.shouldContainExactly(book.reviews.map { it.id })
    }

    @Test
    fun `GET empty array if reviews not exist`() {
        val book = modelHelper.createBook()

        val response = mvc.get("/books/${book.id}/reviews")
            .andExpect(status().isOk)
            .andReturn()
            .asObject<List<ReviewView>>()

        response.shouldBeEmpty()
    }

    @Test
    fun `POST created new review in book`() {
        val book = modelHelper.createBook()

        val bookRequest = ReviewAddRequest("Review 1", BookRating.NORMAL)

        val response = mvc.post("/books/${book.id}/reviews", bookRequest.asJson())
            .andExpect(status().isCreated)
            .andReturn()
            .asObject<ReviewView>()

        response.text.shouldBe(bookRequest.text)
        response.rating.shouldBe(bookRequest.rating)

        transactional {
            reviewRepository.count().shouldBe(1)
            reviewRepository.findOrException(response.id).also {
                it.text.shouldBe(bookRequest.text)
                it.rating.shouldBe(bookRequest.rating)
            }
        }
    }

    @Test
    fun `PUT review return edited review`() {
        val book = transactional {
            modelHelper.createBook().apply {
                addReview(Review(text = "Review 1", rating = BookRating.GOOD))
                addReview(Review(text = "Review 2", rating = BookRating.NORMAL))
                bookRepository.save(this)
            }
        }

        val request = ReviewEditRequest("Review new name", BookRating.BAD)
        val editedReview = book.reviews[0]
        val notEditReview = book.reviews[1]

        val response = mvc.put("/books/${book.id}/reviews/${editedReview.id}", request.asJson())
            .andExpect(status().isOk)
            .andReturn()
            .asObject<ReviewView>()

        response.text.shouldBe(request.text)
        response.rating.shouldBe(request.rating)

        transactional {
            reviewRepository.count().shouldBe(2)
            reviewRepository.findOrException(response.id).also {
                it.text.shouldBe(request.text)
                it.rating.shouldBe(request.rating)
            }
            reviewRepository.findOrException(notEditReview.id).also {
                it.text.shouldBe(notEditReview.text)
                it.rating.shouldBe(notEditReview.rating)
            }
        }
    }

    @Test
    fun `DELETE return 200 if review deleted`() {
        val book = transactional {
            modelHelper.createBook().apply {
                addReview(Review(text = "Review 1", rating = BookRating.GOOD))
                addReview(Review(text = "Review 2", rating = BookRating.NORMAL))
                bookRepository.save(this)
            }
        }

        val reviewForDelete = book.reviews[0]

        mvc.delete("/books/${book.id}/reviews/${reviewForDelete.id}").andExpect(status().isNoContent)

        transactional {
            reviewRepository.count().shouldBe(1)
        }
    }
}