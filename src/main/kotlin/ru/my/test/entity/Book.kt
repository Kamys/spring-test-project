package ru.my.test.entity

import javax.persistence.*

@Entity
@Table(name = "books")
class Book(
    id: Long = 0,
    var name: String,
    @ManyToMany
    @JoinTable(
        name = "author_book",
        joinColumns = [JoinColumn(name = "book_id")],
        inverseJoinColumns = [JoinColumn(name = "author_id")]
    )
    var authors: List<Author> = emptyList(),
    @OneToMany(mappedBy="book", cascade = [CascadeType.ALL], orphanRemoval = true)
    var reviews: MutableList<Review> = mutableListOf(),
): BaseEntity(id) {

    fun addReview(review: Review) {
        reviews.add(review)
        review.book = this
    }
}