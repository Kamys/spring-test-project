package ru.my.test.controller

import org.springframework.web.bind.annotation.*
import ru.my.test.model.ReviewAddRequest
import ru.my.test.model.ReviewEditRequest
import ru.my.test.model.ReviewView
import ru.my.test.service.ReviewService
import javax.validation.Valid

@RestController
@RequestMapping("/reviews")
class ReviewController(
    private val reviewService: ReviewService
) {

    @GetMapping
    fun getReviews(): Any {
        return reviewService.getAll()
    }

    @GetMapping("/{reviewId}")
    fun getReviews(@PathVariable("reviewId") reviewId: Int): Any {
        return reviewService.getById(reviewId)
    }

    @PostMapping
    fun createReview(@Valid @RequestBody request: ReviewAddRequest): ReviewView {
        return reviewService.add(request)
    }

    @PutMapping("/{reviewId}")
    fun editReview(
        @PathVariable("reviewId") reviewId: Int,
        @RequestBody @Valid request: ReviewEditRequest
    ): ReviewView {
        return reviewService.edit(reviewId, request)
    }

    @DeleteMapping("/{reviewId}")
    fun deleteReview(
        @PathVariable("reviewId") reviewId: Int
    ) {
        reviewService.delete(reviewId)
    }
}