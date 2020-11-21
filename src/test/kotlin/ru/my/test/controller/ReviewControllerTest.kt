package ru.my.test.controller

import ApiError
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ru.my.test.AbstractIntegrationTest
import ru.my.test.entity.BookRating
import ru.my.test.model.*
import ru.my.test.service.ReviewRepository
import ru.my.test.service.findOrException
import javax.transaction.Transactional


class ReviewControllerTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var reviewRepository: ReviewRepository

    @BeforeEach
    fun beforeEach() {
        reviewRepository.deleteAll()
    }

    @Test
    fun `GET all exist reviews`() {
        val book = modelHelper.createBook()

        val reviewFirst = modelHelper.createReview(book)
        val reviewSecond = modelHelper.createReview(book)

        val response = mvc.get("/reviews")
            .andExpect(status().isOk)
            .andReturn()
            .asObject<List<ReviewView>>()

        response.size.shouldBe(2)
        response.map { it.id }.shouldContainExactly(listOf(reviewFirst.id, reviewSecond.id))
        response.map { it.bookId }.shouldContainExactly(listOf(book.id, book.id))
    }

    @Test
    fun `GET review by id`() {
        val book = modelHelper.createBook()

        val review = modelHelper.createReview(book)

        val response = mvc.get("/reviews/${review.id}")
            .andExpect(status().isOk)
            .andReturn()
            .asObject<ReviewView>()

        // TODO: стоит ли тут использовать equals? Или checkEqualityOfFields?
        response.id.shouldBe(review.id)
        response.text.shouldBe(review.text)
        response.rating.shouldBe(review.rating)
        response.bookId.shouldBe(review.book.id)

        // TODO: стоит ли делать в одном тесте две проверки. Результата и то что записалось в Repository
        reviewRepository.findOrException(review.id).shouldNotBeNull()
    }

    @Test
    fun `GET review by nonexistent ID should return 404`() {
        mvc.get("/reviews/1").andExpect(status().isNotFound)
    }

    @Test
    fun `GET empty array if reviews not exist`() {
        val response = mvc.get("/reviews")
            .andExpect(status().isOk)
            .andReturn()
            .asObject<List<ReviewView>>()

        response.shouldBeEmpty()
    }

    @Test
    fun `POST created new review`() {
        val book = modelHelper.createBook()

        val request = ReviewAddRequest(bookId = book.id, rating = BookRating.GOOD, text = "Review text")

        val response = mvc.post("/reviews", request.asJson())
            .andExpect(status().isOk)
            .andReturn()
            .asObject<ReviewView>()

        response.text.shouldBe(request.text)
        response.rating.shouldBe(request.rating)
        response.bookId.shouldBe(request.bookId)

        reviewRepository.count().shouldBe(1)
        val repReview = reviewRepository.findOrException(response.id)

        repReview.text.shouldBe(request.text)
        repReview.rating.shouldBe(request.rating)
        repReview.book.id.shouldBe(request.bookId)
    }

    @Test
    @Transactional
    fun `POST return 404 if bookId has nonexistent ID`() {
        val request = ReviewAddRequest(bookId = 99, rating = BookRating.GOOD, text = "Review text")

        val response = mvc.post("/reviews", request.asJson())
            .andExpect(status().isNotFound)
            .andReturn()
            .asObject<ApiError>()

        // TODO: вынести все сообщения об ошибках в одно место
        response.detail.shouldBe("Не удалось найти книгу с id: 99")
    }

    @Test
    fun `POST return validation error if request not correct`() {
        val response = mvc.post("/reviews", "{}")
            .andExpect(status().isBadRequest)
            .andReturn()
            .asObject<ApiError>()

        response.title.shouldBe(ApiError.ERROR_MESSAGES_JSON_NOT_VALID)
    }

    @Test
    fun `PUT review by nonexistent ID should return 404`() {
        val request = BookEditRequest("Book new name")

        mvc.put("/reviews/1", request.asJson()).andExpect(status().isNotFound)
    }

    @Test
    fun `PUT review return edited review`() {
        val book = modelHelper.createBook()
        val review = modelHelper.createReview(
            book = book,
            rating = BookRating.VERY_GOOD,
            text = "Review text"
        )

        val request = ReviewEditRequest(rating = BookRating.NORMAL, text = "New text")

        val response = mvc.put("/reviews/${review.id}", request.asJson())
            .andExpect(status().isOk)
            .andReturn()
            .asObject<ReviewView>()

        response.id.shouldBe(review.id)
        response.text.shouldBe(request.text)
        response.rating.shouldBe(request.rating)

        val repReview = reviewRepository.findOrException(review.id)
        repReview.text.shouldBe(request.text)
        repReview.rating.shouldBe(request.rating)
    }

    @Test
    fun `DELETE review by nonexistent ID should return 404`() {
        mvc.delete("/reviews/99").andExpect(status().isNotFound)
    }

    @Test
    fun `DELETE review return 200 if book deleted`() {
        val book = modelHelper.createBook()

        val reviewFirst = modelHelper.createReview(book)
        val reviewSecond = modelHelper.createReview(book)

        mvc.delete("/reviews/${reviewFirst.id}").andExpect(status().isOk)

        val allReviews = reviewRepository.findAll()
        allReviews.size.shouldBe(1)
        allReviews.first().id.shouldBe(reviewSecond.id)
        allReviews.first().text.shouldBe(reviewSecond.text)
        allReviews.first().rating.shouldBe(reviewSecond.rating)
    }
}