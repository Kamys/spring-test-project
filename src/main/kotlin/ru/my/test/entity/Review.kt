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
    id: Long = 0,
    var text: String,
    @Enumerated(EnumType.STRING)
    var rating: BookRating,
) : BaseEntity(id) {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    lateinit var book: Book
}