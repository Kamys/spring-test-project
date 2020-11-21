package ru.my.test.service.mapper

import ru.my.test.entity.Book
import ru.my.test.entity.Review
import ru.my.test.model.BookView
import ru.my.test.model.ReviewView

fun Book.toView(): BookView {
    return BookView(this.id, this.name, this.authors.map { it.id }, this.reviews.map { it.toView() })
}

fun Review.toView(): ReviewView {
    return ReviewView(this.id, this.text, this.rating, this.book.id)
}