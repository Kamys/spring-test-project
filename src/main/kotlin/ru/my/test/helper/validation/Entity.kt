package ru.my.test.helper.validation

import org.springframework.beans.factory.annotation.Autowired
import ru.my.test.service.AuthorService
import ru.my.test.service.BookService
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import kotlin.reflect.KClass


@Constraint(validatedBy = [UniqueBookNameValidator::class])
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FIELD
)
annotation class UniqueBookName(
    val message: String = "Имя уже используется",
    val groups: Array<KClass<out Any>> = [],
    val payload: Array<KClass<out Any>> = []
)

class UniqueBookNameValidator(
    @Autowired
    private val bookService: BookService
) : ConstraintValidator<UniqueBookName, String?> {

    override fun isValid(name: String?, context: ConstraintValidatorContext?): Boolean {
        if (name.isNullOrEmpty()) {
            return true
        }
        return !bookService.existsByName(name)
    }
}