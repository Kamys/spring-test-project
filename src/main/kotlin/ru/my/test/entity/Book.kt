package ru.my.test.entity

import javax.persistence.*

@Entity
@Table(name = "books")
class Book(
    id: Long = 0,
    var name: String,
    @ManyToMany(cascade = [CascadeType.PERSIST])
    @JoinTable(
        name = "author_book",
        joinColumns = [JoinColumn(name = "book_id")],
        inverseJoinColumns = [JoinColumn(name = "author_id")],
    )
    var authors: MutableList<Author> = mutableListOf(),
    @OneToMany(mappedBy = "book", cascade = [CascadeType.ALL], orphanRemoval = true)
    var reviews: MutableList<Review> = mutableListOf(),
) : BaseEntity(id) {

    fun addAuthor(author: Author) {
        authors.add(author)
    }

    fun addReview(review: Review) {
        reviews.add(review)
        review.book = this
    }
}