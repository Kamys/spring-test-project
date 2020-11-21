package ru.my.test.entity

import javax.persistence.*

enum class BookRating {
    VERY_BAD,
    BAD,
    NORMAL,
    GOOD,
    VERY_GOOD,
}

@Entity
@Table(name = "reviews")
class Review(
    id: Int = 0,
    var text: String,
    var rating: BookRating,
    @ManyToOne
    @JoinColumn(name = "book_id")
    var book: Book,
) : BaseEntity(id)