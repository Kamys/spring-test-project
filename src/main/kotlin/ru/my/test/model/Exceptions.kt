package ru.my.test.model

enum class ErrorMessages(val text: String) {
    JSON_NOT_VALID("Неправильно сформирован JSON"),
    FAILED_VALIDATION("Запрос не прошёл валидацию")
}

open class ApiError(
    val title: String,
    val detail: String?
)

class ApiValidationError(
    title: String = ErrorMessages.FAILED_VALIDATION.text,
    detail: String?,
    val violations: List<Violation>
) : ApiError(title, detail)

data class Violation(
    val field: String,
    val message: String?
)

class NotFoundException(error: String) : RuntimeException(error)
class BadRequest(error: String) : RuntimeException(error)