import com.fasterxml.jackson.annotation.JsonValue
import org.springframework.http.HttpStatus

enum class ErrorType {
    SERVER_ERROR,
    VALIDATION_ERROR,
    PROCESS_ERROR;

    @JsonValue
    fun toJsonValue(): String {
        return this.toString().toLowerCase()
    }
}

open class ApiError(
    val type: ErrorType,
    val status: Int,
    val title: String,
    val detail: String?
)

class ApiValidationError(
    type: ErrorType = ErrorType.VALIDATION_ERROR,
    status: Int = HttpStatus.BAD_REQUEST.value(),
    title: String = "Запрос не прошёл валидацию",
    detail: String?,
    val violations: List<Violation>
) : ApiError(type, status, title, detail)

data class Violation(
    val field: String,
    val message: String?
)